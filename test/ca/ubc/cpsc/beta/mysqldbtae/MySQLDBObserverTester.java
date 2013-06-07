package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.decorators.helpers.BoundedTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor;

import ec.util.MersenneTwister;

@SuppressWarnings("unused")
public class MySQLDBObserverTester {

	
	private static Process proc;
	
	private static AlgorithmExecutionConfig execConfig;

	private static  ParamConfigurationSpace configSpace;
	
	private static MySQLConfig mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_observer";
	

	private static final int TARGET_RUNS_IN_LOOPS = 50;
	
	private static final int BATCH_INSERT_SIZE = TARGET_RUNS_IN_LOOPS/10;
	
	private static final int MYSQL_RUN_PARTITION = 0;
	
	private  static Random rand;
	
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
			b.append(" --mysqlDatabase ").append(mysqlConfig.databaseName);
			b.append(" --pool ").append(MYSQL_POOL);
			b.append(" --timeLimit 1d");
			b.append(" --tae CLI --runsToBatch 200 --delayBetweenRequests 1 --idleLimit 15s" );
			proc = Runtime.getRuntime().exec(b.toString());
			
			InputReader.createReadersForProcess(proc);
			
			
			Runtime.getRuntime().addShutdownHook(new Thread()
			{
				@Override
				public void run()
				{
					afterClass();
				}
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		File paramFile = TestHelper.getTestFile("paramFiles/paramEchoParamFile.txt");
		configSpace = new ParamConfigurationSpace(paramFile);
		
		StringBuilder b = new StringBuilder();
		b.append("java -cp ");
		b.append(System.getProperty("java.class.path"));
		b.append(" ");
		b.append(TrueSleepyParamEchoExecutor.class.getCanonicalName());
		execConfig = new AlgorithmExecutionConfig(b.toString(), System.getProperty("user.dir"), configSpace, false, false, 0.01);
		
		
		rand = new MersenneTwister();
		

		
	}
	
	


	/**
	 * Tests whether warnings are generated for Algorithms exceeding there runtime
	 */
	@Test
	public void testDynamicAdaptiveCappingSingleRunUnBounded()
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
		
		assertTrue(mysqlDBTae.areRunsObservable());
		
		
		List<RunConfig> runConfigs = new ArrayList<RunConfig>(1);
		for(int i=0; i < 1; i++)
		{
			ParamConfiguration config = configSpace.getRandomConfiguration(rand);
			config.put("runtime", "100");
			if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED") || config.get("solved").equals("TIMEOUT"))
			{
				//Only want good configurations
				i--;
				continue;
			} else
			{
				RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 3000, config);
				runConfigs.add(rc);
			}
		}
		
		System.out.println("Performing " + runConfigs.size() + " runs");
		
		//StringWriter sw = new StringWriter();
		//ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		//PrintStream out = System.out;
		//System.setOut(new PrintStream(bout));
		
		TargetAlgorithmEvaluatorRunObserver obs = new TargetAlgorithmEvaluatorRunObserver()
		{
			
			@Override
			public void currentStatus(List<? extends KillableAlgorithmRun> runs) {
				
				double runtimeSum = 0.0; 
				for(AlgorithmRun run : runs)
				{
					runtimeSum += run.getRuntime();
				}
				
				//System.out.println(runtimeSum);
				if(runtimeSum > 1)
				{
					System.out.println("Attempting kill");
					for(KillableAlgorithmRun run : runs)
					{
						run.kill();
					}
				}
			}
			
		};
		
		long startTime  = System.currentTimeMillis();
		List<AlgorithmRun> runs = mysqlDBTae.evaluateRun(runConfigs,obs);
		long endTime = System.currentTimeMillis();
		//System.setOut(out);
		//System.out.println(bout.toString());
		
		for(AlgorithmRun run : runs)
		{
			System.out.println(run.getResultLine());
			
			ParamConfiguration config  = run.getRunConfig().getParamConfiguration();
			
			if(run.getRunResult().isSuccessfulAndCensored())
			{
				
				continue;
			}
			assertDEquals(config.get("runtime"), run.getRuntime(), 0.1);
			assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
			assertDEquals(config.get("quality"), run.getQuality(), 0.1);
			assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
			assertEquals(config.get("solved"), run.getRunResult().name());
			//This executor should not have any additional run data
			assertEquals("",run.getAdditionalRunData());
			

		}
		
		assertTrue("Should have taken less than 15 seconds to run, it took " + (endTime - startTime)/1000.0 + " seconds", (endTime - startTime) < (long) 15000);
	}
	
	
	/**
	 * Tests whether warnings are generated for Algorithms exceeding there runtime
	 */
	@Test
	public void testDynamicAdaptiveCappingSingleRunBounded()
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
		
		TargetAlgorithmEvaluator mysqlDBTae = new BoundedTargetAlgorithmEvaluator(new MySQLTargetAlgorithmEvaluator(execConfig, mysqlPersistence), 1, execConfig);	
		
		assertTrue(mysqlDBTae.areRunsObservable());
		
		
		List<RunConfig> runConfigs = new ArrayList<RunConfig>(1);
		for(int i=0; i < 1; i++)
		{
			ParamConfiguration config = configSpace.getRandomConfiguration(rand);
			config.put("runtime", "100");
			if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED") || config.get("solved").equals("TIMEOUT"))
			{
				//Only want good configurations
				i--;
				continue;
			} else
			{
				RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 3000, config);
				runConfigs.add(rc);
			}
		}
		
		System.out.println("Performing " + runConfigs.size() + " runs");
		
		//StringWriter sw = new StringWriter();
		//ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		//PrintStream out = System.out;
		//System.setOut(new PrintStream(bout));
		
		TargetAlgorithmEvaluatorRunObserver obs = new TargetAlgorithmEvaluatorRunObserver()
		{
			
			@Override
			public void currentStatus(List<? extends KillableAlgorithmRun> runs) {
				
				double runtimeSum = 0.0; 
				for(AlgorithmRun run : runs)
				{
					runtimeSum += run.getRuntime();
				}
				
				//System.out.println(runtimeSum);
				if(runtimeSum > 1)
				{
					System.out.println("Attempting kill");
					for(KillableAlgorithmRun run : runs)
					{
						run.kill();
					}
				}
			}
			
		};
		
		long startTime  = System.currentTimeMillis();
		List<AlgorithmRun> runs = mysqlDBTae.evaluateRun(runConfigs,obs);
		long endTime = System.currentTimeMillis();
		//System.setOut(out);
		//System.out.println(bout.toString());
		
		for(AlgorithmRun run : runs)
		{
			System.out.println(run.getResultLine());
			
			ParamConfiguration config  = run.getRunConfig().getParamConfiguration();
			
			if(run.getRunResult().isSuccessfulAndCensored())
			{
				assertTrue("Run Result Killed",run.getRunResult().equals(RunResult.KILLED));
				continue;
			}
			assertDEquals(config.get("runtime"), run.getRuntime(), 0.1);
			assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
			assertDEquals(config.get("quality"), run.getQuality(), 0.1);
			assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
			assertEquals(config.get("solved"), run.getRunResult().name());
			//This executor should not have any additional run data
			assertEquals("",run.getAdditionalRunData());
			

		}
		
		assertTrue("Should have taken less than 15 seconds to run, it took " + (endTime - startTime)/1000.0 + " seconds", (endTime - startTime) < (long) 15000);
	}
	
	/**
	 * Tests whether warnings are generated for Algorithms exceeding there runtime
	 */
	@Test
	public void testDynamicAdaptiveCappingMultiRunBoundedtoOne()
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
		
		TargetAlgorithmEvaluator mysqlDBTae = new BoundedTargetAlgorithmEvaluator(new MySQLTargetAlgorithmEvaluator(execConfig, mysqlPersistence), 1, execConfig);	
		
		assertTrue(mysqlDBTae.areRunsObservable());
		
		
		List<RunConfig> runConfigs = new ArrayList<RunConfig>(1);
		for(int i=0; i < 4; i++)
		{
			ParamConfiguration config = configSpace.getRandomConfiguration(rand);
			config.put("runtime", "100");
			if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED") || config.get("solved").equals("TIMEOUT"))
			{
				//Only want good configurations
				i--;
				continue;
			} else
			{
				RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 3000, config);
				runConfigs.add(rc);
			}
		}
		
		System.out.println("Performing " + runConfigs.size() + " runs");
		
		//StringWriter sw = new StringWriter();
		//ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		//PrintStream out = System.out;
		//System.setOut(new PrintStream(bout));
		
		TargetAlgorithmEvaluatorRunObserver obs = new TargetAlgorithmEvaluatorRunObserver()
		{
			
			@Override
			public void currentStatus(List<? extends KillableAlgorithmRun> runs) {
				
				double runtimeSum = 0.0; 
				for(AlgorithmRun run : runs)
				{
					runtimeSum += run.getRuntime();
				}
				
				//System.out.println(runtimeSum);
				if(runtimeSum > 1)
				{
					System.out.println("Attempting kill");
					for(KillableAlgorithmRun run : runs)
					{
						run.kill();
					}
				}
			}
			
		};
		
		long startTime  = System.currentTimeMillis();
		List<AlgorithmRun> runs = mysqlDBTae.evaluateRun(runConfigs,obs);
		long endTime = System.currentTimeMillis();
		//System.setOut(out);
		//System.out.println(bout.toString());
		
		for(AlgorithmRun run : runs)
		{
			System.out.println(run.getResultLine());
			
			ParamConfiguration config  = run.getRunConfig().getParamConfiguration();
			
			if(run.getRunResult().isSuccessfulAndCensored())
			{
				continue;
			}
			assertDEquals(config.get("runtime"), run.getRuntime(), 0.1);
			assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
			assertDEquals(config.get("quality"), run.getQuality(), 0.1);
			assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
			assertEquals(config.get("solved"), run.getRunResult().name());
			//This executor should not have any additional run data
			assertEquals("",run.getAdditionalRunData());
			

		}
		
		assertTrue("Should have taken less than 30 seconds to run, it took " + (endTime - startTime)/1000.0 + " seconds", (endTime - startTime) < (long) 30000);
		mysqlDBTae.notifyShutdown();
	}
	
	/**
	 * Tests whether warnings are generated for Algorithms exceeding there runtime
	 */
	@Test
	public void testDynamicAdaptiveCappingMultiRunBoundedtoTwo()
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
		
		TargetAlgorithmEvaluator mysqlDBTae = new BoundedTargetAlgorithmEvaluator(new MySQLTargetAlgorithmEvaluator(execConfig, mysqlPersistence), 2, execConfig);	
		
		assertTrue(mysqlDBTae.areRunsObservable());
		
		
		List<RunConfig> runConfigs = new ArrayList<RunConfig>(1);
		for(int i=0; i < 4; i++)
		{
			ParamConfiguration config = configSpace.getRandomConfiguration(rand);
			config.put("runtime", "100");
			if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED") || config.get("solved").equals("TIMEOUT"))
			{
				//Only want good configurations
				i--;
				continue;
			} else
			{
				RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 3000, config);
				runConfigs.add(rc);
			}
		}
		
		System.out.println("Performing " + runConfigs.size() + " runs");
		
		//StringWriter sw = new StringWriter();
		//ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		//PrintStream out = System.out;
		//System.setOut(new PrintStream(bout));
		
		TargetAlgorithmEvaluatorRunObserver obs = new TargetAlgorithmEvaluatorRunObserver()
		{
			
			@Override
			public void currentStatus(List<? extends KillableAlgorithmRun> runs) {
				
				double runtimeSum = 0.0; 
				for(AlgorithmRun run : runs)
				{
					runtimeSum += run.getRuntime();
				}
				
				//System.out.println(runtimeSum);
				if(runtimeSum > 1)
				{
					System.out.println("Attempting kill");
					for(KillableAlgorithmRun run : runs)
					{
						run.kill();
					}
				}
			}
			
		};
		
		long startTime  = System.currentTimeMillis();
		List<AlgorithmRun> runs = mysqlDBTae.evaluateRun(runConfigs,obs);
		long endTime = System.currentTimeMillis();
		//System.setOut(out);
		//System.out.println(bout.toString());
		
		for(AlgorithmRun run : runs)
		{
			System.out.println(run.getResultLine());
			
			ParamConfiguration config  = run.getRunConfig().getParamConfiguration();
			
			if(run.getRunResult().isSuccessfulAndCensored())
			{
				continue;
			}
			assertDEquals(config.get("runtime"), run.getRuntime(), 0.1);
			assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
			assertDEquals(config.get("quality"), run.getQuality(), 0.1);
			assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
			assertEquals(config.get("solved"), run.getRunResult().name());
			//This executor should not have any additional run data
			assertEquals("",run.getAdditionalRunData());
			

		}
		
		assertTrue("Should have taken less than 30 seconds to run, it took " + (endTime - startTime)/1000.0 + " seconds", (endTime - startTime) < (long) 30000);
		mysqlDBTae.notifyShutdown();
	}
	
	
	

	/**
	 * Tests whether warnings are generated for Algorithms exceeding there runtime
	 */
	@Test
	@Ignore
	public void testDynamicAdaptiveCappingMultiRunSingleCore()
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
		
		assertTrue(mysqlDBTae.areRunsObservable());
		
		
		
		List<RunConfig> runConfigs = new ArrayList<RunConfig>(10);
		for(int i=0; i < 10; i++)
		{
			ParamConfiguration config = configSpace.getRandomConfiguration(rand);
			config.put("runtime", ""+(i+1));
			if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED") || config.get("solved").equals("TIMEOUT"))
			{
				//Only want good configurations
				i--;
				continue;
			} else
			{
				RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 3000, config);
				runConfigs.add(rc);
			}
		}
		
		System.out.println("Performing " + runConfigs.size() + " runs");
		
		//StringWriter sw = new StringWriter();
		//ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		//PrintStream out = System.out;
		//System.setOut(new PrintStream(bout));
		
		TargetAlgorithmEvaluatorRunObserver obs = new TargetAlgorithmEvaluatorRunObserver()
		{
			
			@Override
			public void currentStatus(List<? extends KillableAlgorithmRun> runs) {
				
				double runtimeSum = 0.0; 
				for(AlgorithmRun run : runs)
				{
					runtimeSum += run.getRuntime();
				}
				
				//System.out.println(runtimeSum);
				if(runtimeSum > 5)
				{
					System.out.println("Issuing kill order on " + runtimeSum);
					for(KillableAlgorithmRun run : runs)
					{
						run.kill();
					}
				}
			}
			
		};
		
		long startTime  = System.currentTimeMillis();
		List<AlgorithmRun> runs = mysqlDBTae.evaluateRun(runConfigs,obs);
		long endTime = System.currentTimeMillis();
		//System.setOut(out);
		//System.out.println(bout.toString());
		
		for(AlgorithmRun run : runs)
		{
			System.out.println(run.getResultLine());
			
			ParamConfiguration config  = run.getRunConfig().getParamConfiguration();
			
			if(run.getRunResult().isSuccessfulAndCensored())
			{
				continue;
			}
			assertDEquals(config.get("runtime"), run.getRuntime(), 0.1);
			assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
			assertDEquals(config.get("quality"), run.getQuality(), 0.1);
			assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
			assertEquals(config.get("solved"), run.getRunResult().name());
			//This executor should not have any additional run data
			assertEquals("",run.getAdditionalRunData());
			

		}
		
		
		assertTrue("Should have taken less than five seconds to run, it took " + (endTime - startTime)/1000.0 + " seconds", (endTime - startTime) < (long) 6000);
		mysqlDBTae.notifyShutdown();
		
	}
	

	/**
	 * Tests whether warnings are generated for Algorithms exceeding there runtime
	 */
	@Test
	@Ignore
	public void testDynamicAdaptiveCappingMultiRunMultiCore()
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
		
		assertTrue(mysqlDBTae.areRunsObservable());
		
		
		
		List<RunConfig> runConfigs = new ArrayList<RunConfig>(10);
		for(int i=0; i < 10; i++)
		{
			ParamConfiguration config = configSpace.getRandomConfiguration(rand);
			config.put("runtime", ""+(i+1));
			if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED") || config.get("solved").equals("TIMEOUT"))
			{
				//Only want good configurations
				i--;
				continue;
			} else
			{
				RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 3000, config);
				runConfigs.add(rc);
			}
		}
		
		System.out.println("Performing " + runConfigs.size() + " runs");
		
		//StringWriter sw = new StringWriter();
		//ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		//PrintStream out = System.out;
		//System.setOut(new PrintStream(bout));
		
		TargetAlgorithmEvaluatorRunObserver obs = new TargetAlgorithmEvaluatorRunObserver()
		{
			
			@Override
			public void currentStatus(List<? extends KillableAlgorithmRun> runs) {
				
				double runtimeSum = 0.0; 
				for(AlgorithmRun run : runs)
				{
					runtimeSum += run.getRuntime();
				}
				
				//System.out.println(runtimeSum);
				if(runtimeSum > 5)
				{
					for(KillableAlgorithmRun run : runs)
					{
						run.kill();
					}
				}
			}
			
		};
		
		long startTime  = System.currentTimeMillis();
		List<AlgorithmRun> runs = mysqlDBTae.evaluateRun(runConfigs,obs);
		long endTime = System.currentTimeMillis();
		//System.setOut(out);
		//System.out.println(bout.toString());
		
		for(AlgorithmRun run : runs)
		{
			System.out.println(run.getResultLine());
			
			ParamConfiguration config  = run.getRunConfig().getParamConfiguration();
			
			if(run.getRunResult().isSuccessfulAndCensored())
			{
				continue;
			}
			assertDEquals(config.get("runtime"), run.getRuntime(), 0.1);
			assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
			assertDEquals(config.get("quality"), run.getQuality(), 0.1);
			assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
			assertEquals(config.get("solved"), run.getRunResult().name());
			//This executor should not have any additional run data
			assertEquals("",run.getAdditionalRunData());
			

		}
		
		
		
		assertTrue("Should have taken less than five seconds to run, it took " + (endTime - startTime)/1000.0 + " seconds", (endTime - startTime) < (long) 6000);
		mysqlDBTae.notifyShutdown();
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
