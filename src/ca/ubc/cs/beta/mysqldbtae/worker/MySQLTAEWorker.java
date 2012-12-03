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
import ca.ubc.cs.beta.aclib.misc.watch.AutoStartStopWatch;
import ca.ubc.cs.beta.aclib.misc.watch.StopWatch;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorBuilder;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class MySQLTAEWorker {

	private static final Logger log = LoggerFactory.getLogger(MySQLTAEWorker.class);
	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
	
		
		
		
		
		MySQLTAEWorkerOptions options = new MySQLTAEWorkerOptions();
		
		JCommander com = new JCommander(options, true, true);
		com.setProgramName("mysqltaeworker");
		int exceptionCount = 0;
		try {
			com.parse(args);
			
			List<String> names = TargetAlgorithmEvaluatorBuilder.getAvailableTargetAlgorithmEvaluators(options.taeOptions);
			
			for(String name : names)
			{
				log.info("Target Algorithm Evaluator Available {} ", name);
			}
			
			
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
			com.usage();
			System.out.flush();
			
			log.error("Error occured parsing arguments", e.getMessage());
			
			
		}
	}
	
	public static void processRuns(MySQLTAEWorkerOptions options)
	{
		
			MySQLPersistence mysqlPersistence = new MySQLPersistence(options.mysqlOptions);
		
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
							algorithmRuns.addAll(tae.evaluateRun(runConfig));							
						} catch(Exception e)
						{
							algorithmRuns.add(new ExistingAlgorithmRun(execConfig,runConfig,"ABORT, 0.0 ,0 ,0, " + runConfig.getProblemInstanceSeedPair().getSeed() + "," + e.getClass() , runWatch.stop()));
						}
						
						
					}
					
				}
				
				if(zeroJobs)
				{
					log.info("No jobs in database");
				}
				
				log.info("Saving results");
				mysqlPersistence.setRunResults(algorithmRuns);
				
				long loopStop = loopStart.stop();
				
				double waitTime = (options.delayBetweenRequests) - loopStop/1000.0;
				log.info("Processing results took {} seconds, waiting for {} seconds", loopStop / 1000.0, waitTime);
				
				try {
					Thread.sleep((int) waitTime * 1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
				
			}
		
		
		
		
		
		
	}

	
}
