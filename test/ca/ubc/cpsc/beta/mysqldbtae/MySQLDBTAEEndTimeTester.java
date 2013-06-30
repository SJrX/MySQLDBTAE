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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.ubc.cs.beta.TestHelper;
import ca.ubc.cs.beta.aclib.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.RunResult;
import ca.ubc.cs.beta.aclib.algorithmrun.kill.KillableAlgorithmRun;
import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration;
import ca.ubc.cs.beta.aclib.configspace.ParamConfigurationSpace;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.options.MySQLOptions;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistenceUtil;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLDBTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorOptions;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ca.ubc.cs.beta.targetalgorithmevaluator.ParamEchoExecutor;
import ec.util.MersenneTwister;

@SuppressWarnings("unused")
public class MySQLDBTAEEndTimeTester {


	
	private static AlgorithmExecutionConfig execConfig;

	private static  ParamConfigurationSpace configSpace;
	
	private static MySQLOptions mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_endtimetest";
	

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
		b.append(ParamEchoExecutor.class.getCanonicalName());
		execConfig = new AlgorithmExecutionConfig(b.toString(), System.getProperty("user.dir"), configSpace, false, false, 500);
		
		
		
		rand = new MersenneTwister();

		
	}
	
	
	public Process setupWorker(String limit, String idle, String jobID)
	{
		Process proc;
		try {
			StringBuilder b = new StringBuilder();
			
			b.append("java -cp ");
			b.append(System.getProperty("java.class.path"));
			b.append(" ");
			b.append(MySQLTAEWorker.class.getCanonicalName());
			b.append(" --pool ").append(MYSQL_POOL);
			
			b.append(" --timeLimit " ).append(limit);
			b.append(" --jobID ").append(jobID);
			b.append(" --tae CLI --runsToBatch 1 --delayBetweenRequests 1 --updateFrequency 1 --idleLimit " ).append(idle);
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
	
	@Test
	public void testIdleTime()
	{
			
		try {
			Process proc1 = setupWorker("60s","6s","CLI");
			Process proc2 = setupWorker("60s","12s","CLI");
			System.out.println("Launching proc1, proc2");

			Thread.sleep(3000);
			
			assertTrue(isRunning(proc1));
			assertTrue(isRunning(proc2));
			System.out.println("Both still running");
			
			Thread.sleep(6000);
			
			assertTrue(!isRunning(proc1));
			System.out.println("proc1 terminates");

			Thread.sleep(6000);
			
			assertTrue(!isRunning(proc2));
			System.out.println("proc2 terminates");

		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
			
	}
	
	@Test
	public void testEndTime()
	{
			
		try {
			Process proc1 = setupWorker("6s","60s","CLI");
			Process proc2 = setupWorker("12s","60s","CLI");
			System.out.println("Launching proc1, proc2");

			Thread.sleep(3000);
			
			assertTrue(isRunning(proc1));
			assertTrue(isRunning(proc2));
			System.out.println("Both still running");
			
			Thread.sleep(6000);
			
			assertTrue(!isRunning(proc1));
			System.out.println("proc1 terminates");

			Thread.sleep(6000);
			
			assertTrue(!isRunning(proc2));
			System.out.println("proc2 terminates");

		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
			
	}	
	
	@Test
	public void testEndTimeUpdate()
	{
		MySQLPersistenceClient mysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, BATCH_INSERT_SIZE, true,MYSQL_PERMANENT_RUN_PARTITION+1,false, priority);
		
		try {
			long startTime = System.currentTimeMillis();
			Process proc1 = setupWorker("1d","1d","proc1");
			Process proc2 = setupWorker("1d","1d","proc2");
			System.out.println("Launching proc1, proc2");

			Thread.sleep(5000);
			assertTrue(isRunning(proc1));
			assertTrue(isRunning(proc2));
			System.out.println("Both still running");
			
			long endTime = System.currentTimeMillis() + 5000;
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(endTime);
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sqlTime = sdf.format(calendar.getTime());
			MySQLPersistenceUtil.executeQueryForDebugPurposes("UPDATE " + mysqlConfig.databaseName+"." + MYSQL_POOL+ "_workers SET endTime_UPDATEABLE=\"" + sqlTime +"\", upToDate=0 WHERE jobID LIKE \"proc1%\"", mysqlPersistence);
				
			for(int i=0;i<30;i++)
			{
				if(isRunning(proc1))
					Thread.sleep(1000);
				else
					break;
			}
			assertTrue(!isRunning(proc1));
			assertTrue(isRunning(proc2));
			System.out.println("proc1 terminates");

			endTime = System.currentTimeMillis() + 5000;
			calendar.setTimeInMillis(endTime);
			sqlTime = sdf.format(calendar.getTime());
			MySQLPersistenceUtil.executeQueryForDebugPurposes("UPDATE " + mysqlConfig.databaseName+"." + MYSQL_POOL+ "_workers SET endTime_UPDATEABLE=\"" + sqlTime +"\", upToDate=0 WHERE jobID LIKE \"proc2%\"", mysqlPersistence);
			
			for(int i=0;i<30;i++)
			{
				if(isRunning(proc2))
					Thread.sleep(1000);
				else
					break;
			}
			assertTrue(!isRunning(proc1));
			assertTrue(!isRunning(proc2));
			System.out.println("proc2 terminates");

		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
			
	}	
	
	public boolean isRunning(Process process) {
	    try {
	        process.exitValue();
	        return false;
	    } catch (Exception e) {
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
