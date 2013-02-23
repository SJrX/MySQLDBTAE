package ca.ubc.cs.beta.mysqldbtae.worker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aclib.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.ExistingAlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.kill.KillableAlgorithmRun;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.misc.version.VersionTracker;
import ca.ubc.cs.beta.aclib.misc.watch.AutoStartStopWatch;
import ca.ubc.cs.beta.aclib.misc.watch.StopWatch;
import ca.ubc.cs.beta.aclib.options.ConfigToLaTeX;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorBuilder;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.currentstatus.CurrentRunStatusObserver;
import ca.ubc.cs.beta.mysqldbtae.exceptions.PoolChangedException;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;
import ca.ubc.cs.beta.mysqldbtae.persistence.worker.MySQLPersistenceWorker;
import ca.ubc.cs.beta.mysqldbtae.persistence.worker.UpdatedWorkerParameters;
import ca.ubc.cs.beta.mysqldbtae.version.MySQLDBTAEVersionInfo;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class MySQLTAEWorker {

	private static Logger log;//Do not initialize here until after the logging environment variables are started
	
	
	
	/**
	 * @param args
	 */
	
	private static final long startTimeSecs = System.currentTimeMillis() / 1000;
	
	public static void main(String[] args) {
	
		
		
		
		MySQLTAEWorkerOptions options = new MySQLTAEWorkerOptions();
		
		
		
				
		JCommander com = new JCommander(options, true, true);
		com.setProgramName("mysqltaeworker");
		int exceptionCount = 0;
		
		
		
		
		
		
		try {
			try {
			com.parse(args);
			} finally
			{
				String workerID = options.jobID  + "/" + ManagementFactory.getRuntimeMXBean().getName();
				String logLocation = options.logDirectory.getAbsolutePath() + File.separator + "log-worker-"+workerID.replaceAll("[^A-Za-z0-9_]+", "_")+".txt";
				System.setProperty("LOG_LOCATION", logLocation);
				System.out.println("*****************************\nLogging to: " + logLocation +  "\n*****************************");
				
				log = LoggerFactory.getLogger(MySQLTAEWorker.class);;

			}
			
			
			
			
			
			
			
			
			
			List<String> names = TargetAlgorithmEvaluatorBuilder.getAvailableTargetAlgorithmEvaluators(options.taeOptions);
			
			VersionTracker.setClassLoader(TargetAlgorithmEvaluatorBuilder.getClassLoader(options.taeOptions));
			VersionTracker.logVersions();
		
			
			log.info("Abort on Crash and abort on First Run Crash are disabled, as is verifySAT");
			options.taeOptions.abortOnCrash = false;
			options.taeOptions.abortOnFirstRunCrash = false;
			options.taeOptions.verifySAT = false;
			
			for(String name : names)
			{
				log.info("Target Algorithm Evaluator Available {} ", name);
			}
			
			log.info("====== Configuration ======\n{}",options.toString());
			
			
			
			boolean done=false;
			while(!done && (exceptionCount < options.uncaughtExceptionLimit))
			{
				try {
					
					processRuns(options);
					done = true;
					log.info("Done work");
					
				}catch(PoolChangedException e)
				{
					options.pool = e.getNewPool();
				} catch(Exception e)
				{
					if(Thread.currentThread().isInterrupted())
					{
						log.info("Thread interrupted aborting");
						break;
					} else
					{
						
						try {
							Thread.sleep( (long) (( 120 + Math.random()*60.0) * 1000) );
						} catch (InterruptedException e1) {
							Thread.interrupted();
							return;
						}
						exceptionCount++;
						log.error("Exception occured",e);
						
						log.info("Uncaught exceptions used {} out of {}", exceptionCount, options.uncaughtExceptionLimit);
					}
					
				}
				

			}
		
		}catch(ParameterException e)
		{
			try {
				ConfigToLaTeX.usage(ConfigToLaTeX.getParameters(options));
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.flush();
			
			log.error("Error occured parsing arguments: {}", e.getMessage());
			
			
		}
		log.info("Main Method Ended");
	}
	
	public static long getSecondsLeft(MySQLTAEWorkerOptions options)
	{
		
		long timeUsed = (System.currentTimeMillis() / 1000 ) - startTimeSecs;
		long timeLimit = options.timeLimit;
		
		return (timeLimit - options.shutdownBuffer - timeUsed);
		
	}
	
	
	public static void processRuns(final MySQLTAEWorkerOptions options) throws PoolChangedException
	{
		
		
			long endTime = (startTimeSecs + getSecondsLeft(options)) * 1000;
			long lastUpdateTime = System.currentTimeMillis();
			
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
						System.err.println("WHAT?");
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
						
						
						Map<AlgorithmExecutionConfig, List<RunConfig>> runs = mysqlPersistence.getRuns(options.runsToBatch);
						
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
								TargetAlgorithmEvaluator tae = TargetAlgorithmEvaluatorBuilder.getTargetAlgorithmEvaluator(options.taeOptions, execConfig, false);
								taeMap.put(execConfig, tae);
							}
								
							TargetAlgorithmEvaluator tae = taeMap.get(execConfig);
							
							
							for(final RunConfig runConfig : ent.getValue())
							{ //===Process the requests one by one, in case we get an Exception
								AutoStartStopWatch runWatch = new AutoStartStopWatch();
								
								try {
									
									
									CurrentRunStatusObserver obs = new CurrentRunStatusObserver() {
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
									
									
									if(runConfig.getCutoffTime() < getSecondsLeft(options))
									{
										algorithmRuns.addAll(tae.evaluateRun(Collections.singletonList(runConfig), obs));
									} else
									{
										log.info("Skipping runs for {} seconds, because only {} left", runConfig.getCutoffTime(), getSecondsLeft(options) );
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
						}
						
						log.info("Saving results");
						mysqlPersistence.setRunResults(algorithmRuns);
						mysqlPersistence.resetUnfinishedRuns();
						
						long loopStop = loopStart.stop();
						
						
						
						if(System.currentTimeMillis() - lastUpdateTime > (options.updateFrequency * 1000))
						{
							
							log.info("Checking for new parameters");
							UpdatedWorkerParameters params = mysqlPersistence.getUpdatedParameters();
							if(params != null)
							{
								options.delayBetweenRequests = params.getDelayBetweenRequests();
								options.runsToBatch = params.getBatchSize();
								
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
						
						
						log.info("Seconds left for worker is {} seconds",getSecondsLeft(options));
						
						if(waitTime > getSecondsLeft(options))
						{
							log.info("Wait time {} is too high, finishing up", waitTime);
							return;
						} else if(getSecondsLeft(options) < 0)
						{
							//Not sure why this isn't a dead branch 
						}
						
						
						
				
						 
						log.info("Processing results took {} seconds, waiting for {} seconds", loopStop / 1000.0, waitTime);
						
						try {
							if(waitTime > 0.0)
							{
								Thread.sleep((int) (waitTime * 1000));
							}
							
						} catch (InterruptedException e) {
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
					throw t;
					
				}
				
			} finally
			{
				mysqlPersistence.markWorkerCompleted("Normal Shutdown");
				mysqlPersistence.resetUnfinishedRuns();
			}
			
		
		
		
		
		
	}

	
}
