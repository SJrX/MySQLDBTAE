package ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import net.jcip.annotations.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aeatk.algorithmrunconfiguration.AlgorithmRunConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.AlgorithmRunResult;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.RunStatus;
import ca.ubc.cs.beta.aeatk.concurrent.threadfactory.SequentiallyNamedThreadFactory;
import ca.ubc.cs.beta.aeatk.misc.watch.AutoStartStopWatch;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.AbstractAsyncTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.exceptions.TargetAlgorithmAbortException;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.RunToken;

@ThreadSafe
public class MySQLTargetAlgorithmEvaluator extends AbstractAsyncTargetAlgorithmEvaluator {


	
	public static final String ADDITIONAL_RUN_DATA_ENCODED_EXCEPTION_PREFIX = "ENCODED EXCEPTION: ";
	final MySQLPersistenceClient persistence;
	private final Logger log = LoggerFactory.getLogger(MySQLTargetAlgorithmEvaluator.class);
	private final ScheduledExecutorService requestWatcher;
	
	private final boolean wakeUpWorkers;
	private final long delayInMS;
	private final AtomicLong lastWarning; 
	
	/**
	 * This latch 
	 */
	private final CountDownLatch workersCleanedUp = new CountDownLatch(1);

	public MySQLTargetAlgorithmEvaluator( MySQLPersistenceClient persistence) {
		//We set the number of thread pools to twice the number of available processors because we believe the tasks will be IO bound and not CPU bound
		this( persistence, false, Runtime.getRuntime().availableProcessors()*2, 2000);
		
	}

	
	public MySQLTargetAlgorithmEvaluator( MySQLPersistenceClient persistence, boolean wakeUpWorkers, int poolSize, long delayInMS) {
		this(persistence, wakeUpWorkers, poolSize, delayInMS, 120);

	}


	public MySQLTargetAlgorithmEvaluator(MySQLPersistenceClient persistence,boolean wakeUpWorkers, int poolSize,long delayInMS,final int deadJobCheckFrequency) {
		this.persistence = persistence;
		this.wakeUpWorkers = wakeUpWorkers;
		this.delayInMS = delayInMS;
		requestWatcher = Executors.newScheduledThreadPool(poolSize, (new SequentiallyNamedThreadFactory("MySQL Request Watching Thread")));
		lastWarning = new AtomicLong(delayInMS);
	
		Runnable deadJobChecker = new Runnable()
		{

			@Override
			public void run() {
				
				try {
					try 
					{
						MySQLTargetAlgorithmEvaluator.this.persistence.fixJobState();
					} catch(Throwable t)
					{
						log.error("Encountered unknown throwable when fixing job state", t);
						throw t;
					}
				} finally
				{
					workersCleanedUp.countDown();
				}
				
				
			}
			
		};
		log.debug("Scheduling dead job checker with frequency {} s", deadJobCheckFrequency);
		requestWatcher.scheduleAtFixedRate(deadJobChecker, 0, deadJobCheckFrequency, TimeUnit.SECONDS);
		
	}


	private AtomicBoolean shutdownRequested = new AtomicBoolean(false);
	@Override
	public void notifyShutdown() {
		log.debug("MySQL TAE Shutdown in Progress");
		
		if(workersCleanedUp.getCount() > 0)
		{
			log.debug("Worker check has not completed, waiting");
			try {
				workersCleanedUp.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
					
		}
		
		
		shutdownRequested.set(true);
		requestWatcher.shutdown();
	
		try {
			
			requestWatcher.awaitTermination(365, TimeUnit.DAYS);
			
		} catch(InterruptedException e)
		{
			Thread.currentThread().interrupt();
		
		}
		
		persistence.shutdown();
		log.debug("MySQL TAE Shutdown Complete");
		requestWatcher.shutdownNow();
		
		
	}

	@Override
	@SuppressWarnings("unchecked")
	public void evaluateRunsAsync(List<AlgorithmRunConfiguration> runConfigs,
			TargetAlgorithmEvaluatorCallback handler, TargetAlgorithmEvaluatorRunObserver obs) {
	    
	    //Start a stop watch.
	    //Submission time should not take longer than 5 s plus one additional second for every additional 100 runs.
	    long maxSubmitTimeInMS = 5000 + runConfigs.size()*10;
	    AutoStartStopWatch stopwatch = new AutoStartStopWatch();
	    
	    if(runConfigs.size() == 0)
		{
			handler.onSuccess(Collections.EMPTY_LIST);
		}
		RunToken token = persistence.enqueueRunConfigs(runConfigs,obs);
		
		if(this.wakeUpWorkers) persistence.wakeWorkers(runConfigs.size() + 1);
		
		
		MySQLRequestWatcher mysqlWatcher = new MySQLRequestWatcher(token, handler);
		
		requestWatcher.execute(mysqlWatcher);
		
		//Stop stopwatch, if >  max limit log a warning.
		long submitTime = stopwatch.stop();
		if(submitTime > maxSubmitTimeInMS)
		{
		    log.warn("Took more than expected maximum time of {} ms to submit {} runs.",maxSubmitTimeInMS,runConfigs.size());
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
		private long lastRun = System.currentTimeMillis();
		
	
		public MySQLRequestWatcher(RunToken token, TargetAlgorithmEvaluatorCallback handler)
		{
			this.token = token;
			this.handler = handler;
			
		}
		
		@Override
		public synchronized void run() {
			
				List<AlgorithmRunResult> runs = null;
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
					
					for(AlgorithmRunResult run : runs)
					{
						if(run.getRunStatus().equals(RunStatus.ABORT))
						{
							log.info("Detected an aborted run {} : {} ", run.getAlgorithmRunConfiguration(), run);
							String addlRunData = run.getAdditionalRunData().trim();
							if(addlRunData.startsWith(MySQLTargetAlgorithmEvaluator.ADDITIONAL_RUN_DATA_ENCODED_EXCEPTION_PREFIX))
							{
								log.error("Worker encountered error, exception details: {}",  addlRunData.replaceAll(";", "\n"));
							}
							
							handler.onFailure(new TargetAlgorithmAbortException(run));
							return;
						}
					}
		
				} catch(RuntimeException e)
				{
					handler.onFailure(e);
					return;
				} catch(Throwable t)
				{
					handler.onFailure(new IllegalStateException("Unexpected Throwable occured", t));
					
					if(t instanceof Error)
					{
						throw t;
					}
					
					
					return;
				}
				try {
					handler.onSuccess(runs);
				} catch(RuntimeException e)
				{
					log.error("RuntimeException occurred during invocation of onSuccess(), calling onFailure()", e);
					handler.onFailure(e);
				} catch(Throwable t)
				{
					handler.onFailure(new IllegalStateException("Unexpected Throwable occured", t));
					
					if(t instanceof Error)
					{
						
						throw (Error) t;
					}
					return;
				}
				
			
				
		}
		
	}

	@Override
	public boolean areRunsObservable() {
		return true;
	}

}
