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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.ubc.cs.beta.TestHelper;
import ca.ubc.cs.beta.aeatk.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aeatk.algorithmrun.RunResult;
import ca.ubc.cs.beta.aeatk.configspace.ParamConfiguration;
import ca.ubc.cs.beta.aeatk.configspace.ParamConfigurationSpace;
import ca.ubc.cs.beta.aeatk.execconfig.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aeatk.runconfig.RunConfig;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
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
public class MySQLDBTAEPoolIdleTester {


	
	private static AlgorithmExecutionConfiguration execConfig;

	private static  ParamConfigurationSpace configSpace;
		
	private static MySQLOptions mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_poolIdleTimeTest";
	

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
		configSpace = new ParamConfigurationSpace(paramFile);
	
		StringBuilder b = new StringBuilder();
		b.append("java -cp ");
		b.append(System.getProperty("java.class.path"));
		b.append(" ");
		b.append(TrueSleepyParamEchoExecutor.class.getCanonicalName());
		execConfig = new AlgorithmExecutionConfiguration(b.toString(), System.getProperty("user.dir"), configSpace, false, false, 500);
		
		rand = new MersenneTwister();
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
			
			b.append(" --timeLimit 120");
			b.append(" --tae CLI --runsToBatch 5 --delayBetweenRequests 1 --updateFrequency 1 --poolIdleTimeLimit 20  --shutdownBuffer 0 --idleLimit 120" );
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
	 * Test creates 5 workers with a poolIdleTimeLimit of 20.
	 * After 12 seconds have passed, it ensures they have all
	 * died because the pool idle usage will exceed the limit.
	 */
	@Test
	public void testPoolIdleTime()
	{
		try {
			
			MySQLTargetAlgorithmEvaluator tae = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, BATCH_INSERT_SIZE, true, MYSQL_PERMANENT_RUN_PARTITION+1, false, priority);
					
			MySQLPersistenceUtil.executeQueryForDebugPurposes("TRUNCATE " + mysqlConfig.databaseName+"." + MYSQL_POOL+"_workers", tae);
			
			Process proc1 = setupWorker();
			Process proc2 = setupWorker();
			Process proc3 = setupWorker();
			Process proc4 = setupWorker();
			Process proc5 = setupWorker();
		
			Thread.sleep(2000);
			assertTrue(isRunning(proc1));
			assertTrue(isRunning(proc2));
			assertTrue(isRunning(proc3));
			assertTrue(isRunning(proc4));
			assertTrue(isRunning(proc5));
			
			Thread.sleep(10000);
			assertTrue(!isRunning(proc1));
			assertTrue(!isRunning(proc2));
			assertTrue(!isRunning(proc3));
			assertTrue(!isRunning(proc4));
			assertTrue(!isRunning(proc5));
		} catch(RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			
	}

	/**
	 * Test creates 2 workers with a poolIdleTimeLimit of 20.
	 * After 4 seconds have passed, it updates the
	 * poolIdleTimeLimit to 8 seconds and in 5 seconds checks
	 * that both have died.
	 */
	@Test
	public void testPoolIdleTimeUpdate()
	{
		try {
			
			MySQLTargetAlgorithmEvaluator tae = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, BATCH_INSERT_SIZE, true, MYSQL_PERMANENT_RUN_PARTITION+1, false, priority);
			
			MySQLPersistenceUtil.executeQueryForDebugPurposes("TRUNCATE " + mysqlConfig.databaseName+"." + MYSQL_POOL+"_workers", tae);
			
			Process proc1 = setupWorker();
			Process proc2 = setupWorker();
			
			Thread.sleep(4000);
			assertTrue(isRunning(proc1));
			assertTrue(isRunning(proc2));
			

			MySQLPersistenceUtil.executeQueryForDebugPurposes("UPDATE " + mysqlConfig.databaseName+"." + MYSQL_POOL+ "_workers SET poolIdleTimeLimit_UPDATEABLE=\"4\", upToDate=0", tae);


			
			Thread.sleep(5000);
			assertTrue(!isRunning(proc1));
			assertTrue(!isRunning(proc2));
			
		} catch(RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			
	}
	
	/**
	 * Test creates a worker and changes its poolIdleTimeLimit
	 * to 0 and sets its startWeekYear to 0.  Because it was
	 * not launched within the past 3 weeks, it is not counted
	 * towards pool idle time.  It is verified to be running.
	 * Another worker is added and both have their startWeekYear
	 * set to 2 weeks previous and are then shortly tested for 
	 * termination.
	 */
	@Test
	public void testPoolIdleTimeWeekYear()
	{
		try {
			
			MySQLTargetAlgorithmEvaluator tae = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, BATCH_INSERT_SIZE, true, MYSQL_PERMANENT_RUN_PARTITION+1, false, priority);
			
			MySQLPersistenceUtil.executeQueryForDebugPurposes("TRUNCATE " + mysqlConfig.databaseName+"." + MYSQL_POOL+"_workers", tae);
			
			Process proc1 = setupWorker();
			Thread.sleep(4000);

			assertTrue(isRunning(proc1));
			
			MySQLPersistenceUtil.executeQueryForDebugPurposes("UPDATE " + mysqlConfig.databaseName+"." + MYSQL_POOL+ "_workers SET poolIdleTimeLimit_UPDATEABLE=0, upToDate=0, startWeekYear=0", tae);
			
			Thread.sleep(2000);
			assertTrue(isRunning(proc1));
			
			Process proc2 = setupWorker();
			Thread.sleep(6000);
			
			assertTrue(!isRunning(proc1));
			assertTrue(isRunning(proc2));
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.WEEK_OF_YEAR, -2);
			int current = Integer.parseInt(cal.get(Calendar.WEEK_OF_YEAR)+""+cal.get(Calendar.YEAR));
			
			MySQLPersistenceUtil.executeQueryForDebugPurposes("UPDATE " + mysqlConfig.databaseName+"." + MYSQL_POOL+ "_workers SET poolIdleTimeLimit_UPDATEABLE=10, upToDate=0, startWeekYear="+current, tae);
			
			Thread.sleep(2000);
			
			assertTrue(!isRunning(proc1));
			assertTrue(!isRunning(proc2));
			
		} catch(RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			
	}
	
	public boolean isRunning(Process process) {
	    try {
	        process.exitValue();
	        return false;
	    } catch (IllegalThreadStateException e) {
	        return true;
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
