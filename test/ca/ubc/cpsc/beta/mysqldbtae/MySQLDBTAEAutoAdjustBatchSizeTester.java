package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.BeforeClass;
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
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.base.cli.CommandLineAlgorithmRun;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistenceUtil;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorOptions;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ca.ubc.cs.beta.targetalgorithmevaluator.ParamEchoExecutor;
import ca.ubc.cs.beta.targetalgorithmevaluator.TrueSleepyParamEchoExecutor;
import ec.util.MersenneTwister;

@SuppressWarnings("unused")
public class MySQLDBTAEAutoAdjustBatchSizeTester {

	private static AlgorithmExecutionConfiguration execConfig;

	private static  ParameterConfigurationSpace configSpace;
	
	private static List<AlgorithmRunConfiguration> runConfigs;
	
	private static MySQLOptions mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_AutoAdjustTest";
	

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
	
		StringBuilder b = new StringBuilder();
		b.append("java -cp ");
		b.append(System.getProperty("java.class.path"));
		b.append(" ");
		b.append(TrueSleepyParamEchoExecutor.class.getCanonicalName());
		execConfig = new AlgorithmExecutionConfiguration(b.toString(), System.getProperty("user.dir"), configSpace, false, false, 500);
		
		rand = new MersenneTwister();
		
		runConfigs= new ArrayList<AlgorithmRunConfiguration>(1);
		for(int i=0; i < 30; i++)
		{
			ParameterConfiguration config = configSpace.getRandomParameterConfiguration(rand);
			config.put("runtime", "1");
			if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED") || config.get("solved").equals("TIMEOUT"))
			{
				//Only want good configurations
				i--;
				continue;
			} else
			{
				AlgorithmRunConfiguration rc = new AlgorithmRunConfiguration(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 20, config,execConfig);
				runConfigs.add(rc);
			}
		}

		
	}
	
	
	public Process setupWorker()
	{
		Process proc;
		try {
			StringBuilder b = new StringBuilder();
			
			b.append("java -cp ");
			b.append(System.getProperty("java.class.path"));
			b.append(" ");
			b.append(MySQLTAEWorker.class.getCanonicalName());
			b.append(" --pool ").append(MYSQL_POOL);
			
			b.append(" --timeLimit 300");
			b.append(" --runsToBatch 1 --console-log-level DEBUG --log-level DEBUG --delayBetweenRequests 1 --shutdownBuffer 0 --idleLimit 10 --tae RANDOM --auto-adjust-batch-size true --concurrency-factor 1");
			b.append(" --mysql-hostname ").append(mysqlConfig.host).append(" --mysql-password ").append(mysqlConfig.password).append(" --mysql-database ").append(mysqlConfig.databaseName).append(" --mysql-username ").append(mysqlConfig.username).append(" --mysql-port ").append(mysqlConfig.port);
			
			System.out.println(b.toString());
			proc = Runtime.getRuntime().exec(b.toString());
			
			InputReader.createReadersForProcess(proc);
			
			return proc;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Test starts a worker, then kills it harshly
	 * before starting another one.
	 * 
	 * It is expected that the TAE should figure out that it needs reschedule the runs.
	 *  
	 *  
	 *  
	 */
	@Test
	public void testWorkerAutoAdjust()
	{
		executeTest();
	}
	
	
	public void executeTest()
	{
		try {		
			MySQLTargetAlgorithmEvaluator mySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, BATCH_INSERT_SIZE, true, MYSQL_PERMANENT_RUN_PARTITION+1, false, priority, 20)	;		
			
			MySQLPersistenceUtil.executeQueryForDebugPurposes("TRUNCATE TABLE " + MySQLPersistenceUtil.getRunConfigTable(mySQLTAE),mySQLTAE);
			Process proc1 = setupWorker();
			
			try {
			/*Process proc2 = setupWorker();
			Process proc3 = setupWorker();
			Process proc4 = setupWorker();
			Process proc5 = setupWorker();
			*/
			long startTime = System.currentTimeMillis();
			
			
			
			final CountDownLatch t = new CountDownLatch(1);
			final AtomicBoolean done = new AtomicBoolean(false); 
			final AtomicReference<RuntimeException> rt = new AtomicReference<>();
			mySQLTAE.evaluateRun(runConfigs, null);
			long endTime = System.currentTimeMillis();
			assertTrue("Expected amount of time test took to run is less than 10 seconds",(endTime-startTime)<10000);
			} finally
			{
				proc1.destroy();
			}
		} catch(RuntimeException e)
		{
			e.printStackTrace();
			throw e;
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
