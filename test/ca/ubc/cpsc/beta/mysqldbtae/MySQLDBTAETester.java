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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.beust.jcommander.ParameterException;

import ca.ubc.cs.beta.TestHelper;
import ca.ubc.cs.beta.aeatk.algorithmexecutionconfiguration.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aeatk.algorithmrun.RunResult;
import ca.ubc.cs.beta.aeatk.configspace.ParamConfiguration;
import ca.ubc.cs.beta.aeatk.configspace.ParamConfigurationSpace;
import ca.ubc.cs.beta.aeatk.configspace.ParamFileHelper;
import ca.ubc.cs.beta.aeatk.options.AbstractOptions;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aeatk.runconfig.RunConfig;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.WaitableTAECallback;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.decorators.debug.EqualTargetAlgorithmEvaluatorTester;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.decorators.resource.BoundedTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.exceptions.TargetAlgorithmAbortException;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.experimental.queuefacade.basic.BasicTargetAlgorithmEvaluatorQueue;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.experimental.queuefacade.basic.BasicTargetAlgorithmEvaluatorQueueResultContext;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.experimental.queuefacade.general.TargetAlgorithmEvaluatorQueueFacade;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.init.TargetAlgorithmEvaluatorLoader;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.exceptions.PoolChangedException;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistenceUtil;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorkerOptions;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorkerTaskProcessor;
import ca.ubc.cs.beta.targetalgorithmevaluator.EchoTargetAlgorithmEvaluator;
import ec.util.MersenneTwister;

@SuppressWarnings("unused")
public class MySQLDBTAETester {

	
	private static Process proc;
	
	private static AlgorithmExecutionConfiguration execConfig;

	private static ParamConfigurationSpace configSpace;
	
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
			b.append(" --timeLimit 1d --idleLimit 5s");
			b.append(" --tae PARAMECHO --runsToBatch 200 --delayBetweenRequests 1 " );
			
			b.append("--mysql-hostname ").append(mysqlConfig.host).append(" --mysql-password ").append(mysqlConfig.password).append(" --mysql-database ").append(mysqlConfig.databaseName).append(" --mysql-username ").append(mysqlConfig.username).append(" --mysql-port ").append(mysqlConfig.port);
			
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
		execConfig = new AlgorithmExecutionConfiguration("ignore", System.getProperty("user.dir"), configSpace, false, false, 500);
		rand = new MersenneTwister();

		
	}
	
	
	@Test
	public void testRetrieval()
	{
		
		
			MySQLTargetAlgorithmEvaluator mysqlDBTae = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, 25, true, MYSQL_PERMANENT_RUN_PARTITION, false, priority);
			
			
			
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
					RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 1001, config,execConfig);
					runConfigs.add(rc);
				}
			}
			
			System.out.println("Performing " + runConfigs.size() + " runs");
			TargetAlgorithmEvaluator tae = new EqualTargetAlgorithmEvaluatorTester(mysqlDBTae, new EchoTargetAlgorithmEvaluator());
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
	
			MySQLTargetAlgorithmEvaluator mysqlDBTae = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL,  25, true, MYSQL_RUN_PARTITION, true, priority);
			
			
			
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
					RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 1001, config,execConfig);
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
					throw new IllegalStateException("Yo Yo Yo! (This is okay and on purpose, please ignore this exception it's not really a problem)");
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
		
			MySQLTargetAlgorithmEvaluator mysqlDBTae = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL,  25, true, MYSQL_PERMANENT_RUN_PARTITION, false, priority);
			
			
			
			List<RunConfig> runConfigs = new ArrayList<RunConfig>(5);
			for(int i=0; i < 5; i++)
			{
				ParamConfiguration config = configSpace.getDefaultConfiguration();
			
				config.put("seed", String.valueOf(i));
				config.put("runtime", String.valueOf(2*i+1));
				RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("RunPartition"), Long.valueOf(config.get("seed"))), 1001, config,execConfig);
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
			
			tae = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL,  25, true, MYSQL_PERMANENT_RUN_PARTITION+1, false, priority);

			
			//Different RunPartition
			MySQLPersistenceUtil.executeQueryForDebugPurposes("DELETE FROM "+ mysqlConfig.databaseName + "." +  MYSQL_POOL + "_runConfigs WHERE runPartition = " + (Integer.valueOf(MYSQL_PERMANENT_RUN_PARTITION)+1), tae);
			runs = tae.evaluateRun(runConfigs);			
			MySQLPersistenceUtil.executeQueryForDebugPurposes("UPDATE "+ mysqlConfig.databaseName + "." +  MYSQL_POOL + "_runConfigs SET runtime=runtime-1 WHERE runPartition = " + (Integer.valueOf(MYSQL_PERMANENT_RUN_PARTITION)+1), tae);
			
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
			
			MySQLTargetAlgorithmEvaluator mysqlDBTae = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL,  25, true, MYSQL_RUN_PARTITION, true, priority);
			
			
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
						RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 1001, config,execConfig);
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
	
	
	@Test(expected=ParameterException.class)
	public void testAutoDeleteFailOfNegativeRunPartition()
	{
		MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL,  25, true, MYSQL_PERMANENT_RUN_PARTITION, true, priority);
	}
	
	@Test
	public void testAbortDetect() throws InterruptedException
	{		
		
	
		MySQLTargetAlgorithmEvaluator highMySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL,  1500, true, MYSQL_RUN_PARTITION, false, JobPriority.HIGH);		
	
		List<RunConfig> runConfigs = new ArrayList<RunConfig>(TARGET_RUNS_IN_LOOPS);
		
		
		do
		{
			ParamConfiguration config = configSpace.getRandomConfiguration(rand);
			if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED"))
			{
				//Only want good configurations
				continue;
			} else
			{
				config.put("runtime", "0.5");
				config.put("solved", "ABORT");
				RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance","SLEEP"), Long.valueOf(config.get("seed"))), 1001, config,execConfig);
				
				runConfigs.add(rc);
				break;
			}
		} while(true);
		
		BasicTargetAlgorithmEvaluatorQueue taeQueue = new BasicTargetAlgorithmEvaluatorQueue(highMySQLTAE, false);
		
		long time = System.currentTimeMillis();
		List<AlgorithmRun> runs = null;
		taeQueue.evaluateRunAsync(runConfigs);
		
		while(true)
		{
			BasicTargetAlgorithmEvaluatorQueueResultContext result = taeQueue.poll(1, TimeUnit.SECONDS);
			if(result == null)
			{
				System.out.flush();
				System.err.flush();
				System.err.println("IF THIS TEST IS NOT STOPPING, IT HAS FAILED!!");
				System.err.println("IF THIS TEST IS NOT STOPPING, IT HAS FAILED!!");
				System.err.println("IF THIS TEST IS NOT STOPPING, IT HAS FAILED!!");
				System.err.println("IF THIS TEST IS NOT STOPPING, IT HAS FAILED!!");
				System.err.println("IF THIS TEST IS NOT STOPPING, IT HAS FAILED!!");
				System.err.println("IF THIS TEST IS NOT STOPPING, IT HAS FAILED!!");
				System.err.println("IF THIS TEST IS NOT STOPPING, IT HAS FAILED!!");
				System.err.println("IF THIS TEST IS NOT STOPPING, IT HAS FAILED!!");
				System.err.println("IF THIS TEST IS NOT STOPPING, IT HAS FAILED!!");
				System.err.println("IF THIS TEST IS NOT STOPPING, IT HAS FAILED!!");
				System.err.println("IF THIS TEST IS NOT STOPPING, IT HAS FAILED!!");
				System.err.println("IF THIS TEST IS NOT STOPPING, IT HAS FAILED!!");

				System.out.flush();
				System.err.flush();
			}
			else
			{
				assertEquals("Expected aborted exception",TargetAlgorithmAbortException.class,result.getRuntimeException().getClass());
				assertEquals("Expected no results",null, result.getAlgorithmRuns());
				break;
			}
		}
				 //runs = highMySQLTAE.evaluateRun(runConfigs);
			
		

		highMySQLTAE.notifyShutdown();
		
		
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
	

	@Test
	public void testRetrievalBounded()
	{
			
			MySQLTargetAlgorithmEvaluator mysqlDBTae = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL,  25, true, MYSQL_RUN_PARTITION, true, priority);
			
			
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
					RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 1001, config, execConfig);
					runConfigs.add(rc);
				}
			}
			
			System.out.println("Performing " + runConfigs.size() + " runs");
			TargetAlgorithmEvaluator tae = new BoundedTargetAlgorithmEvaluator(mysqlDBTae, 10);
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
	
	
	

	
}
