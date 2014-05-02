package ca.ubc.cs.beta.mysqldbtae.worker;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aeatk.misc.jcommander.JCommanderHelper;
import ca.ubc.cs.beta.aeatk.misc.returnvalues.AEATKReturnValues;
import ca.ubc.cs.beta.aeatk.misc.spi.SPIClassLoaderHelper;
import ca.ubc.cs.beta.aeatk.misc.version.VersionTracker;
import ca.ubc.cs.beta.aeatk.options.AbstractOptions;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.init.TargetAlgorithmEvaluatorLoader;
import ca.ubc.cs.beta.mysqldbtae.exceptions.PoolChangedException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class MySQLTAEWorker {

	private static Logger log;//Do not initialize here until after the logging environment variables are started
	
	/**
	 * @param args
	 */
	
	private static final long startTimeSecs = System.currentTimeMillis() / 1000;
	
	
	public static String getStringForPossibleNull(String possiblyNull)
	{
		return getStringForPossibleNull(possiblyNull, "", "");
	}
	
	public static String getStringForPossibleNull(String possiblyNull, String pre, String post)
	{
		if(possiblyNull == null)
		{
			return "";
		} 
		
		if(possiblyNull.trim().length() == 0)
		{
			return "";
		} else
		{
			return possiblyNull.trim();
		}
	}
	
	public static void main(String[] args) {
		
		MySQLTAEWorkerOptions options = new MySQLTAEWorkerOptions();
		Map<String,AbstractOptions> taeOptions = TargetAlgorithmEvaluatorLoader.getAvailableTargetAlgorithmEvaluators();
		
		
		options.maxRunsToBatch = Math.max(options.maxRunsToBatch, options.runsToBatch);
		options.minRunsToBatch = Math.min(options.minRunsToBatch, options.runsToBatch);
		if(options.autoAdjustRuns == null)
		{
			if(options.runsToBatch == 1)
			{
				options.autoAdjustRuns = false;
			} else
			{
				options.autoAdjustRuns = true;
			}
		}
		JCommander com = null;
		int exceptionCount = 0;
		try {
			try {
				 com = JCommanderHelper.parseCheckingForHelpAndVersion(args, options, taeOptions);
				com.setProgramName("mysqltaeworker");
				
			} finally
			{
				
				String jobID = "";
				jobID = getStringForPossibleNull(System.getenv("JOB_ID"));
				
				jobID += getStringForPossibleNull(System.getenv("SGE_TASK_ID"),"[","]");
				
				jobID += getStringForPossibleNull(System.getenv("PBS_JOBID"));
				
				jobID += getStringForPossibleNull(System.getenv("PBS_ARRAYID"),"[","]");
				
				
				
				if(!options.jobID.equals("CLI"))
				{
					jobID = options.jobID;
				} else if(jobID.trim().length() == 0)
				{
					jobID= "CLI";
				}
				String workerID = jobID  + "/" + ManagementFactory.getRuntimeMXBean().getName();
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
			System.exit(AEATKReturnValues.PARAMETER_EXCEPTION);
		}
		
		log.info("Main Method Ended");
		
		
		//Checking to make sure no TAE threads are around, we will eventually terminate after 60 seconds
		//as this is better than wasting a bunch of CPU time.
		
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		
		for(int i=0; i < 600; i++)
		{
			int nonDaemonThreadCount = threadMXBean.getThreadCount() - threadMXBean.getDaemonThreadCount();
			if(nonDaemonThreadCount <= 1)
			{ //This main thread is the only thread left
				break;
			} else
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
				
			
				if(i < 50 )
				{
					//Don't log anything for the first 5 seconds
					//System.out.println(".");
					continue;
				}
				
				
				StringBuilder nonDaemonThreads = new StringBuilder();
				
				for(Thread t : getAllThreads())
				{
					
					if(!t.isDaemon())
					{
						nonDaemonThreads.append("Name:["+t.getName() + "], State:[" + t.getState()+ "]\n");
					}
				}
				
				log.warn("There seems to be many non-daemon threads around {}. Chances are some target algorithm evaluator has some threads around or wasn't cleaned up properly:\n{}", nonDaemonThreadCount, nonDaemonThreads.toString());
			}
			
			
		}
			log.info("Shutdown");
		System.exit(AEATKReturnValues.SUCCESS);
	}
	
	private static Thread[] getAllThreads( ) {
	    final ThreadGroup root = getRootThreadGroup( );
	    final ThreadMXBean thbean = ManagementFactory.getThreadMXBean( );
	    int nAlloc = thbean.getThreadCount( );
	    int n = 0;
	    Thread[] threads;
	    do {
	        nAlloc *= 2;
	        threads = new Thread[ nAlloc ];
	        n = root.enumerate( threads, true );
	    } while ( n == nAlloc );
	    return java.util.Arrays.copyOf( threads, n );
	}
	
 
	private static ThreadGroup getRootThreadGroup( ) {
	    ThreadGroup tg = Thread.currentThread( ).getThreadGroup( );
	    ThreadGroup ptg;
	    while ( (ptg = tg.getParent( )) != null )
	        tg = ptg;
	    return tg;
	}
	
	
}
