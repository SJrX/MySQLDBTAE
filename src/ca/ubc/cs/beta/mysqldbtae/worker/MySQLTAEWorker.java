package ca.ubc.cs.beta.mysqldbtae.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aclib.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.ExistingAlgorithmRun;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.misc.version.VersionTracker;
import ca.ubc.cs.beta.aclib.misc.watch.AutoStartStopWatch;
import ca.ubc.cs.beta.aclib.misc.watch.StopWatch;
import ca.ubc.cs.beta.aclib.options.ConfigToLaTeX;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorBuilder;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;
import ca.ubc.cs.beta.mysqldbtae.persistence.worker.MySQLPersistenceWorker;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class MySQLTAEWorker {

	private static final Logger log = LoggerFactory.getLogger(MySQLTAEWorker.class);
	
	
	
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
			com.parse(args);
			
			List<String> names = TargetAlgorithmEvaluatorBuilder.getAvailableTargetAlgorithmEvaluators(options.taeOptions);
			
			VersionTracker.setClassLoader(TargetAlgorithmEvaluatorBuilder.getClassLoader(options.taeOptions));
			VersionTracker.logVersions();
		
			
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
					
					
				} catch(Exception e)
				{
					if(Thread.currentThread().isInterrupted())
					{
						log.info("Thread interrupted aborting");
						break;
					} else
					{
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
	}
	
	public static long getSecondsLeft(MySQLTAEWorkerOptions options)
	{
		
		long timeUsed = (System.currentTimeMillis() / 1000 ) - startTimeSecs;
		long timeLimit = options.timeLimit;
		
		return (timeLimit - options.shutdownBuffer - timeUsed);
		
	}
	
	
	public static void processRuns(MySQLTAEWorkerOptions options)
	{
		
			final MySQLPersistenceWorker mysqlPersistence = new MySQLPersistenceWorker(options.mysqlOptions,options.pool);
		
			Runtime.getRuntime().addShutdownHook(new Thread() {
				
				@Override
				public void run()
				{
					mysqlPersistence.resetUnfinishedRuns();
				}
				
			});
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
						
						
						for(RunConfig runConfig : ent.getValue())
						{ //===Process the requests one by one, in case we get an Exception
							AutoStartStopWatch runWatch = new AutoStartStopWatch();
							
							try {
								
								
								
								
								if(runConfig.getCutoffTime() < getSecondsLeft(options))
								{
									algorithmRuns.addAll(tae.evaluateRun(runConfig));
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
					
					double waitTime = (options.delayBetweenRequests) - loopStop/1000.0;
					
					
					log.info("Seconds left for worker is {} seconds",getSecondsLeft(options));
					
					if(waitTime > getSecondsLeft(options))
					{
						
						return;
					} else if(getSecondsLeft(options) < 0)
					{
						
					}
					 
					log.info("Processing results took {} seconds, waiting for {} seconds", loopStop / 1000.0, waitTime);
					
					try {
						if(waitTime > 0.0)
						{
							Thread.sleep((int) waitTime * 1000);
						}
						
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return;
					}
					
				}
				
			} finally
			{
				mysqlPersistence.resetUnfinishedRuns();
			}
			
		
		
		
		
		
	}

	
}
