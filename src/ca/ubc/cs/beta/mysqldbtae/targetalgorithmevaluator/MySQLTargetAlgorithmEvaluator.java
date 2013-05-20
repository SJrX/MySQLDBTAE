package ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aclib.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.RunResult;
import ca.ubc.cs.beta.aclib.exceptions.TargetAlgorithmAbortException;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.currentstatus.CurrentRunStatusObserver;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.deferred.AbstractDeferredTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.deferred.TAECallback;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.exceptions.TAEShutdownException;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.RunToken;

public class MySQLTargetAlgorithmEvaluator extends AbstractDeferredTargetAlgorithmEvaluator {


	private final MySQLPersistenceClient persistence;
	private final Logger log = LoggerFactory.getLogger(MySQLTargetAlgorithmEvaluator.class);
	private final ExecutorService requestWatcher = Executors.newCachedThreadPool();
	

	public MySQLTargetAlgorithmEvaluator(AlgorithmExecutionConfig execConfig, MySQLPersistenceClient persistence) {
		super(execConfig);
		this.persistence = persistence;
	}


	private AtomicBoolean shutdownRequested = new AtomicBoolean(false);
	@Override
	public void notifyShutdown() {
		
		shutdownRequested.set(true);
		requestWatcher.shutdown();
		persistence.shutdown();
		try {
			log.info("MySQL TAE Shutdown in Progress");
			requestWatcher.awaitTermination(24, TimeUnit.DAYS);
			log.info("MySQL TAE Shutdown Complete");
		} catch(InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
		requestWatcher.shutdownNow();
	}

	@Override
	public void evaluateRunsAsync(RunConfig runConfig, TAECallback handler) {
		evaluateRunsAsync(Collections.singletonList(runConfig), handler);
	}

	
	@Override
	@SuppressWarnings("unchecked")
	public void evaluateRunsAsync(List<RunConfig> runConfigs,
			TAECallback handler) {
				evaluateRunsAsync(runConfigs, handler, null);
			}

	@Override
	@SuppressWarnings("unchecked")
	public void evaluateRunsAsync(List<RunConfig> runConfigs,
			TAECallback handler, CurrentRunStatusObserver obs) {
		
		if(runConfigs.size() == 0)
		{
			handler.onSuccess(Collections.EMPTY_LIST);
		}
		RunToken token = persistence.enqueueRunConfigs(runConfigs,obs);
		
		MySQLRequestWatcher mysqlWatcher = new MySQLRequestWatcher(token, handler);
		
		requestWatcher.execute(mysqlWatcher);

	}

	@Override
	public boolean isRunFinal() {
		return true;
	}

	@Override
	public boolean areRunsPersisted() {
		return true;
	}

	class MySQLRequestWatcher implements Runnable
	{
		private final RunToken token;
		private final TAECallback handler; 
		
		public MySQLRequestWatcher(RunToken token, TAECallback handler)
		{
			this.token = token;
			this.handler = handler;
		}
		
		@Override
		public void run() {
	
			List<AlgorithmRun> runs = null;
			try {
				while((runs = persistence.pollRunResults(token)) == null)
				{
					
					if(shutdownRequested.get())
					{
						return;
					}
					try {
						Thread.sleep(2000);
					} catch(InterruptedException e)
					{
						Thread.currentThread().interrupt();
						//We were interrupted, we will abort
						handler.onFailure(new TAEShutdownException(e));
						return;
					}
					
				}
				
				for(AlgorithmRun run : runs)
				{
					if(run.getRunResult().equals(RunResult.ABORT))
					{
						log.info("Um this was an abort {} : {} ", run.getRunConfig(), run);
						
						handler.onFailure(new TargetAlgorithmAbortException(run));
						return;
					}
				}
	
			} catch(RuntimeException e)
			{
				handler.onFailure(e);
				return;
			}
			try {
				handler.onSuccess(runs);
			} catch(RuntimeException e)
			{
				log.error("RuntimeException occurred during invocation of onSuccess(), calling onFailure()", e);
				handler.onFailure(e);
			}
			return;
				
		}
		
	}

	@Override
	public boolean areRunsObservable() {
		// TODO Auto-generated method stub
		return true;
	}
	
}
