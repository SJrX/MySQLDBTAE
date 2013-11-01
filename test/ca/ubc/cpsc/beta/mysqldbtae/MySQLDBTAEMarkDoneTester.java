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
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
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
public class MySQLDBTAEMarkDoneTester {


	
	private static AlgorithmExecutionConfig execConfig;

	private static  ParamConfigurationSpace configSpace;
	
	private static List<RunConfig> runConfigs;
	
	private static MySQLOptions mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_markCompleteTest";
	

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
		execConfig = new AlgorithmExecutionConfig(b.toString(), System.getProperty("user.dir"), configSpace, false, false, 500);
		
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
				RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 20, config,execConfig);
				runConfigs.add(rc);
			}
		}

		
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
			b.append(" --tae CLI --runsToBatch 5 --delayBetweenRequests 1 --updateFrequency 1 --shutdownBuffer 0 --idleLimit " ).append(idle);
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
	 * Test creates a worker and gives it 5 jobs, each
	 * 5 seconds long.  Its observer is set to terminate
	 * all other runs when the first completes.  It
	 * tests that the entire procedure takes less than
	 * 20 seconds which means the jobs must be marked as
	 * completed one by one as they finish rather than
	 * in batch. 
	 */
	@Test
	public void testMarkDone()
	{
		try {
	
			
			MySQLTargetAlgorithmEvaluator mySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, BATCH_INSERT_SIZE, true, MYSQL_PERMANENT_RUN_PARTITION+1, false, priority);			
			
			Process proc1 = setupWorker("180s","5s","proc1");
			
			long startTime = System.currentTimeMillis();
			
			
			List<AlgorithmRun> runs = mySQLTAE.evaluateRun(runConfigs,new TargetAlgorithmEvaluatorRunObserver() {

				@Override
				public void currentStatus(List<? extends KillableAlgorithmRun> runs) {
					if(runs.get(0).isRunCompleted())
					{
						for(KillableAlgorithmRun run: runs)
						{
							System.err.println("Killing Run");
							run.kill();
						}
					}	
				} });
			
			long endTime = System.currentTimeMillis();
			assertTrue(runs.get(0).isRunCompleted());
			assertTrue((endTime-startTime)<20000);
		
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
