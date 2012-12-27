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
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.AbstractTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.deferred.AbstractDeferredTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.deferred.TAECallback;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.exceptions.TAEShutdownException;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.RunToken;

public class MySQLDBTAE extends AbstractDeferredTargetAlgorithmEvaluator {


	private final MySQLPersistenceClient persistence;
	private final Logger log = LoggerFactory.getLogger(MySQLDBTAE.class);
	private final ExecutorService requestWatcher = Executors.newCachedThreadPool();
	

	public MySQLDBTAE(AlgorithmExecutionConfig execConfig, MySQLPersistenceClient persistence) {
		super(execConfig);
		this.persistence = persistence;
	}

	/*
	@Override
	public List<AlgorithmRun> evaluateRun(List<RunConfig> runConfigs)
	{
		
		/*
		RunToken token = persistence.enqueueRunConfigs(runConfigs);
		
		
		List<AlgorithmRun> runs = persistence.pollRunResults(token);
		
		for(AlgorithmRun run : runs)
		{
			if(run.getRunResult().equals(RunResult.ABORT))
			{
				log.info("Um this was an abort {} : {} ", run.getRunConfig(), run);
				
				throw new TargetAlgorithmAbortException(run);
			}
		}
		
		return runs;
		
	}*/


	private AtomicBoolean shutdownRequested = new AtomicBoolean(false);
	@Override
	public void notifyShutdown() {
		
		shutdownRequested.set(true);
		requestWatcher.shutdown();
		
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
	public void evaluateRunsAsync(List<RunConfig> runConfigs,
			TAECallback handler) {
		
		RunToken token = persistence.enqueueRunConfigs(runConfigs);
		
		MySQLRequestWatcher mysqlWatcher = new MySQLRequestWatcher(token, handler);
		
		requestWatcher.execute(mysqlWatcher);

	}

	@Override
	public boolean isRunFinal() {
		return true;
	}

	@Override
	public boolean areRunsPersisteted() {
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
			
			handler.onSuccess(runs);
			return;
				
		}
		
	}
	
}
