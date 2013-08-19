package ca.ubc.cs.beta.mysqldbtae.worker;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aclib.misc.jcommander.JCommanderHelper;
import ca.ubc.cs.beta.aclib.misc.spi.SPIClassLoaderHelper;
import ca.ubc.cs.beta.aclib.misc.version.VersionTracker;
import ca.ubc.cs.beta.aclib.options.AbstractOptions;

import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.init.TargetAlgorithmEvaluatorLoader;
import ca.ubc.cs.beta.mysqldbtae.exceptions.PoolChangedException;
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
		Map<String,AbstractOptions> taeOptions = TargetAlgorithmEvaluatorLoader.getAvailableTargetAlgorithmEvaluators();
		
		JCommander com = null;
		int exceptionCount = 0;
		try {
			try {
				 com = JCommanderHelper.parseCheckingForHelpAndVersion(args, options, taeOptions);
				com.setProgramName("mysqltaeworker");
				
			} finally
			{
				String workerID = options.jobID  + "/" + ManagementFactory.getRuntimeMXBean().getName();
				String logLocation = options.logDirectory.getAbsolutePath() + File.separator + "log-worker-"+workerID.replaceAll("[^A-Za-z0-9_]+", "_")+".txt";
				System.setProperty("LOG_LOCATION", logLocation);
				System.out.println("*****************************\nLogging to: " + logLocation +  "\n*****************************");
				
				log = LoggerFactory.getLogger(MySQLTAEWorker.class);;
				if(com != null)
				{
					for(String name : com.getParameterFilesToRead())
					{
						log.info("Parsing (default) options from file: {} ", name);
					}
				}
			}
			
			
			VersionTracker.setClassLoader(SPIClassLoaderHelper.getClassLoader());
			VersionTracker.logVersions();
		
			
			options.taeOptions.turnOffCrashes();
			
			
			
			
			for(String name : taeOptions.keySet())
			{
				log.info("Target Algorithm Evaluator Available {} ", name);
			}
			
			log.info("====== Configuration ======\n{}",options.toString());
			
			
			
			boolean done=false;
			while(!done && (exceptionCount < options.uncaughtExceptionLimit))
			{
				try {
					
					MySQLTAEWorkerTaskProcessor processor = new MySQLTAEWorkerTaskProcessor(startTimeSecs, options, taeOptions);
					
					processor.process();
					
					done = true;
					
					
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
						exceptionCount++;
						log.error("Exception occured",e);
						
						log.info("Uncaught exceptions used {} out of {}", exceptionCount, options.uncaughtExceptionLimit);
						try {
							Thread.sleep( (long) (( 120 + Math.random()*60.0) * 1000) );
						} catch (InterruptedException e1) {
							Thread.interrupted();
							return;
						}
					
					}
					
				}
				

			}
		
		}catch(ParameterException e)
		{
		
		
			
			log.error("Error occured parsing arguments: {}", e.getMessage());
			if(log.isDebugEnabled())
			{
				log.debug("Stack trace", e);
			}
		}
		log.info("Main Method Ended");
	}
	
}
