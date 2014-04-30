package ca.ubc.cpsc.beta.mysqldbtae;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import ca.ubc.cs.beta.aeatk.algorithmrunconfiguration.AlgorithmRunConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.AlgorithmRunResult;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.RunStatus;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfigurationSpace;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.WaitableTAECallback;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.decorators.helpers.WalltimeAsRuntimeTargetAlgorithmEvaluatorDecorator;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.decorators.resource.BoundedTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor;
import ec.util.MersenneTwister;

@SuppressWarnings("unused")
public class MySQLClientKillSingleRunTester {
	
		
	private static Process proc;
	
	private static AlgorithmExecutionConfiguration execConfig;

	private static  ParameterConfigurationSpace configSpace;
	
	private static MySQLOptions mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_kill_single_run";
	

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
		configSpace = new ParameterConfigurationSpace(paramFile);
		
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
	 * This is related to issue 1928, where operator precedence caused us to kill all new runs in the db.
	 */
	@Test
	public void testKillingOfNewAndAssignedWorksProperly() throws Throwable
	{
		

	
		TargetAlgorithmEvaluator mySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, 25, true, MYSQL_RUN_PARTITION, DELETE_ON_SHUTDOWN, priority);	
	

		

		TargetAlgorithmEvaluator mysqlDBTae = new BoundedTargetAlgorithmEvaluator(mySQLTAE, 25);	
		
		mysqlDBTae = new WalltimeAsRuntimeTargetAlgorithmEvaluatorDecorator(mysqlDBTae);
		assertTrue(mysqlDBTae.areRunsObservable());
		
		
		List<AlgorithmRunConfiguration> runConfigs = new ArrayList<AlgorithmRunConfiguration>(1);
		for(int i=0; i < 3; i++)
		{
			ParameterConfiguration config = configSpace.getRandomParameterConfiguration(rand);
			config.put("runtime", "8");
			config.put("solved", "SAT");
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
		}
		
		final TargetAlgorithmEvaluatorRunObserver obs = new TargetAlgorithmEvaluatorRunObserver()
		{
			
			@Override
			public void currentStatus(List<? extends AlgorithmRunResult> runs) {
				
				double runtimeSum = 0.0; 
				for(AlgorithmRunResult run : runs)
				{
					runtimeSum += run.getRuntime();
				}
				

				if(runtimeSum > 1)
				{
					runs.get(0).kill();
					runs.get(2).kill();
				}
			}
			
		};
			
		
		List<AlgorithmRunResult> runs = mysqlDBTae.evaluateRun(runConfigs,obs);
		
		assertEquals("First run should be killed", RunStatus.KILLED, runs.get(0).getRunStatus());
		assertEquals("First run should be SAT", RunStatus.SAT, runs.get(1).getRunStatus());
		assertEquals("First run should be SAT", RunStatus.KILLED, runs.get(2).getRunStatus());
		
		assertTrue("Runtime of first element should be less than 7, not: " + runs.get(0).getRuntime(), runs.get(0).getRuntime() < 7);
		assertTrue("Runtime of second element should be greater than 7, not: " + runs.get(1).getRuntime(), runs.get(1).getRuntime() > 7 );
		assertTrue("Runtime of second element should be less than 7, not: " + runs.get(2).getRuntime(), runs.get(2).getRuntime() < 7 );
		
		
		
		
		
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
