

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aclib.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.ExistingAlgorithmRun;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.misc.version.VersionTracker;
import ca.ubc.cs.beta.aclib.misc.watch.AutoStartStopWatch;
import ca.ubc.cs.beta.aclib.misc.watch.StopWatch;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorBuilder;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorkerOptions;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Tester {

	private static final Logger log = LoggerFactory.getLogger(Tester.class);
	private static final int THREADS = 8;
	private static final CountDownLatch latch = new CountDownLatch(THREADS+1);
	private static final CountDownLatch doneLatch = new CountDownLatch(THREADS + 1);
	
	private static final int TOTAL_INSERTS = 10000;
	private static final boolean DISABLE_OUTPUT = false;
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	
	private static final AtomicLong totalInserts = new AtomicLong();
	
	private static volatile PrintStream mainOut;
	
	public static void main(final String[] args) throws InterruptedException, IOException {
	
		
		
		MySQLTAEWorkerOptions options = new MySQLTAEWorkerOptions();
		
		JCommander com = new JCommander(options, true, true);
		com.setProgramName("mysqltaeworker");
		int exceptionCount = 0;
		try {
			com.parse(args);
			
		} catch(ParameterException e)
		{
			com.usage();
			System.out.flush();
			
			log.error("Error occured parsing arguments {}", e.getMessage());
			System.exit(0);
		}
		
		
		ExecutorService execService = Executors.newCachedThreadPool();
		
		for(int i=0; i < THREADS; i++)
		{
			Callable<Void> r = new Callable<Void>()
			{
					
						public void run()
						{
						
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
								
								
								
								
								
								boolean done=false;
								while(!done && (exceptionCount < options.uncaughtExceptionLimit))
								{
									try {
										processRuns(options);
										done = true;
										log.info("Done work");
									} catch(Exception e)
									{
										
										mainOut.println(e);
										e.printStackTrace(mainOut);
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
								
								log.error("Error occured parsing arguments {}", e.getMessage());
								
								
							}
							
							doneLatch.countDown();
							log.info("Tick {} left", doneLatch.getCount());
							
						}

						@Override
						public Void call() throws Exception {
							run();
							return null;
						}
					};
					
				execService.submit(r);
					
		}
		
		System.out.println("Latch Count " + latch.getCount());
		System.out.println("Done Latch Count " + doneLatch.getCount());
		PrintStream out = System.out;
		log.info("Starting");
		if(DISABLE_OUTPUT)
		{
			System.setOut(new NullPrintStream());
		}
		mainOut = out;
		latch.countDown();
		latch.await();
		long startTime = System.currentTimeMillis();
	
		doneLatch.countDown();
		doneLatch.await();
		System.setOut(out);
		
		
		log.info("Done");
		System.out.println("Latch Count " + latch.getCount());
		System.out.println("Done Latch Count " + doneLatch.getCount());
		
		execService.shutdown();
		System.out.println("Test");
		System.err.println("Total time is secs "+ (System.currentTimeMillis() - startTime)/1000.0);
		
		log.info("Total inserts {}", totalInserts.get());
		log.info("Total Throughput {} / second ", totalInserts.get() / ((System.currentTimeMillis() - startTime)/1000.0) ); 
				
				
		System.out.println("Test2");
		System.out.flush();
		System.err.flush();
		
		
		
		
		
	}
	
	public static void processRuns(MySQLTAEWorkerOptions options)
	{
			
		
			MySQLPersistence mysqlPersistence = new MySQLPersistence(options.mysqlOptions,options.pool,100);
		
			Map<AlgorithmExecutionConfig, TargetAlgorithmEvaluator> taeMap = new HashMap<AlgorithmExecutionConfig, TargetAlgorithmEvaluator>();
			
			log.info("Waiting for Work");
			int i=0;
			while(true)
			{
				
				//mysqlPersistence.startTransaction();
				Map<AlgorithmExecutionConfig, List<RunConfig>> runs = null;
				try {
					try {
						latch.countDown();
						try {
						latch.await();
						}catch(Exception e)
						{
							return;
						}
						runs = mysqlPersistence.getRuns(options.runsToBatch);
						totalInserts.addAndGet(1);
					} catch(Exception e)
					{
						
						e.printStackTrace();
					}
				} finally
				{
					if(runs != null)
					{
						log.info("Done {}", runs.size());
					}
					//if(true) return;
				}
				
				
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
							log.error("Exception occured while running algorithm" ,e);
							algorithmRuns.add(new ExistingAlgorithmRun(execConfig,runConfig,"ABORT, 0.0 ,0 ,0, " + runConfig.getProblemInstanceSeedPair().getSeed() + "," + e.getClass() , runWatch.stop()));
						}
						
						
					}
					
				}
				
				if(zeroJobs)
				{
					log.info("No jobs in database");
				}
				
				log.info("Saving {} results", algorithmRuns.size());
				mysqlPersistence.setRunResults(algorithmRuns);
				
				long loopStop = loopStart.stop();
				
				double waitTime = (options.delayBetweenRequests) - loopStop/1000.0;
				log.info("Processing results took {} seconds, waiting for {} seconds", loopStop / 1000.0, waitTime);
				i++;
				try {
					//mysqlPersistence.commitTransaction();
					//waitTime = 0;
					if((waitTime > 0.0))
					{
						Thread.sleep((int) waitTime * 1000);
						
					}
					
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
				
				if(i >= TOTAL_INSERTS) return;
				
			}
		
		
		
		
		
		
	}

	
}
