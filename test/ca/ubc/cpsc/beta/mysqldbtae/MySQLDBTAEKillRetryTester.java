package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.BeforeClass;
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
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.currentstatus.CurrentRunStatusObserver;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.deferred.TAECallback;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ca.ubc.cs.beta.targetalgorithmevaluator.ParamEchoExecutor;
import ec.util.MersenneTwister;

public class MySQLDBTAEKillRetryTester {


	
	private static Process proc;
	
	private static AlgorithmExecutionConfig execConfig;

	private static  ParamConfigurationSpace configSpace;
	
	private static MySQLConfig mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_killtest";
	

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
		
		mysqlConfig = new MySQLConfig();
		mysqlConfig.host = "arrowdb.cs.ubc.ca";
		mysqlConfig.port = 4040;
		mysqlConfig.password = "october-127";
		mysqlConfig.databaseName = "mysql_db_tae";
		mysqlConfig.username = "mysql_db_tae";
		
		
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
	
	
	public void setupWorker()
	{
		try {
			StringBuilder b = new StringBuilder();
			
			b.append("java -cp ");
			b.append(System.getProperty("java.class.path"));
			b.append(" ");
			b.append(MySQLTAEWorker.class.getCanonicalName());
			b.append(" --pool ").append(MYSQL_POOL);
			b.append(" --timeLimit 1d");
			b.append(" --tae CLI --runsToBatch 1 --delayBetweenRequests 3 --idleLimit 10s " );
			
			
			System.out.println(b.toString());
			proc = Runtime.getRuntime().exec(b.toString());
			
			InputReader.createReadersForProcess(proc);
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testKillRetry()
	{
			setupWorker();
		
		
			MySQLPersistenceClient  highMysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, 1500, true,MYSQL_RUN_PARTITION,true, JobPriority.HIGH);
			try {
			highMysqlPersistence.setCommand(System.getProperty("sun.java.command"));
			} catch(RuntimeException e)
			{
				e.printStackTrace();
				throw e;
			}
			highMysqlPersistence.setAlgorithmExecutionConfig(execConfig);
			
			MySQLTargetAlgorithmEvaluator highMySQLTAE = new MySQLTargetAlgorithmEvaluator(execConfig, highMysqlPersistence);		
		
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
					config.put("runtime", "30");
					config.put("solved", "SAT");
					RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance","SLEEP"), Long.valueOf(config.get("seed"))), 1001, config);
					
					runConfigs.add(rc);
					break;
				}
				
				
			}
			
			long time = System.currentTimeMillis();
			
			List<AlgorithmRun> runs = highMySQLTAE.evaluateRun(runConfigs, new CurrentRunStatusObserver() {

				@Override
				public void currentStatus(
						List<? extends KillableAlgorithmRun> runs) {
						
					
					if(runs.get(0).getRuntime() > 10)
					{
						System.err.println("Killing Run");
						runs.get(0).kill();
					}
					
				} });
			 
			
			assertEquals(RunResult.TIMEOUT, runs.get(0).getRunResult());
			
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				Thread.currentThread();
			}
			
			
			runs = highMySQLTAE.evaluateRun(runConfigs);			
			
			assertEquals(RunResult.SAT, runs.get(0).getRunResult());
			
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				Thread.currentThread();
			}
			
			
			highMySQLTAE.notifyShutdown();
			
			
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


