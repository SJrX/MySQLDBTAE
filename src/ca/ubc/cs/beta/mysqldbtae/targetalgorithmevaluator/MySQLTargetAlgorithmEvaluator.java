package ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aclib.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.RunResult;
import ca.ubc.cs.beta.aclib.concurrent.threadfactory.SequentiallyNamedThreadFactory;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.AbstractAsyncTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.exceptions.TargetAlgorithmAbortException;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.RunToken;

@ThreadSafe
public class MySQLTargetAlgorithmEvaluator extends AbstractAsyncTargetAlgorithmEvaluator {


	final MySQLPersistenceClient persistence;
	private final Logger log = LoggerFactory.getLogger(MySQLTargetAlgorithmEvaluator.class);
	private final ScheduledExecutorService requestWatcher;
	
	private final boolean wakeUpWorkers;
	private final long delayInMS;
	private final AtomicLong lastWarning; 
	

	public MySQLTargetAlgorithmEvaluator( MySQLPersistenceClient persistence) {
		//We set the number of thread pools to twice the number of available processors because we believe the tasks will be IO bound and not CPU bound
		this( persistence, false, Runtime.getRuntime().availableProcessors()*2, 2000);
		
	}

	
	public MySQLTargetAlgorithmEvaluator( MySQLPersistenceClient persistence, boolean wakeUpWorkers, int poolSize, long delayInMS) {
		
		this.persistence = persistence;
		this.wakeUpWorkers = wakeUpWorkers;
		this.delayInMS = delayInMS;
		requestWatcher = Executors.newScheduledThreadPool(poolSize, (new SequentiallyNamedThreadFactory("MySQL Request Watching Thread")));
		lastWarning = new AtomicLong(delayInMS);
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
		
		if(this.wakeUpWorkers) persistence.wakeWorkers(runConfigs.size() + 1);
		
		
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
		private final TargetAlgorithmEvaluatorCallback handler;
		private long lastRun = System.currentTimeMillis();
		
	
		public MySQLRequestWatcher(RunToken token, TargetAlgorithmEvaluatorCallback handler)
		{
			this.token = token;
			this.handler = handler;
			
		}
		
		@Override
		public synchronized void run() {
			
				List<AlgorithmRun> runs = null;
				try {
					
					if((runs = persistence.pollRunResults(token)) == null)
					{
				
						if(!shutdownRequested.get())
						{
							requestWatcher.schedule(this, delayInMS, TimeUnit.MILLISECONDS);
						}
						
						
						if(((System.currentTimeMillis() - lastRun)) > lastWarning.get() * 2)
						{
							log.info("MySQL Request Watcher has fallen behind in polling and it took {} ms to execute, next warning at {} ms", (System.currentTimeMillis() - lastRun), lastWarning.get()*4 );
							lastWarning.getAndSet(lastWarning.get() * 2);
						}
						lastRun = System.currentTimeMillis();
						
						return;
						
						
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
				
			
				
		}
		
	}

	@Override
	public boolean areRunsObservable() {
		return true;
	}

}
