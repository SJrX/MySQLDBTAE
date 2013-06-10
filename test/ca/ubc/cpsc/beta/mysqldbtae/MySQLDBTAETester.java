package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.ubc.cs.beta.TestHelper;
import ca.ubc.cs.beta.aclib.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration;
import ca.ubc.cs.beta.aclib.configspace.ParamConfigurationSpace;
import ca.ubc.cs.beta.aclib.configspace.ParamFileHelper;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.options.AbstractOptions;
import ca.ubc.cs.beta.aclib.options.MySQLOptions;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.WaitableTAECallback;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.decorators.debug.EqualTargetAlgorithmEvaluatorTester;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.decorators.helpers.BoundedTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.init.TargetAlgorithmEvaluatorLoader;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.exceptions.PoolChangedException;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistenceUtil;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorkerOptions;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorkerTaskProcessor;
import ca.ubc.cs.beta.targetalgorithmevaluator.EchoTargetAlgorithmEvaluator;
import ec.util.MersenneTwister;

@SuppressWarnings("unused")
public class MySQLDBTAETester {

	
	private static Process proc;
	
	private static AlgorithmExecutionConfig execConfig;

	private static  ParamConfigurationSpace configSpace;
	
	private static MySQLOptions mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_tae_test";
	

	private static final int TARGET_RUNS_IN_LOOPS = 50;
	private static final int BATCH_INSERT_SIZE = TARGET_RUNS_IN_LOOPS/10;
	
	private static final int MYSQL_RUN_PARTITION = 0;
	
	//Negative partitions won't be deleted
	private static final int MYSQL_PERMANENT_RUN_PARTITION = -10;
	
	private static final AtomicBoolean lastLineSeen = new AtomicBoolean(false);
	private static Random rand;
	
	private JobPriority priority = JobPriority.HIGH;
	@BeforeClass
	public static void beforeClass()
	{
		mysqlConfig = MySQLDBUnitTestConfig.getMySQLConfig();
		
		try {
			StringBuilder b = new StringBuilder();
			
			b.append("java -cp ");
			b.append(System.getProperty("java.class.path"));
			b.append(" ");
			b.append(MySQLTAEWorker.class.getCanonicalName());
			b.append(" --pool ").append(MYSQL_POOL);
			b.append(" --mysqlDatabase ").append(mysqlConfig.databaseName);
			b.append(" --timeLimit 1d --idleLimit 10s");
			b.append(" --tae PARAMECHO --runsToBatch 200 --delayBetweenRequests 1 " );
			proc = Runtime.getRuntime().exec(b.toString());
			
			InputReader.createReadersForProcess(proc, "WORKER>", new InputReader.LineHandler() 
			{

				@Override
				public void processLine(String line) {
					// TODO Auto-generated method stub
					//System.out.println("Saw: " + line);
					if(line.contains("Main Method Ended"))
					{
						System.out.println("Setting last line to true");
						lastLineSeen.set(true);
					}
				}
				
			});
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		File paramFile = TestHelper.getTestFile("paramFiles/paramEchoParamFileWithKilled.txt");
		configSpace = new ParamConfigurationSpace(paramFile);
		execConfig = new AlgorithmExecutionConfig("ignore", System.getProperty("user.dir"), configSpace, false, false, 500);
		rand = new MersenneTwister();

		
	}
	
	
	@Test
	public void testRetrieval()
	{
		
			
			MySQLPersistenceClient  mysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, 25, true,MYSQL_RUN_PARTITION,true, priority);
			try {
			mysqlPersistence.setCommand(System.getProperty("sun.java.command"));
			} catch(RuntimeException e)
			{
				e.printStackTrace();
				throw e;
			}
			mysqlPersistence.setAlgorithmExecutionConfig(execConfig);
			
			MySQLTargetAlgorithmEvaluator mysqlDBTae = new MySQLTargetAlgorithmEvaluator(execConfig, mysqlPersistence);
			
			
			
			List<RunConfig> runConfigs = new ArrayList<RunConfig>(TARGET_RUNS_IN_LOOPS);
			for(int i=0; i < TARGET_RUNS_IN_LOOPS; i++)
			{
				ParamConfiguration config = configSpace.getRandomConfiguration(rand);
				if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED"))
				{
					//Only want good configurations
					i--;
					continue;
				} else
				{
					RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 1001, config);
					runConfigs.add(rc);
				}
			}
			
			System.out.println("Performing " + runConfigs.size() + " runs");
			TargetAlgorithmEvaluator tae = new EqualTargetAlgorithmEvaluatorTester(mysqlDBTae, new EchoTargetAlgorithmEvaluator(execConfig));
			List<AlgorithmRun> runs = tae.evaluateRun(runConfigs);
			
			
			for(AlgorithmRun run : runs)
			{
				ParamConfiguration config  = run.getRunConfig().getParamConfiguration();
				assertDEquals(config.get("runtime"), run.getRuntime(), 0.1);
				assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
				assertDEquals(config.get("quality"), run.getQuality(), 0.1);
				assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
				assertEquals(config.get("solved"), run.getRunResult().name());
				//This executor should not have any additional run data
				assertEquals("",run.getAdditionalRunData());

			}
			
			
			mysqlDBTae.notifyShutdown();
	}
	

	
	@Test
	public void testOnSuccessExceptionCallsonFailure()
	{
		
			
			MySQLPersistenceClient  mysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, 25, true,MYSQL_RUN_PARTITION,true, priority);
			try {
			mysqlPersistence.setCommand(System.getProperty("sun.java.command"));
			} catch(RuntimeException e)
			{
				e.printStackTrace();
				throw e;
			}
			mysqlPersistence.setAlgorithmExecutionConfig(execConfig);
			
			MySQLTargetAlgorithmEvaluator mysqlDBTae = new MySQLTargetAlgorithmEvaluator(execConfig, mysqlPersistence);
			
			
			
			List<RunConfig> runConfigs = new ArrayList<RunConfig>(TARGET_RUNS_IN_LOOPS);
			for(int i=0; i < TARGET_RUNS_IN_LOOPS; i++)
			{
				ParamConfiguration config = configSpace.getRandomConfiguration(rand);
				if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED"))
				{
					//Only want good configurations
					i--;
					continue;
				} else
				{
					RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 1001, config);
					runConfigs.add(rc);
				}
			}
			
			System.out.println("Performing " + runConfigs.size() + " runs");
			TargetAlgorithmEvaluator tae = mysqlDBTae;
			final AtomicBoolean onFailure = new AtomicBoolean(false);
			TargetAlgorithmEvaluatorCallback callback = new TargetAlgorithmEvaluatorCallback()
			{

				@Override
				public void onSuccess(List<AlgorithmRun> runs) {
					System.out.println("Throwing an exception in onSuccess");
					throw new IllegalStateException("Yo Yo Yo!");
				}

				@Override
				public void onFailure(RuntimeException t) {
					onFailure.set(true);
				}
				
			};
			WaitableTAECallback wait = new WaitableTAECallback(callback);
			
			tae.evaluateRunsAsync(runConfigs, wait);
			
			wait.waitForCompletion();
			assertTrue("onFailure was expected to be invoked", onFailure.get());
			
			 
			mysqlDBTae.notifyShutdown();
	}
	

	
	@Test
	public void testRunPartitions()
	{
		
		
			
			MySQLPersistenceClient  mysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, 25, true,MYSQL_PERMANENT_RUN_PARTITION,false, priority);
			
			try {
			mysqlPersistence.setCommand(System.getProperty("sun.java.command"));
			} catch(RuntimeException e)
			{
				e.printStackTrace();
				throw e;
			}
			mysqlPersistence.setAlgorithmExecutionConfig(execConfig);
			
			MySQLTargetAlgorithmEvaluator mysqlDBTae = new MySQLTargetAlgorithmEvaluator(execConfig, mysqlPersistence);
			
			
			
			List<RunConfig> runConfigs = new ArrayList<RunConfig>(5);
			for(int i=0; i < 5; i++)
			{
				ParamConfiguration config = configSpace.getDefaultConfiguration();
			
				config.put("seed", String.valueOf(i));
				config.put("runtime", String.valueOf(2*i+1));
				RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("RunPartition"), Long.valueOf(config.get("seed"))), 1001, config);
				runConfigs.add(rc);
				
			}
			
			System.out.println("Performing " + runConfigs.size() + " runs");
			TargetAlgorithmEvaluator tae = mysqlDBTae;
			
			List<AlgorithmRun> runs = tae.evaluateRun(runConfigs);
			
			
			for(AlgorithmRun run : runs)
			{
				ParamConfiguration config  = run.getRunConfig().getParamConfiguration();
				assertDEquals(config.get("runtime"), run.getRuntime(), 0.1);
				assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
				assertDEquals(config.get("quality"), run.getQuality(), 0.1);
				assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
				assertEquals(config.get("solved"), run.getRunResult().name());
				//This executor should not have any additional run data
				assertEquals("",run.getAdditionalRunData());

			}
			
			//Same runPartition
			runs = tae.evaluateRun(runConfigs);
			for(AlgorithmRun run : runs)
			{
				ParamConfiguration config  = run.getRunConfig().getParamConfiguration();
				assertDEquals(config.get("runtime"), run.getRuntime(), 0.1);
				assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
				assertDEquals(config.get("quality"), run.getQuality(), 0.1);
				assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
				assertEquals(config.get("solved"), run.getRunResult().name());
				//This executor should not have any additional run data
				assertEquals("",run.getAdditionalRunData());

			}
			
			
			mysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, 25, true,MYSQL_PERMANENT_RUN_PARTITION+1,false, priority);
			try {
			mysqlPersistence.setCommand(System.getProperty("sun.java.command"));
			} catch(RuntimeException e)
			{
				e.printStackTrace();
				throw e;
			}
			mysqlPersistence.setAlgorithmExecutionConfig(execConfig);
			
			tae = new MySQLTargetAlgorithmEvaluator(execConfig, mysqlPersistence);

			
			//Different RunPartition
			MySQLPersistenceUtil.executeQueryForDebugPurposes("DELETE FROM "+ mysqlConfig.databaseName + ".runConfigs_" +  MYSQL_POOL + " WHERE runPartition = " + (Integer.valueOf(MYSQL_PERMANENT_RUN_PARTITION)+1), mysqlPersistence);
			runs = tae.evaluateRun(runConfigs);			
			MySQLPersistenceUtil.executeQueryForDebugPurposes("UPDATE "+ mysqlConfig.databaseName + ".runConfigs_" +  MYSQL_POOL + " SET runtime=runtime-1 WHERE runPartition = " + (Integer.valueOf(MYSQL_PERMANENT_RUN_PARTITION)+1), mysqlPersistence);
			
			runs = tae.evaluateRun(runConfigs);
			for(AlgorithmRun run : runs)
			{
				ParamConfiguration config  = run.getRunConfig().getParamConfiguration();
				//THIS Test will fail
				//MODIFY THE DB, and set this RunPartition value to have the -1 as a value.
				try {
				assertDEquals(config.get("runtime"), run.getRuntime()+1, 0.1);
				} catch(AssertionError e)
				{
					System.err.println("Make sure to run this configuration to fix the database (after truncating the table and one failed run) UPDATE "+ mysqlConfig.databaseName + ".runConfigs_" +  MYSQL_POOL + " SET runtime=runtime-1 WHERE runPartition = " + (Integer.valueOf(MYSQL_PERMANENT_RUN_PARTITION)+1));
					throw e;
				}
				assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
				assertDEquals(config.get("quality"), run.getQuality(), 0.1);
				assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
				assertEquals(config.get("solved"), run.getRunResult().name());
				//This executor should not have any additional run data
				assertEquals("",run.getAdditionalRunData());

			}
						
			
			mysqlDBTae.notifyShutdown();
	}
	
	

	@Test
	public void testAsyncRetrieval()
	{
		
			final AtomicReference<RuntimeException> myRef = new AtomicReference<RuntimeException>();
			final int TEST_COUNT = 50;
			
			final CountDownLatch latch = new CountDownLatch(TEST_COUNT);
			
			final AtomicInteger runsCompleted = new AtomicInteger(0);
			
			MySQLPersistenceClient  mysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, 25,true,MYSQL_RUN_PARTITION,true,priority);
			try {
			mysqlPersistence.setCommand(System.getProperty("sun.java.command"));
			} catch(RuntimeException e)
			{
				e.printStackTrace();
				throw e;
			}
			mysqlPersistence.setAlgorithmExecutionConfig(execConfig);
			
			MySQLTargetAlgorithmEvaluator mysqlDBTae = new MySQLTargetAlgorithmEvaluator(execConfig, mysqlPersistence);
			
			
			for(int i=0; i < TEST_COUNT; i++)
			{
				final List<RunConfig> runConfigs = new ArrayList<RunConfig>(TARGET_RUNS_IN_LOOPS);
				
				for(int j=0; j <= i; j++)
				{
					ParamConfiguration config = configSpace.getRandomConfiguration(rand);
					if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED"))
					{
						//Only want good configurations
						j--;
						continue;
					} else
					{
						RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 1001, config);
						runConfigs.add(rc);
					}
				}
				
				mysqlDBTae.evaluateRunsAsync(runConfigs, new TargetAlgorithmEvaluatorCallback() {

					@Override
					public void onSuccess(List<AlgorithmRun> runs) {
						// TODO Auto-generated method stub
						
						try {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						for(AlgorithmRun run : runs)
						{
							ParamConfiguration config  = run.getRunConfig().getParamConfiguration();
							assertDEquals(config.get("runtime"), run.getRuntime(), 0.1);
							assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
							assertDEquals(config.get("quality"), run.getQuality(), 0.1);
							assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
							assertEquals(config.get("solved"), run.getRunResult().name());
							//This executor should not have any additional run data
							assertEquals("",run.getAdditionalRunData());
							runsCompleted.incrementAndGet();

						}
						} catch(RuntimeException e)
						{
							myRef.set(e);
						}
						latch.countDown();
					}

					@Override
					public void onFailure(RuntimeException t) {
						myRef.set(t);
						latch.countDown();
					}
					
					
				});
				
			}
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
			
			RuntimeException e = myRef.get();
			
			if(e != null)
			{
				throw e;
			}
			assertEquals((TEST_COUNT*(TEST_COUNT+1))/2,runsCompleted.get());
			//System.out.println("Um I really did " + runsCompleted.get());
			
			mysqlDBTae.notifyShutdown();
		
	}
	
	
	@Test
	public void testRetrievalBounded()
	{
		
			
			MySQLPersistenceClient  mysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, 25, true, MYSQL_RUN_PARTITION,true, priority);
			try {
			mysqlPersistence.setCommand(System.getProperty("sun.java.command"));
			} catch(RuntimeException e)
			{
				e.printStackTrace();
				throw e;
			}
			mysqlPersistence.setAlgorithmExecutionConfig(execConfig);
			
			MySQLTargetAlgorithmEvaluator mysqlDBTae = new MySQLTargetAlgorithmEvaluator(execConfig, mysqlPersistence);
			
			
			List<RunConfig> runConfigs = new ArrayList<RunConfig>(TARGET_RUNS_IN_LOOPS);
			for(int i=0; i < TARGET_RUNS_IN_LOOPS; i++)
			{
				ParamConfiguration config = configSpace.getRandomConfiguration(rand);
				if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED"))
				{
					//Only want good configurations
					i--;
					continue;
				} else
				{
					RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 1001, config);
					runConfigs.add(rc);
				}
			}
			
			System.out.println("Performing " + runConfigs.size() + " runs");
			TargetAlgorithmEvaluator tae = new BoundedTargetAlgorithmEvaluator(mysqlDBTae, 10,execConfig);
			List<AlgorithmRun> runs = tae.evaluateRun(runConfigs);
			
			
			for(AlgorithmRun run : runs)
			{
				ParamConfiguration config  = run.getRunConfig().getParamConfiguration();
				assertDEquals(config.get("runtime"), run.getRuntime(), 0.1);
				assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
				assertDEquals(config.get("quality"), run.getQuality(), 0.1);
				assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
				assertEquals(config.get("solved"), run.getRunResult().name());
				//This executor should not have any additional run data
				assertEquals("",run.getAdditionalRunData());

			}
			
			mysqlDBTae.notifyShutdown();
			
	}
	@Test(expected=IllegalArgumentException.class)
	public void testAutoDeleteFailOfNegativeRunPartition()
	{
		new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, 25, true, MYSQL_PERMANENT_RUN_PARTITION,true, priority);
	}
	
	
	
	
	@AfterClass
	public static void afterClass()
	{
		if(proc != null)
		{
			proc.destroy();
		
			try {
				proc.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Process destroyed");
		} else
		{
			System.err.println("No process destroyed");
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * This tests that the worker will eventually idle timeout at the appropriate time
	 * 
	 * NOTE: THIS MUST BE THE LAST TEST IN THE FILE SINCE IT WILL KILL THE WORKER
	 * 
	 * Essentially we sleep for 5 seconds,
	 * submit a job,
	 * then make sure after 15 more seconds the worker has exited
	 * 
	 * 
	 * This test is incredibly sensitive to timings unfortunately :(
	 */
	@Test
	public void testIdleShutdown()
	{
		
		
		MySQLPersistenceClient  mysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, 25, true, MYSQL_RUN_PARTITION,true, priority);
		try {
		mysqlPersistence.setCommand(System.getProperty("sun.java.command"));
		} catch(RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
		mysqlPersistence.setAlgorithmExecutionConfig(execConfig);
		
		MySQLTargetAlgorithmEvaluator mysqlDBTae = new MySQLTargetAlgorithmEvaluator(execConfig, mysqlPersistence);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			Thread.currentThread().interrupt();
		}
		
		List<RunConfig> runConfigs = new ArrayList<RunConfig>(1);
		for(int i=0; i < 1; i++)
		{
			ParamConfiguration config = configSpace.getRandomConfiguration(rand);
			if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED"))
			{
				//Only want good configurations
				i--;
				continue;
			} else
			{
				RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 1001, config);
				runConfigs.add(rc);
			}
		}
		
		System.out.println("Performing " + runConfigs.size() + " runs");
		TargetAlgorithmEvaluator tae =mysqlDBTae;
		List<AlgorithmRun> runs = tae.evaluateRun(runConfigs);
			
		
		for(int i = 0; i < 8; i++)
		{
			try {
				Thread.sleep(1000);
				try {
					if(proc.exitValue() >= 0)
					{
						fail("Process shouldn't have exited yet");
						return;
					}
				} catch(IllegalThreadStateException e)
				{
					//We have no other way of checking if a process has ended except for waiting for catching this exception if it isn't
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		assertFalse("Expected worker would NOT have exited by now ", lastLineSeen.get());
		System.out.println("Waiting 2 seconds for idle");
		
		try {
			Thread.sleep(4000);
			assertTrue("Expected worker would have exited by now ", lastLineSeen.get());
		}  catch (InterruptedException e) {
			System.out.println("Interrupted");
			Thread.currentThread().interrupt();
		}
		
		
		
	}

	/****
	 * 
	 * DO NOT PUT NEW TESTS HERE, THE PREVIOUS TEST NEEDS TO BE THE LAST ONE BECAUSE IT WILL CAUSE THE WORKER TO TIMEOUT
	 * DO NOT PUT NEW TESTS HERE, THE PREVIOUS TEST NEEDS TO BE THE LAST ONE BECAUSE IT WILL CAUSE THE WORKER TO TIMEOUT
	 * DO NOT PUT NEW TESTS HERE, THE PREVIOUS TEST NEEDS TO BE THE LAST ONE BECAUSE IT WILL CAUSE THE WORKER TO TIMEOUT
	 * DO NOT PUT NEW TESTS HERE, THE PREVIOUS TEST NEEDS TO BE THE LAST ONE BECAUSE IT WILL CAUSE THE WORKER TO TIMEOUT
	 * DO NOT PUT NEW TESTS HERE, THE PREVIOUS TEST NEEDS TO BE THE LAST ONE BECAUSE IT WILL CAUSE THE WORKER TO TIMEOUT
	 * 
	 */

	
	/***
	 * This test is an exception because it doesn't rely on the worker :)
	 * 
	 * 
	 */
	@Test
	/**
	 * This tests that garbage in the database (for instance a para file that doesn't exist
	 * doesn't cause the worker to crash)
	 * 
	 */
	public void testBadExecution()
	{
		ExecutorService execService = Executors.newCachedThreadPool();
		try {
			
			
			final MySQLTAEWorkerOptions options = new MySQLTAEWorkerOptions();
			
			options.pool = MYSQL_POOL+"badexec";
			options.timeLimit = 86400*1;
			options.idleLimit = 10;
			options.delayBetweenRequests = 1;
			options.taeOptions.targetAlgorithmEvaluator = "CLI";
			options.createTables = true;
			final Map<String, AbstractOptions> opts = TargetAlgorithmEvaluatorLoader.getAvailableTargetAlgorithmEvaluators();
			
			CountDownLatch latch = new CountDownLatch(1);
			
			
			
			final List<MySQLTAEWorkerTaskProcessor> processors = Collections.synchronizedList(new ArrayList<MySQLTAEWorkerTaskProcessor>());
			for(int i=0; i < 1; i++)
			{
			
				MySQLTAEWorkerTaskProcessor taeTaskProcessor = new MySQLTAEWorkerTaskProcessor(System.currentTimeMillis()/1000, options, opts, latch);
				processors.add(taeTaskProcessor);
					
			}
			
			
			
			for(int i=0; i < 1; i++)
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
			
	
			
			MySQLPersistenceClient  normalMysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL+"badexec", 1500, true,1,true, JobPriority.NORMAL);
			try {
			normalMysqlPersistence.setCommand(System.getProperty("sun.java.command"));
			} catch(RuntimeException e)
			{
				e.printStackTrace();
				throw e;
			}
			
			try {
				File paramFile = File.createTempFile("junittests", "badexec");
				
				FileWriter writer = new FileWriter(paramFile);
				writer.write("boo { yay, hoo, yeah } [hoo]");
				
				writer.flush();
				writer.close();
				ParamConfigurationSpace configSpace = ParamFileHelper.getParamFileParser(paramFile);
				
				
				AlgorithmExecutionConfig execConfig = new AlgorithmExecutionConfig("ignore", System.getProperty("user.dir"), configSpace, false, false, 500);
				
			normalMysqlPersistence.setAlgorithmExecutionConfig(execConfig);
			
			MySQLTargetAlgorithmEvaluator normalMySQLTAE = new MySQLTargetAlgorithmEvaluator(execConfig, normalMysqlPersistence);
			
			
	
			
			TargetAlgorithmEvaluatorCallback tae = new TargetAlgorithmEvaluatorCallback()
			{
	
				@Override
				public void onSuccess(List<AlgorithmRun> runs) {
	
					System.out.println("Done");
				}
	
				@Override
				public void onFailure(RuntimeException t) {
	
					System.out.println("ROAR");
				}
				
			};
			
			
			WaitableTAECallback wait = new WaitableTAECallback(tae);
			normalMySQLTAE.evaluateRunsAsync(new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("foo"), 123), 0, configSpace.getDefaultConfiguration()), wait);
			
			paramFile.delete();
			/*
			 writer = new FileWriter(paramFile);
			writer.write("boo { yay} [yay]");
			
			writer.flush();
			writer.close();
			*/
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			}
			
			
			latch.countDown();
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			}
			
			
			normalMySQLTAE.notifyShutdown();
			
			
			
			try {
				Thread.sleep(5000);
				if(processors.get(0).getCrashReason() != null)
				{
					processors.get(0).getCrashReason().printStackTrace();
				}
				assertNull("Expected that the worker would not crash but got " + processors.get(0).getCrashReason(), processors.get(0).getCrashReason());
			
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
			
			
			} catch(IOException e)
			{
				e.printStackTrace();
				fail("Unexpected exception occured");
			}
		} finally
		{
			execService.shutdownNow();
			
		}
		
	}
	
	
	
	
	
}
