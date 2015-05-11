package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistenceUtil;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorOptions;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ca.ubc.cs.beta.targetalgorithmevaluator.ParamEchoExecutor;
import ec.util.MersenneTwister;

@SuppressWarnings("unused")
public class MySQLDBTAEEndTimeTester {


	
	private static AlgorithmExecutionConfiguration execConfig;

	private static  ParameterConfigurationSpace configSpace;
	
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
		configSpace = new ParameterConfigurationSpace(paramFile);
	
		StringBuilder b = new StringBuilder();
		b.append("java -cp ");
		b.append(System.getProperty("java.class.path"));
		b.append(" ");
		b.append(ParamEchoExecutor.class.getCanonicalName());
		execConfig = new AlgorithmExecutionConfiguration(b.toString(), System.getProperty("user.dir"), configSpace, false, false, 500);
		
		
		
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
			b.append(" --tae CLI --runsToBatch 1 --delayBetweenRequests 1 --shutdownBuffer 0 --idleLimit " ).append(idle);
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
	 * Test verifies workers die from idle time
	 */
	@Test
	public void testIdleTime()
	{
			
		try {
			Process proc1 = setupWorker("120s","2s","CLI");
			System.out.println("Launching proc1");

			Thread.sleep(1000);
			
			assertTrue(isRunning(proc1));
			
			Thread.sleep(4000);
			
			assertTrue(!isRunning(proc1));

		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
			
	}
	
	/**
	 * Test verifies that workers die from
	 * their set endTime
	 */
	@Test
	public void testEndTime()
	{
			
		try {
			System.err.println("============== Start of end time test ==================");
			System.err.println("============== Start of end time test ==================");
			System.err.println("============== Start of end time test ==================");
			System.err.println("============== Start of end time test ==================");
			Process proc1 = setupWorker("2s","120s","CLI");

			Thread.sleep(1000);
			
			assertTrue(isRunning(proc1));
			
			Thread.sleep(4000);
			
			System.err.println("============== End of end time test ==================");

			System.err.println("============== End of end time test ==================");

			System.err.println("============== End of end time test ==================");

			System.err.println("============== End of end time test ==================");
			
			assertTrue(!isRunning(proc1));

		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
			
	}	
	
	private static int getPID(Process p)
	{
		int pid = 0;
		
		try {
			Field f = p.getClass().getDeclaredField("pid");
			
			f.setAccessible(true);
			pid = Integer.valueOf(f.get(p).toString());
			f.setAccessible(false);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			return -1;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		if(pid > 0)
		{
			return pid;
		} else
		{
			return -1;
		}
	}
	
	
	/**
	 * Test initially creates a worker with high
	 * end and idle times and verifies that it 
	 * is still alive.  Then the endTime is
	 * updated to be much shorter and the worker
	 * is verified to have terminated
	 */
	@Test
	public void testEndTimeUpdate()
	{
		//MySQLPersistenceClient mysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, BATCH_INSERT_SIZE, true,MYSQL_PERMANENT_RUN_PARTITION+1,false, priority);
		
		MySQLTargetAlgorithmEvaluator tae = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, BATCH_INSERT_SIZE, true, MYSQL_RUN_PARTITION, false, priority);
		
		MySQLPersistenceUtil.executeQueryForDebugPurposes("TRUNCATE TABLE " + MySQLPersistenceUtil.getRunConfigTable(tae),tae);
		try {
			long startTime = System.currentTimeMillis();
			Process proc1 = setupWorker("120s","120s","proc1");

			Thread.sleep(5000);
			assertTrue(isRunning(proc1));
			
			long endTime = System.currentTimeMillis() + 2000;
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(endTime);
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sqlTime = sdf.format(calendar.getTime());
			MySQLPersistenceUtil.executeQueryForDebugPurposes("UPDATE " + mysqlConfig.databaseName+"." + MYSQL_POOL+ "_workers SET endTime_UPDATEABLE=\"" + sqlTime +"\", upToDate=0 WHERE jobID LIKE \"proc1%\"", tae);
				
			System.out.println("UPDATE " + mysqlConfig.databaseName+"." + MYSQL_POOL+ "_workers SET endTime_UPDATEABLE=\"" + sqlTime +"\", upToDate=0 WHERE jobID LIKE \"proc1%\"");
			assertTrue(isRunning(proc1));

			Thread.sleep(4000);
			
			try {
				
			
			assertTrue(!isRunning(proc1));
			}finally
			{
				proc1.destroy();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
			
	}	
	
	public boolean isRunning(Process process) {
	    try {
	    	process.exitValue();
	        return false;
	    } catch (IllegalThreadStateException e) {
	    	/*
	    	if(new File("/proc").exists())
	    	{
	    		if(new File("/proc/" + getPID(process)).exists())
		    	{
	    			for(File f : (new File("/proc/" + getPID(process)).listFiles()))
	    			{
	    				System.out.println(f.getAbsolutePath());
	    			}
	    					
	    			return true; 
		    	} else 
		    	{
		    		return false;
		    	}
	    	} else*/
	    	{
	    		return true;
	    	}
	    	
	        
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
