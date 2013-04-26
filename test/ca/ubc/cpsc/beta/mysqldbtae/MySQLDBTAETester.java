package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ca.ubc.cs.beta.TestHelper;
import ca.ubc.cs.beta.aclib.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.RunResult;
import ca.ubc.cs.beta.aclib.algorithmrun.kill.KillableAlgorithmRun;
import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration;
import ca.ubc.cs.beta.aclib.configspace.ParamConfigurationSpace;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.misc.options.MySQLConfig;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.cli.CommandLineTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.currentstatus.CurrentRunStatusObserver;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.decorators.BoundedTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.decorators.EqualTargetAlgorithmEvaluatorTester;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.deferred.TAECallback;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ca.ubc.cs.beta.targetalgorithmevaluator.EchoTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor;

import ec.util.MersenneTwister;


public class MySQLDBTAETester {

	
	private static Process proc;
	
	private static AlgorithmExecutionConfig execConfig;

	private static  ParamConfigurationSpace configSpace;
	
	private static MySQLConfig mysqlConfig;
	
	private static final String MYSQL_POOL = "juniting2";
	

	private static final int TARGET_RUNS_IN_LOOPS = 50;
	private static final int BATCH_INSERT_SIZE = TARGET_RUNS_IN_LOOPS/10;
	
	private static final int MYSQL_RUN_PARTITION = 0;
	
	//Negative partitions won't be deleted
	private static final int MYSQL_PERMANENT_RUN_PARTITION = -10;
	
	private static Random rand;
	
	private JobPriority priority = JobPriority.HIGH;
	@BeforeClass
	public static void beforeClass()
	{
		
		mysqlConfig = new MySQLConfig();
		mysqlConfig.host = "arrowdb.cs.ubc.ca";
		mysqlConfig.port = 4040;
		mysqlConfig.password = "october-127";
		mysqlConfig.databaseName = "mysql_db_tae";
		mysqlConfig.username = "mysql_db_tae";
		
		try {
			StringBuilder b = new StringBuilder();
			
			b.append("java -cp ");
			b.append(System.getProperty("java.class.path"));
			b.append(" ");
			b.append(MySQLTAEWorker.class.getCanonicalName());
			b.append(" --pool ").append(MYSQL_POOL);
			b.append(" --timeLimit 1d");
			b.append(" --tae PARAMECHO --runsToBatch 200 --delayBetweenRequests 1 " );
			proc = Runtime.getRuntime().exec(b.toString());
			
			InputReader.createReadersForProcess(proc);
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		File paramFile = TestHelper.getTestFile("paramFiles/paramEchoParamFile.txt");
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
					System.err.println("Make sure to run this configuration to fix the database (after truncating the table and one failed run) UPDATE runConfigs_" + MYSQL_POOL + " SET runtime=runtime-1 WHERE runPartition = " + (Integer.valueOf(MYSQL_PERMANENT_RUN_PARTITION)+1));
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
				
				mysqlDBTae.evaluateRunsAsync(runConfigs, new TAECallback() {

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
	public void testProtectionMechanism()
	{
		MySQLPersistenceClient  mysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, 25, true, MYSQL_PERMANENT_RUN_PARTITION,true, priority);
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
}
