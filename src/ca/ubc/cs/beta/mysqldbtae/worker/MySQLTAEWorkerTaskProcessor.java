package ca.ubc.cs.beta.mysqldbtae.worker;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aeatk.algorithmrunconfiguration.AlgorithmRunConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.AlgorithmRunResult;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.ExistingAlgorithmRunResult;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.RunStatus;
import ca.ubc.cs.beta.aeatk.concurrent.threadfactory.SequentiallyNamedThreadFactory;
import ca.ubc.cs.beta.aeatk.misc.watch.AutoStartStopWatch;
import ca.ubc.cs.beta.aeatk.misc.watch.StopWatch;
import ca.ubc.cs.beta.aeatk.options.AbstractOptions;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.init.TargetAlgorithmEvaluatorBuilder;
import ca.ubc.cs.beta.mysqldbtae.exceptions.PoolChangedException;
import ca.ubc.cs.beta.mysqldbtae.persistence.worker.MySQLPersistenceWorker;
import ca.ubc.cs.beta.mysqldbtae.persistence.worker.UpdatedWorkerParameters;
import ca.ubc.cs.beta.mysqldbtae.version.MySQLDBTAEVersionInfo;

public class MySQLTAEWorkerTaskProcessor {

	private static Logger log = LoggerFactory.getLogger(MySQLTAEWorkerTaskProcessor.class);//Do not initialize here until after the logging environment variables are started
	
	private final MySQLTAEWorkerOptions options;
	private final Map<String, AbstractOptions> taeOptions;

	private  final long startTimeSecs;

	private final CountDownLatch latch;
	private final Object lock;
	
	private volatile long totalRunFetchTimeInMS = 0;
	
	private volatile RuntimeException crashReason = null;
	
	private volatile double workerIdleTime = 0;
	
	private volatile int totalRunFetchRequests = 0;
	public MySQLTAEWorkerTaskProcessor(long startTimeSecs, MySQLTAEWorkerOptions options, Map<String, AbstractOptions> taeOptions)
	{
		this(startTimeSecs, options, taeOptions, new CountDownLatch(0));
	}
	
	
	/**
	 * Creates a MySQLTAEWorkerTaskProcessor 
	 * 
	 * @param startTimeSecs
	 * @param options
	 * @param taeOptions
	 * @param latch - latch we will wait on before starting
	 */
	public MySQLTAEWorkerTaskProcessor(long startTimeSecs, MySQLTAEWorkerOptions options, Map<String, AbstractOptions> taeOptions, CountDownLatch latch)
	{
		this.startTimeSecs = startTimeSecs;
		this.options = options;
		this.taeOptions = taeOptions;
		this.latch = latch;
		this.lock = new Object();
	}
	
	public void process() throws PoolChangedException
	{
		try {
			latch.await();
		} catch (InterruptedException e1) {
			Thread.currentThread().interrupt();
			return;
		}
		
		long endTime = (startTimeSecs + getSecondsLeft()) * 1000;
		long minCutoffDeathTimestampInMillis = Long.MAX_VALUE;
		long lastUpdateTime = System.currentTimeMillis();
		long lastJobFinished = System.currentTimeMillis();
		
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTimeInMillis(endTime);
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTimeInMillis(startTimeSecs*1000);
	
		String version = "<Error getting version>";
		try
		{
			MySQLDBTAEVersionInfo mysqlVersionInfo = new MySQLDBTAEVersionInfo();
			version = mysqlVersionInfo.getVersion();
		} catch(RuntimeException e)
		{ 
			log.error("Couldn't get version information ", e);
		}
		
		ScheduledExecutorService executePushBack = Executors.newSingleThreadScheduledExecutor(new SequentiallyNamedThreadFactory("pushBackThread", true));
		
		
		
		
		
		
		try (final MySQLPersistenceWorker mysqlPersistence = new MySQLPersistenceWorker(options.mysqlOptions,options.pool, options.jobID,startCalendar.getTime(), endCalendar.getTime(), options.runsToBatch, options.delayBetweenRequests, options.poolIdleTimeLimit, version,options.createTables, options.concurrencyFactor))
		{
			
		
	
			Runtime.getRuntime().addShutdownHook(new Thread() {
				
				@Override
				public void run()
				{
					if(!mysqlPersistence.isClosed())
					{
						mysqlPersistence.resetUnfinishedRuns();
						try {
							mysqlPersistence.markWorkerCompleted("Triggered By Shutdown Hook (probably shutting down due to SIGTERM or SIGINT)");
						} catch(RuntimeException e)
						{
							e.printStackTrace();
							System.err.println("Error occured during shutdown hook?");
						}
					} 
					log.info("Shutdown hook finished");
				}
				
			});
	
			log.info("Initializing Target Algorithm Evaluator");
			
			try(TargetAlgorithmEvaluator tae = TargetAlgorithmEvaluatorBuilder.getTargetAlgorithmEvaluator(options.taeOptions,  false, taeOptions))
			{
				try {
					
					try {
		
				
		
						log.info("Starting Job Processing");
		
						while(true)
						{
							StopWatch runFetchTime = new AutoStartStopWatch();
							List<AlgorithmRunConfiguration> runs = mysqlPersistence.getRuns(options.runsToBatch);
							totalRunFetchTimeInMS += runFetchTime.stop();
							
							LinkedBlockingQueue<AlgorithmRunConfiguration> runsQueue = new LinkedBlockingQueue<AlgorithmRunConfiguration>();
							log.debug("Retrieved {} jobs from the database",runs.size());	
							for( AlgorithmRunConfiguration ent : runs)
							{
								try{
									runsQueue.put(ent);
								}  catch (InterruptedException e) {
									Thread.currentThread().interrupt();
									return;
								}
							}
							
							executePushBack.schedule(new PushBack(mysqlPersistence, runsQueue,options.delayBetweenRequests, executePushBack, options.pushbackThreshold), options.delayBetweenRequests, TimeUnit.SECONDS);
							log.debug("Job push back scheduled for {} seconds", options.delayBetweenRequests);
		
							totalRunFetchRequests++;
							
							StopWatch loopStart = new AutoStartStopWatch();
							
						
							int jobsEvaluated = 0;
							
							AlgorithmRunConfiguration ent;
							while((ent = runsQueue.poll())!=null)
							{
								boolean jobSuccess = processPair(mysqlPersistence, tae, ent);
								
								if(jobSuccess)
								{
									jobsEvaluated++;
								}
							}
								
							synchronized(lock){
								mysqlPersistence.resetUnfinishedRuns();
							}
							
							int minCutoffInDB = mysqlPersistence.getMinCutoff();
							if(jobsEvaluated==0)
							{
								log.info("No jobs were evaluated");
		
								if(minCutoffInDB>getSecondsLeft() && minCutoffDeathTimestampInMillis==Long.MAX_VALUE)
								{
									minCutoffDeathTimestampInMillis = System.currentTimeMillis()+options.minCutoffDeathTime*1000;
								}
							} else
							{
								lastJobFinished = System.currentTimeMillis();
								minCutoffDeathTimestampInMillis = Long.MAX_VALUE;
							}
							
							
							long loopStop = loopStart.stop();
							
							
							
							if(System.currentTimeMillis() - lastUpdateTime > (options.updateFrequency * 1000))
							{
								lastUpdateTime = checkForUpdatedParameters(mysqlPersistence);	
							}
							
							double waitTime = (options.delayBetweenRequests) - loopStop/1000.0;
							
							if(checkShutdownConditions(minCutoffDeathTimestampInMillis,lastJobFinished, mysqlPersistence, minCutoffInDB,waitTime))
							{
								return;
							} else
							{
								if(waitTime > 0.0)
								{
									log.info("Processing results took {} seconds, waiting for {} seconds", loopStop / 1000.0, waitTime);
									boolean fullSleep = mysqlPersistence.sleep(waitTime);
									
									if(!fullSleep)
									{
										log.debug("Worker interrupted, checking for updated parameters");
										lastUpdateTime= checkForUpdatedParameters(mysqlPersistence);
									} else 
									{
										if(jobsEvaluated==0)
										{
											workerIdleTime+=waitTime;
										}
										
									}
									
								}
							}
							 
							
								
							if(Thread.interrupted())
							{
								Thread.currentThread().interrupt();
								return;
							}
							
						}
					} catch(RuntimeException t)
					{
						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						
						PrintStream pout = new PrintStream(bout);
						
						t.printStackTrace(pout);
						mysqlPersistence.markWorkerCompleted("RuntimeException of some kind:"+bout.toString());
						crashReason = t;
						throw t;
						
					}
					
				} finally
				{
					try
					{
							if(Thread.interrupted())
							{
								Thread.currentThread().interrupt();
								//This really shouldn't happen
								mysqlPersistence.markWorkerCompleted("Thread Interrupted which is actually exceptionally odd?");				
							}
							else
							{
								mysqlPersistence.markWorkerCompleted("Shutdown for unknown reason?");
							}
						
						
							mysqlPersistence.resetUnfinishedRuns();
					} catch(Exception e)
					{
						log.error("Exception occurred while Task Processor shutting down", e);
					}
		
				}
			}
			
		}
	
	}


	/**
	 * @param mysqlPersistence
	 * @return
	 * @throws PoolChangedException
	 */
	private long checkForUpdatedParameters(final MySQLPersistenceWorker mysqlPersistence)
			throws PoolChangedException {
		
		log.debug("Checking for new parameters");
		UpdatedWorkerParameters params = mysqlPersistence.getUpdatedParameters();
		mysqlPersistence.updateIdleTime((int) Math.floor(workerIdleTime));
		
		if(params != null)
		{
			options.delayBetweenRequests = params.getDelayBetweenRequests();
			options.runsToBatch = params.getBatchSize();
			options.timeLimit = params.getTimeLimit();
			options.poolIdleTimeLimit = params.getPoolIdleTimeLimit();
			options.concurrencyFactor = params.getConcurrencyFactor();
			log.info("Updated values in database detected -  Delay: "+options.delayBetweenRequests+", Batch Size: "+options.runsToBatch+", Time Limit: "+options.timeLimit+", Pool Idle Time: "+options.poolIdleTimeLimit);
			
			if(!options.pool.trim().equals(params.getPool().trim()))
			{
				options.pool = params.getPool().trim();
				log.info("Pool Changed to {}",options.pool);
				mysqlPersistence.markWorkerCompleted("Pool changed to " + options.pool);
				throw new PoolChangedException(null, options.pool);
			}
					
		}
		return System.currentTimeMillis();
	}	
	

	/**
	 * Checks whether the worker should shutdown
	 * 
	 * @param minCutoffDeathTimestampInMillis
	 * @param lastJobFinished
	 * @param mysqlPersistence
	 * @param minCutoffInDB
	 * @param waitTime
	 * @return <code>true</code> if the worker should terminate, <code>false</code> otherwise.
	 */
	private boolean checkShutdownConditions(long minCutoffDeathTimestampInMillis,
			long lastJobFinished,
			final MySQLPersistenceWorker mysqlPersistence, int minCutoffInDB,
			double waitTime) {

		double idleTime = (int) (System.currentTimeMillis()/1000.0 - lastJobFinished/1000.0);
		
		long sumWorkerIdleTimes = mysqlPersistence.sumIdleTimes();
		
		double minCutoffKillDelayInSeconds = (minCutoffDeathTimestampInMillis-System.currentTimeMillis())/1000;
		
		log.debug("Worker life remaining: {} seconds, worker idle limit remaining: {} seconds, ",getSecondsLeft(), (int) ( options.idleLimit - idleTime));
		log.debug("Remaining Idle Time for Worker Pool: {} seconds", options.poolIdleTimeLimit-sumWorkerIdleTimes);

		if(waitTime > getSecondsLeft())
		{
			log.info("Wait time {} is too high, finishing up", waitTime);
			mysqlPersistence.markWorkerCompleted("Time Limit Expired (Wait time remaining is higher than seconds we have left)");
			return true;
		} else if(idleTime > options.idleLimit)
		{
			log.info("We have been idle too long {}, finishing up", idleTime );
			mysqlPersistence.markWorkerCompleted("Idle limit reached: " + idleTime + " versus limit: " + options.idleLimit);
			return true;
		} else if(sumWorkerIdleTimes > options.poolIdleTimeLimit)
		{
			log.info("Aggregate pool idle time too long {}, finishing up", sumWorkerIdleTimes );
			mysqlPersistence.markWorkerCompleted("Pool Idle Limit reached: " + sumWorkerIdleTimes + " versus limit: " + options.poolIdleTimeLimit);
			return true;
		} else if(minCutoffKillDelayInSeconds < 0)
		{
			log.info("Minimum job cutoff time is too high {} seconds with what we have remaining {} seconds, giving up", minCutoffInDB,  getSecondsLeft());
			mysqlPersistence.markWorkerCompleted("Minimum Job Cutoff in DB is too high: " + minCutoffInDB + " seconds verus: " + getSecondsLeft());
			return true;
		} else
		{
			if(minCutoffDeathTimestampInMillis != Long.MAX_VALUE)
			{
				String killWindow = (minCutoffDeathTimestampInMillis == Long.MAX_VALUE) ? "inf" : ""+minCutoffKillDelayInSeconds;
				log.warn("All jobs in database have a cutoff time larger our remaining time, we will terminate within {} (s), unless new jobs are added", killWindow);
			}
		}
		
		return false;
	}


	/**
	 * Process an individual run
	 *
	 * @param mysqlPersistence	The mysql persistence object
	 * @param taeMap	map of tae options
	 * @param ent	the pair to evaluate
	 * @return <code>true</code> if a job was successfully finished to completion.
	 */
	private boolean processPair(final MySQLPersistenceWorker mysqlPersistence, TargetAlgorithmEvaluator tae, AlgorithmRunConfiguration runConfig) {
		
		
		
		boolean jobEvaluated = false;

		
		AutoStartStopWatch runWatch = new AutoStartStopWatch();
			
		try {									
			
			TargetAlgorithmEvaluatorRunObserver obs = new TargetAlgorithmEvaluatorRunObserver() {
				private long lastDBUpdate = System.currentTimeMillis();
				@Override
				public void currentStatus( List<? extends AlgorithmRunResult> runs) {
					
					if((System.currentTimeMillis() - lastDBUpdate) < options.delayBetweenRequests * 1000)
					{
						//=== Too soon to update request
						return;
					} else
					{
						boolean shouldKill = mysqlPersistence.updateRunStatusAndCheckKillBit(runs.get(0));
						
						if(shouldKill)
						{
							log.info("Database updated and run has been flagged as killed");
							runs.get(0).kill();
						} else
						{
							log.debug("Database updated continue run");
						}
						
						lastDBUpdate = System.currentTimeMillis();
					}
					
				}
				
			};
			
			
			if( (runConfig.getCutoffTime() < getSecondsLeft()) || !options.checkMinCutoff)
			{
				if(runConfig.getProblemInstanceSeedPair().getProblemInstance().getInstanceID() > 0)
				{
					log.info("Starting processing of job {} ", runConfig.getProblemInstanceSeedPair().getProblemInstance().getInstanceID());
				} else
				{
					log.info("Starting processing of job");
				}
				List<AlgorithmRunResult> finishedRuns=tae.evaluateRun(Collections.singletonList(runConfig), obs);
				jobEvaluated = true;
				mysqlPersistence.setRunResults(finishedRuns);
				
				log.info("Job Completed");
				
			} else
			{
				log.info("Skipping runs that could require up to {} (s), because we only have {} (s) left", runConfig.getCutoffTime(), getSecondsLeft() );
			}
			
										
		} catch(Exception e)
		{
			log.error("Exception occured while running algorithm" ,e);
			StringBuilder sb = new StringBuilder();
			sb.append(e.getClass()).append(":").append(e.getMessage()).append(":");
			
			
			int i=0;
			//2048 is the length of the field in the DB, 2000 is buffer
			while(sb.length() < 2000 && i < e.getStackTrace().length)
			{
				sb.append(e.getStackTrace()[i]).append(":");
				i++;
			}
			
			String addlRunData = sb.substring(0, Math.min(2000,sb.length()));
			
			mysqlPersistence.setRunResults(Collections.singletonList((AlgorithmRunResult)new ExistingAlgorithmRunResult(runConfig,RunStatus.ABORT, 0.0 ,0 ,0, runConfig.getProblemInstanceSeedPair().getSeed(), addlRunData , runWatch.stop())));
		}
		return jobEvaluated;
	}
	
	/**
	 * Runnable which is executed on every loop of Process.  Pushes back any assigned jobs not yet run.
	 */
	public class PushBack implements Runnable
	{
		private final MySQLPersistenceWorker  mysqlPersistence;
		private final LinkedBlockingQueue<AlgorithmRunConfiguration> runsQueue;
		private final ScheduledExecutorService executePushBack;
		private final int delayBetweenRequests;
		private final int pushbackThreshhold;
		
		
		public PushBack(MySQLPersistenceWorker mysqlPersistence, LinkedBlockingQueue<AlgorithmRunConfiguration> runsQueue, int delayBetweenRequests, ScheduledExecutorService executePushBack, int pushbackThreshold)
		{
			this.mysqlPersistence = mysqlPersistence;
			this.runsQueue = runsQueue;
			this.delayBetweenRequests = delayBetweenRequests;
			this.executePushBack = executePushBack;
			this.pushbackThreshhold = pushbackThreshold;
		}
		
		@Override
		public void run() {
			List< AlgorithmRunConfiguration> extraRuns = new ArrayList<AlgorithmRunConfiguration>();
			
			if(runsQueue.size() == 0)
			{ //Nothing to push back
				log.debug("Nothing to push back");
				return;
			}
			
			int newJobsInDB = this.mysqlPersistence.getNumberOfNewRuns();
			if(newJobsInDB < pushbackThreshhold)
			{
				synchronized(lock){
					runsQueue.drainTo(extraRuns);
					mysqlPersistence.resetRunConfigs(extraRuns);
				}
				log.info("Current batch of jobs is taking to long, {} queued runs have been pushed back to the database", extraRuns.size());
			} else
			{
					log.debug("There are still {} jobs in DB, not pushing back at the moment", newJobsInDB);
					executePushBack.schedule(this, delayBetweenRequests, TimeUnit.SECONDS);
			}
		}
	}
	
	public long getSecondsLeft()
	{
		
		long timeUsed = (System.currentTimeMillis() / 1000 ) - startTimeSecs;
		long timeLimit = options.timeLimit;
		
		return (timeLimit - options.shutdownBuffer - timeUsed);
		
	}
	
	public long getTotalRunFetchTimeInMS() {
		return totalRunFetchTimeInMS;
	}


	public int getTotalRunFetchRequests() {
		return totalRunFetchRequests;
	}
	
	public RuntimeException getCrashReason()
	{
		return crashReason;
	}



}
