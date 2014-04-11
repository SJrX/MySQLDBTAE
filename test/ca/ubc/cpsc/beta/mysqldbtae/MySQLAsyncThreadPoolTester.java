package ca.ubc.cpsc.beta.mysqldbtae;



import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ca.ubc.cs.beta.TestHelper;
import ca.ubc.cs.beta.aeatk.algorithmexecutionconfiguration.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aeatk.algorithmrun.RunResult;
import ca.ubc.cs.beta.aeatk.algorithmrunconfiguration.AlgorithmRunConfiguration;
import ca.ubc.cs.beta.aeatk.configspace.ParamConfiguration;
import ca.ubc.cs.beta.aeatk.configspace.ParamConfigurationSpace;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.WaitableTAECallback;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.decorators.resource.BoundedTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor;
import ec.util.MersenneTwister;

@SuppressWarnings("unused")
public class MySQLAsyncThreadPoolTester {
	
		
	private static Process proc;
	
	private static AlgorithmExecutionConfiguration execConfig;

	private static  ParamConfigurationSpace configSpace;
	
	private static MySQLOptions mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_async_memout";
	

	private static final int TARGET_RUNS_IN_LOOPS = 50;
	
	private static final int BATCH_INSERT_SIZE = TARGET_RUNS_IN_LOOPS/10;
	
	private static final int MYSQL_RUN_PARTITION = 0;
	
	private  static Random rand;
	
	private final boolean DELETE_ON_SHUTDOWN = false;
	
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
			b.append(" --tae CLI --runsToBatch 5 --delayBetweenRequests 1 --idleLimit 15s" );
			b.append(" --mysql-hostname ").append(mysqlConfig.host).append(" --mysql-password ").append(mysqlConfig.password).append(" --mysql-database ").append(mysqlConfig.databaseName).append(" --mysql-username ").append(mysqlConfig.username).append(" --mysql-port ").append(mysqlConfig.port);
			
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
		execConfig = new AlgorithmExecutionConfiguration(b.toString(), System.getProperty("user.dir"), configSpace, false, false, 0.01);
		
		
		rand = new MersenneTwister();
		

		
	}
	
	
	

	/**
	 * Tests whether warnings are generated for Algorithms exceeding there runtime
	 */
	@Test
	@Ignore
	public void testOutOfMemoryOnSubmissionOfALotOfAsyncRuns() throws Throwable
	{
		fail("DO NOT DELETE THIS TEST AND DO NOT RUN IT");
		fail("IT MOSTLY HAMMERS THE DATABASE AS FAST AS POSSIBLE");
		System.out.println("Max Memory: " + Runtime.getRuntime().maxMemory()/1024/1024 + " MB");
		System.out.println("Free Memory: " + Runtime.getRuntime().freeMemory()/1024/1024 + " MB");
		System.out.println("Total Memory: " + Runtime.getRuntime().totalMemory()/1024/1024 + " MB");
		
		TargetAlgorithmEvaluator mysqlDBTae =MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, 25, true, MYSQL_RUN_PARTITION, DELETE_ON_SHUTDOWN, priority);
		
		assertTrue(mysqlDBTae.areRunsObservable());
		
		long startTime  = System.currentTimeMillis();
		final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
		
		TargetAlgorithmEvaluatorCallback taeCallback = new TargetAlgorithmEvaluatorCallback()
		{

			@Override
			public void onSuccess(List<AlgorithmRun> runs) {
				
				try{
					for(AlgorithmRun run : runs)
					{
						
						System.out.println(run.getResultLine());
						
						ParamConfiguration config  = run.getRunConfig().getParameterConfiguration();
						
						assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
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
				} catch(Throwable e)
				{
					System.err.println("Exception occured, setting");
					exception.set(e);
				}
			}

			@Override
			public void onFailure(RuntimeException e) {
				exception.set(e);
				
			}
			
		};
		
		
		for(int i=0; i < 300000; i++)
		{
			List<AlgorithmRunConfiguration> runConfigs = new ArrayList<AlgorithmRunConfiguration>(1);
			ParamConfiguration config = configSpace.getRandomConfiguration(rand);
			config.put("runtime", "300");
			if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED") || config.get("solved").equals("TIMEOUT"))
			{
				//Only want good configurations
				i--;
				continue;
			} else
			{
				AlgorithmRunConfiguration rc = new AlgorithmRunConfiguration(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 3000, config, execConfig);
				runConfigs.add(rc);
			}
			
			System.out.println("Performing " + runConfigs.size() + " runs");
			
			
			
			mysqlDBTae.evaluateRunsAsync(runConfigs, taeCallback);
			
			
			if(i % 1000 == 0)
			{
				System.out.println("Max Memory: " + Runtime.getRuntime().maxMemory()/1024/1024 + " MB");
				System.out.println("Free Memory: " + Runtime.getRuntime().freeMemory()/1024/1024 + " MB");
				System.out.println("Total Memory: " + Runtime.getRuntime().totalMemory()/1024/1024 + " MB");
			
			}
		}
		
		
		
		//StringWriter sw = new StringWriter();
		//ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		//PrintStream out = System.out;
		//System.setOut(new PrintStream(bout));
		
		final TargetAlgorithmEvaluatorRunObserver obs = new TargetAlgorithmEvaluatorRunObserver()
		{
			
			@Override
			public void currentStatus(List<? extends AlgorithmRun> runs) {
				
				double runtimeSum = 0.0; 
				for(AlgorithmRun run : runs)
				{
					runtimeSum += run.getRuntime();
				}
				
				//System.out.println(runtimeSum);
				if(runtimeSum > 1)
				{
					System.out.println("Attempting kill");
					for(AlgorithmRun run : runs)
					{
						run.kill();
					}
				}
			}
			
		};
		
		
		
		long endTime = System.currentTimeMillis();
		if(exception.get() != null)
		{
			System.err.println("ERROR HAPPENED");
			throw exception.get();
		}
		assertTrue("Should have taken less than 30 seconds to run, it took " + (endTime - startTime)/1000.0 + " seconds", (endTime - startTime) < (long) 30000);
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
