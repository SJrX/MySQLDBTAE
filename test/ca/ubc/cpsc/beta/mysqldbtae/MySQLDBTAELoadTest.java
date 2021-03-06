package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.BeforeClass;
import org.junit.Test;

import ca.ubc.cs.beta.TestHelper;
import ca.ubc.cs.beta.aeatk.algorithmexecutionconfiguration.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunconfiguration.AlgorithmRunConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.AlgorithmRunResult;
import ca.ubc.cs.beta.aeatk.options.AbstractOptions;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfigurationSpace;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.init.TargetAlgorithmEvaluatorLoader;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.exceptions.PoolChangedException;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorkerOptions;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorkerTaskProcessor;
import ec.util.MersenneTwister;

@SuppressWarnings("unused")
public class MySQLDBTAELoadTest {



	
	private static Process proc;
	
	private static AlgorithmExecutionConfiguration execConfig;

	private static  ParameterConfigurationSpace configSpace;
	
	private static MySQLOptions mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_loadtest";
	

	private static final int TARGET_RUNS_IN_LOOPS = 5000;
	private static final int BATCH_INSERT_SIZE = TARGET_RUNS_IN_LOOPS/10;
	
	private static final int MYSQL_RUN_PARTITION = 0;
	
	//Negative partitions won't be deleted
	private static final int MYSQL_PERMANENT_RUN_PARTITION = -10;
	
	private static Random rand;
	
	private JobPriority priority = JobPriority.HIGH;
	@BeforeClass
	public static void beforeClass()
	{
		
		mysqlConfig = MySQLDBUnitTestConfig.getMySQLConfig();
		
		
		File paramFile = TestHelper.getTestFile("paramFiles/paramEchoParamFile.txt");
		configSpace = new ParameterConfigurationSpace(paramFile);
		execConfig = new AlgorithmExecutionConfiguration("ignore", System.getProperty("user.dir"), configSpace, false, false, 500);
		rand = new MersenneTwister();

		
	}
	
	
	public void setupWorker()
	{
		/*
		try {
			StringBuilder b = new StringBuilder();
			
			b.append("java -cp ");
			b.append(System.getProperty("java.class.path"));
			b.append(" ");
			b.append(MySQLTAEWorker.class.getCanonicalName());
			b.append(" --pool ").append(MYSQL_POOL);
			b.append(" --timeLimit 1d");
			b.append(" --tae PARAMECHO --runsToBatch 1 --delayBetweenRequests 3 --idleLimit 2m" );
			proc = Runtime.getRuntime().exec(b.toString());
			
			InputReader.createReadersForProcess(proc);
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	
	@Test
	public void testInsertionLoad()
	{

			MySQLTargetAlgorithmEvaluator normalMySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, 1000, true, MYSQL_RUN_PARTITION, true, JobPriority.NORMAL);
			
			
			MySQLTargetAlgorithmEvaluator highMySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, 1000, true, MYSQL_RUN_PARTITION, true, JobPriority.HIGH)	;
			
			
			
			final AtomicInteger completeRuns = new AtomicInteger();
			
			final AtomicBoolean failure = new AtomicBoolean(false);
			
			final AtomicReference<AlgorithmRunConfiguration> ref = new AtomicReference<AlgorithmRunConfiguration>();
			
			final Semaphore complete = new Semaphore(-TARGET_RUNS_IN_LOOPS+1);
			
			PrintStream stream = System.out;
			
			PrintStream stream2 = new PrintStream(new ByteArrayOutputStream());
			
			System.setOut(stream2);
			long total =0;
			double totalDelta = 0.0;
			
			for(int j=0; j < 100; j++)
			{
				List<AlgorithmRunConfiguration> runConfigs = new ArrayList<AlgorithmRunConfiguration>(TARGET_RUNS_IN_LOOPS);
				
				for(int i=0; i < TARGET_RUNS_IN_LOOPS; i++)
				{
					ParameterConfiguration config = configSpace.getRandomParameterConfiguration(rand);
					if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED"))
					{
						//Only want good configurations
						i--;
						continue;
					} else
					{
						AlgorithmRunConfiguration rc = new AlgorithmRunConfiguration(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 1001, config,execConfig);
						
						runConfigs.add(rc);
					}
				}
				
				long time = System.currentTimeMillis();
				highMySQLTAE.evaluateRunsAsync(runConfigs, new TargetAlgorithmEvaluatorCallback() {

					@Override
					public void onSuccess(List<AlgorithmRunResult> runs) {
						//complete.release();
						boolean swap = completeRuns.compareAndSet(0, 1);
						
						if(!swap)
						{
							failure.set(true);
						}
					}

					@Override
					public void onFailure(RuntimeException t) {
						
					}
					
				});
				long endTime = System.currentTimeMillis();
				long delta = endTime - time;
				total += runConfigs.size();
				totalDelta += delta;
				stream.println("Inserting " + runConfigs.size() + " took " + (delta)/1000.0 + " seconds " + " total:" + total  + " time: " + totalDelta/1000.0 + " test:" + total/(totalDelta/1000));
			}
			
			System.setOut(stream);
			assertTrue("Average runtime should be greater than 1000 rows / second", total/(totalDelta/1000.0) > 1000.0);
			

			highMySQLTAE.notifyShutdown();
			normalMySQLTAE.notifyShutdown();
			
	}
	

	
	/*
	try {
		StringBuilder b = new StringBuilder();
		
		b.append("java -cp ");
		b.append(System.getProperty("java.class.path"));
		b.append(" ");
		b.append(MySQLTAEWorker.class.getCanonicalName());
		b.append(" --pool ").append(MYSQL_POOL);
		b.append(" --timeLimit 1d");
		b.append(" --tae PARAMECHO --runsToBatch 1 --delayBetweenRequests 3 --idleLimit 2m" );
		proc = Runtime.getRuntime().exec(b.toString());
		
		InputReader.createReadersForProcess(proc);
		
		
		
	} catch (IOException e) {
		e.printStackTrace();
	}*/
	
	
	private final int THREADS  = 50;
	@Test
	public void testProcessingLoad()
	{
		
		ExecutorService execService = Executors.newCachedThreadPool();
		try 
		{	
		
			
			if(System.currentTimeMillis() > 0)
			{				
				MySQLTargetAlgorithmEvaluator normalMySQLTAE =MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL+"processing", 1500, true, MYSQL_RUN_PARTITION, true, JobPriority.NORMAL)	;
				
				
				MySQLTargetAlgorithmEvaluator highMySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL+"processing", 1500, true, MYSQL_RUN_PARTITION, true, JobPriority.HIGH)	;
				
				
				
				final AtomicInteger completeRuns = new AtomicInteger();
				
				final AtomicBoolean failure = new AtomicBoolean(false);
				
				final AtomicReference<AlgorithmRunConfiguration> ref = new AtomicReference<AlgorithmRunConfiguration>();
				
				final Semaphore complete = new Semaphore(-TARGET_RUNS_IN_LOOPS+1);
				
				PrintStream stream = System.out;
				
				PrintStream stream2 = new PrintStream(new ByteArrayOutputStream());
				
				System.setOut(stream2);
				long total =0;
				double totalDelta = 0.0;
				
				for(int j=0; j < 10; j++)
				{
					List<AlgorithmRunConfiguration> runConfigs = new ArrayList<AlgorithmRunConfiguration>(TARGET_RUNS_IN_LOOPS);
					
					for(int i=0; i < TARGET_RUNS_IN_LOOPS; i++)
					{
						ParameterConfiguration config = configSpace.getRandomParameterConfiguration(rand);
						if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED"))
						{
							//Only want good configurations
							i--;
							continue;
						} else
						{
							AlgorithmRunConfiguration rc = new AlgorithmRunConfiguration(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 1001, config,execConfig);
							
							runConfigs.add(rc);
						}
					}
					
					long time = System.currentTimeMillis();
					highMySQLTAE.evaluateRunsAsync(runConfigs, new TargetAlgorithmEvaluatorCallback() {
		
						@Override
						public void onSuccess(List<AlgorithmRunResult> runs) {
							//complete.release();
							boolean swap = completeRuns.compareAndSet(0, 1);
							
							if(!swap)
							{
								failure.set(true);
							}
						}
		
						@Override
						public void onFailure(RuntimeException t) {
							
						}
						
					});
					long endTime = System.currentTimeMillis();
					long delta = endTime - time;
					total += runConfigs.size();
					totalDelta += delta;
					stream.println("Inserting " + runConfigs.size() + " took " + (delta)/1000.0 + " seconds " + " total:" + total  + " time: " + totalDelta/1000.0 + " test:" + total/(totalDelta/1000));
				}
				
				System.setOut(stream);
				assertTrue("Average runtime should be greater than 1000 rows / second", total/(totalDelta/1000.0) > 1000.0);
				
			}
			final MySQLTAEWorkerOptions options = new MySQLTAEWorkerOptions();
			
			options.mysqlOptions = mysqlConfig;
			options.pool = MYSQL_POOL+"processing";
			options.timeLimit = 86400*1;
			options.idleLimit = 10;
			options.delayBetweenRequests = 1;
			options.taeOptions.targetAlgorithmEvaluator = "PARAMECHO";
			final Map<String, AbstractOptions> opts = TargetAlgorithmEvaluatorLoader.getAvailableTargetAlgorithmEvaluators();
			
			CountDownLatch latch = new CountDownLatch(1);
			
			
			
			final List<MySQLTAEWorkerTaskProcessor> processors = Collections.synchronizedList(new ArrayList<MySQLTAEWorkerTaskProcessor>());
			for(int i=0; i < THREADS; i++)
			{
			
				MySQLTAEWorkerTaskProcessor taeTaskProcessor = new MySQLTAEWorkerTaskProcessor(System.currentTimeMillis()/1000, options, opts, latch);
				processors.add(taeTaskProcessor);
					
			}
			
			
			
			for(int i=0; i < THREADS; i++)
			{
				final int myInt= i;
				Runnable run = new Runnable()
				{
					@Override
					public void run()
					{
						MySQLTAEWorkerTaskProcessor taeTaskProcessor = processors.get(myInt);
						try {
							
							
							
							
							taeTaskProcessor.process();
						} catch (PoolChangedException e) {
							//Can't happen
							throw new IllegalStateException(e);
						}
						
						double averageTime = taeTaskProcessor.getTotalRunFetchTimeInMS() / (double) taeTaskProcessor.getTotalRunFetchRequests();
						System.out.println("Average time per request " +  averageTime + " ms (number): " + taeTaskProcessor.getTotalRunFetchRequests());
					}
					
				};
				execService.submit(run);
			}
			
			latch.countDown();
			
			while(true)
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
				
				long totalTime = 0; 
				double totalRequests = 0;
				
				for(MySQLTAEWorkerTaskProcessor taeProc : processors)
				{
					totalTime += taeProc.getTotalRunFetchTimeInMS();
					totalRequests += taeProc.getTotalRunFetchRequests();
				}
				double avg = totalTime / (double) totalRequests;
				
				for(int i=0; i < THREADS; i++)
				{
					System.err.println("Average time per request " +  avg + " ms (number): " + totalRequests);
				}
				
				
				
			}
		} finally
		{
			execService.shutdownNow();
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	public void assertDEquals(String d1, double d2, double delta)
	{
		assertDEquals(Double.valueOf(d1), d2, delta);
	}
	public void assertDEquals(String d1, String d2, double delta)
	{
		assertDEquals(Double.valueOf(d1), Double.valueOf(d2), delta);
	}
	
	
	public void assertDEquals(double d1, double d2, double delta)
	{
		if(d1 - d2 > delta) throw new AssertionError("Expected "  + (d1 - d2)+ " < " + delta);
		if(d2 - d1 > delta) throw new AssertionError("Expected "  + (d1 - d2)+ " < " + delta);
		
	}
	
	
}
