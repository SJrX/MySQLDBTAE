package ca.ubc.cs.beta.mysqldbtae.worker;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aclib.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.ExistingAlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.kill.KillableAlgorithmRun;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.misc.watch.AutoStartStopWatch;
import ca.ubc.cs.beta.aclib.misc.watch.StopWatch;
import ca.ubc.cs.beta.aclib.options.AbstractOptions;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.init.TargetAlgorithmEvaluatorBuilder;
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
	
	private volatile long totalRunFetchTimeInMS = 0;
	
	private volatile RuntimeException crashReason = null;


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
		long lastUpdateTime = System.currentTimeMillis();
		long lastJobFinished = System.currentTimeMillis();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(endTime);
	
		String version = "<Error getting version>";
		try
		{
			MySQLDBTAEVersionInfo mysqlVersionInfo = new MySQLDBTAEVersionInfo();
			version = mysqlVersionInfo.getVersion();
		} catch(RuntimeException e)
		{ 
			log.error("Couldn't get version information ", e);
		}
		
		final MySQLPersistenceWorker mysqlPersistence = new MySQLPersistenceWorker(options.mysqlOptions,options.pool, options.jobID,calendar.getTime(), options.runsToBatch, options.delayBetweenRequests , version,options.createTables);
	
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run()
			{
				//
				mysqlPersistence.resetUnfinishedRuns();
				try {
					mysqlPersistence.markWorkerCompleted("Triggered By Shutdown Hook");
				} catch(RuntimeException e)
				{
					e.printStackTrace();
					System.err.println("Error occured during shutdown hook?");
				}
			log.info("Shutdown hook finished");
			}
			
		});
		
		try {
			
			try {
		
				Map<AlgorithmExecutionConfig, TargetAlgorithmEvaluator> taeMap = new HashMap<AlgorithmExecutionConfig, TargetAlgorithmEvaluator>();
				
				log.info("Waiting for Work");
				
				while(true)
				{
					
					
					StopWatch runFetchTime = new AutoStartStopWatch();
					
					Map<AlgorithmExecutionConfig, List<RunConfig>> runs = mysqlPersistence.getRuns(options.runsToBatch);
					
					
					totalRunFetchTimeInMS += runFetchTime.stop();
					totalRunFetchRequests++;
					
					StopWatch loopStart = new AutoStartStopWatch();
					
				
					List<AlgorithmRun> algorithmRuns = new ArrayList<AlgorithmRun>(options.runsToBatch);
					boolean zeroJobs = true;
					for(Entry<AlgorithmExecutionConfig, List<RunConfig>> ent : runs.entrySet())
					{
						zeroJobs = false;
						AlgorithmExecutionConfig execConfig = ent.getKey();
						
						log.info("Have {} jobs to do ", ent.getValue().size());
						if(taeMap.get(execConfig) == null)
						{
							TargetAlgorithmEvaluator tae = TargetAlgorithmEvaluatorBuilder.getTargetAlgorithmEvaluator(options.taeOptions, execConfig, false, taeOptions);
							taeMap.put(execConfig, tae);
						}
							
						TargetAlgorithmEvaluator tae = taeMap.get(execConfig);
						
						
						for(final RunConfig runConfig : ent.getValue())
						{ //===Process the requests one by one, in case we get an Exception
							AutoStartStopWatch runWatch = new AutoStartStopWatch();
							
							try {									
								
								TargetAlgorithmEvaluatorRunObserver obs = new TargetAlgorithmEvaluatorRunObserver() {
									private long lastDBUpdate = System.currentTimeMillis();
									@Override
									public void currentStatus( List<? extends KillableAlgorithmRun> runs) {
										
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
												log.info("Database updated continue run");
											}
											
											lastDBUpdate = System.currentTimeMillis();
										}
										
										
										
										
										
									}
									
								};
								
								
								if(runConfig.getCutoffTime() < getSecondsLeft())
								{
									List<AlgorithmRun> finishedRuns=tae.evaluateRun(Collections.singletonList(runConfig), obs);
									mysqlPersistence.setRunResults(finishedRuns);
									algorithmRuns.addAll(finishedRuns);
								} else
								{
									log.info("Skipping runs for {} seconds, because only {} left", runConfig.getCutoffTime(), getSecondsLeft() );
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
								
								algorithmRuns.add(new ExistingAlgorithmRun(execConfig,runConfig,"ABORT, 0.0 ,0 ,0, " + runConfig.getProblemInstanceSeedPair().getSeed() + "," + addlRunData , runWatch.stop()));
							}
							
							
						}
						
					}
					
					if(zeroJobs)
					{
						log.info("No jobs in database");
					} else
					{
						log.info("Resetting unfinished runs");
						//mysqlPersistence.setRunResults(algorithmRuns);
						mysqlPersistence.resetUnfinishedRuns();
						lastJobFinished = System.currentTimeMillis();
					}
					
					
					long loopStop = loopStart.stop();
					
					
					
					if(System.currentTimeMillis() - lastUpdateTime > (options.updateFrequency * 1000))
					{
						
						log.info("Checking for new parameters");
						UpdatedWorkerParameters params = mysqlPersistence.getUpdatedParameters();
						
						if(params != null)
						{
							options.delayBetweenRequests = params.getDelayBetweenRequests();
							options.runsToBatch = params.getBatchSize();
							options.timeLimit = params.getTimeLimit();
							
							log.info("New Delay {} and Batch Size {}", options.delayBetweenRequests, options.runsToBatch);
							
							if(!options.pool.trim().equals(params.getPool().trim()))
							{
								options.pool = params.getPool().trim();
								log.info("Pool Changed to {}",options.pool);
								throw new PoolChangedException(null, options.pool);
							}
									
							
							
						}
						lastUpdateTime = System.currentTimeMillis();
						
					}
					
					double waitTime = (options.delayBetweenRequests) - loopStop/1000.0;
					
					double idleTime = (int) (System.currentTimeMillis()/1000.0 - lastJobFinished/1000.0);
					
					log.info("Seconds left for worker is {} seconds and idle limit left is {} seconds ",getSecondsLeft(), (int) ( options.idleLimit - idleTime));
					
					
					if(waitTime > getSecondsLeft())
					{
						log.info("Wait time {} is too high, finishing up", waitTime);
						return;
					} else if(idleTime > options.idleLimit)
					{
						log.info("We have been idle too long {}, finishing up", idleTime );
						return;
					}
					 
					log.info("Processing results took {} seconds, waiting for {} seconds", loopStop / 1000.0, waitTime);
					
					
					if(waitTime > 0.0)
					{
						mysqlPersistence.sleep(waitTime);
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
				mysqlPersistence.markWorkerCompleted(bout.toString());
				crashReason = t;
				throw t;
				
			}
			
		} finally
		{
			mysqlPersistence.markWorkerCompleted("Normal Shutdown");
			mysqlPersistence.resetUnfinishedRuns();
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
