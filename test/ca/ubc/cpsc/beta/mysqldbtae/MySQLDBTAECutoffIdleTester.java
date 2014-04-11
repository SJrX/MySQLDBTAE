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
public class MySQLDBTAECutoffIdleTester {


	
	private static AlgorithmExecutionConfiguration execConfig;

	private static  ParamConfigurationSpace configSpace;
	
	private static List<RunConfig> runConfigs;
	
	private static MySQLOptions mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_CutoffIdleTest";
	
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
		
		runConfigs= new ArrayList<RunConfig>(1);
		for(int i=0; i < 5; i++)
		{
			ParamConfiguration config = configSpace.getRandomConfiguration(rand);
			config.put("runtime", "5");
			if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED") || config.get("solved").equals("TIMEOUT"))
			{
				//Only want good configurations
				i--;
				continue;
			} else
			{
				RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 600, config,execConfig);
				runConfigs.add(rc);
			}
		}

		
	}
	
	public Process setupWorker(boolean checkMinCutoff, int minCutoffDeathTime)
	{
		return setupWorker(checkMinCutoff, minCutoffDeathTime, MYSQL_POOL);
	}
	
	public Process setupWorker(boolean checkMinCutoff, int minCutoffDeathTime, String pool)
	{
		Process proc;
		try {
			StringBuilder b = new StringBuilder();
			
			b.append("java -cp ");
			b.append(System.getProperty("java.class.path"));
			b.append(" ");
			b.append(MySQLTAEWorker.class.getCanonicalName());
			b.append(" --pool ").append(pool);
			
			b.append(" --timeLimit 20s");
			b.append(" --jobID CLI");
			b.append(" --minCutoffDeathTime ").append(minCutoffDeathTime);
			b.append(" --checkMinCutoff ").append(checkMinCutoff);
			b.append(" --tae CLI --runsToBatch 5 --delayBetweenRequests 1 --updateFrequency 1 --shutdownBuffer 0 --idleLimit 20s");
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
	 * Test creates a worker that lives for 20 seconds.
	 * It then then creates 5 jobs with a 600 seconds 
	 * cutoff time.  Since no jobs fall within the 
	 * workers lifetime, it will die within 3 seconds
	 * as specified by a parameter.  Test verifies this.
	 */
	@Test
	public void testMinCutoffIdle()
	{
		try {
			MySQLTargetAlgorithmEvaluator mySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL+"_isolated", BATCH_INSERT_SIZE, true, MYSQL_PERMANENT_RUN_PARTITION+1, false, priority);
			
			Process proc1 = setupWorker(true, 3,MYSQL_POOL+"_isolated");
			
			mySQLTAE.evaluateRunsAsync(runConfigs, null, null);
			
			Thread.sleep(11000);
			
			assertTrue(!isRunning(proc1));
		
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
