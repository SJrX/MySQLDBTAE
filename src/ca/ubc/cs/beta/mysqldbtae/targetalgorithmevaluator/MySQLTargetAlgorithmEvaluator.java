package ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.AbstractAsyncTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.exceptions.TAEShutdownException;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.RunToken;

public class MySQLTargetAlgorithmEvaluator extends AbstractAsyncTargetAlgorithmEvaluator {


	private final MySQLPersistenceClient persistence;
	private final Logger log = LoggerFactory.getLogger(MySQLTargetAlgorithmEvaluator.class);
	private final ExecutorService requestWatcher = Executors.newCachedThreadPool();
	
	private final boolean wakeUpWorkers;

	public MySQLTargetAlgorithmEvaluator(AlgorithmExecutionConfig execConfig, MySQLPersistenceClient persistence) {
		this(execConfig, persistence, false);
	}

	
	public MySQLTargetAlgorithmEvaluator(AlgorithmExecutionConfig execConfig, MySQLPersistenceClient persistence, boolean wakeUpWorkers) {
		super(execConfig);
		this.persistence = persistence;
		this.wakeUpWorkers = wakeUpWorkers;
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
	@SuppressWarnings("unchecked")
	public void evaluateRunsAsync(List<RunConfig> runConfigs,
			TargetAlgorithmEvaluatorCallback handler, TargetAlgorithmEvaluatorRunObserver obs) {
		
		if(runConfigs.size() == 0)
		{
			handler.onSuccess(Collections.EMPTY_LIST);
		}
		RunToken token = persistence.enqueueRunConfigs(runConfigs,obs);
		if(this.wakeUpWorkers) persistence.wakeWorkers();
		
		CountDownLatch latch = new CountDownLatch(1);
		MySQLRequestWatcher mysqlWatcher = new MySQLRequestWatcher(token, handler, latch);
		
		requestWatcher.execute(mysqlWatcher);
		try {
			latch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

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
		private final TargetAlgorithmEvaluatorCallback handler; 
		//Latch to release so that the async caller can go
		//Release it once we know there are no results ready in the db, or after we have completed the onSuccess method
		private final CountDownLatch asyncLatch;
	
		public MySQLRequestWatcher(RunToken token, TargetAlgorithmEvaluatorCallback handler, CountDownLatch asyncLatch)
		{
			this.token = token;
			this.handler = handler;
			this.asyncLatch = asyncLatch;
		}
		
		@Override
		public void run() {
			try {
				List<AlgorithmRun> runs = null;
				try {
					while((runs = persistence.pollRunResults(token)) == null)
					{
						this.asyncLatch.countDown();
						
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
			} finally
			{
				asyncLatch.countDown();
			}
				
		}
		
	}

	@Override
	public boolean areRunsObservable() {
		// TODO Auto-generated method stub
		return true;
	}
	
}
