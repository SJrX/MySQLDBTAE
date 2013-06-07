package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration;
import ca.ubc.cs.beta.aclib.configspace.ParamConfigurationSpace;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.misc.options.MySQLConfig;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ec.util.MersenneTwister;

@SuppressWarnings("unused")
public class MySQLDBTAEPriorityOrderTester {

	
	private static Process proc;
	
	private static AlgorithmExecutionConfig execConfig;

	private static  ParamConfigurationSpace configSpace;
	
	private static MySQLConfig mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_priority_order";
	

	private static final int TARGET_RUNS_IN_LOOPS = 5;
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
		execConfig = new AlgorithmExecutionConfig("ignore", System.getProperty("user.dir"), configSpace, false, false, 500);
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
			b.append(" --mysqlDatabase ").append(mysqlConfig.databaseName);
			b.append(" --timeLimit 1d");
			b.append(" --tae PARAMECHO --runsToBatch 1 --delayBetweenRequests 3 --idleLimit 30s" );
			proc = Runtime.getRuntime().exec(b.toString());
			
			InputReader.createReadersForProcess(proc);
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testPriorityOrder()
	{

			MySQLPersistenceClient  normalMysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, 25, true,MYSQL_RUN_PARTITION,true, JobPriority.NORMAL);
			try {
			normalMysqlPersistence.setCommand(System.getProperty("sun.java.command"));
			} catch(RuntimeException e)
			{
				e.printStackTrace();
				throw e;
			}
			normalMysqlPersistence.setAlgorithmExecutionConfig(execConfig);
			
			MySQLTargetAlgorithmEvaluator normalMySQLTAE = new MySQLTargetAlgorithmEvaluator(execConfig, normalMysqlPersistence);
			
			MySQLPersistenceClient  highMysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, 25, true,MYSQL_RUN_PARTITION,true, JobPriority.HIGH);
			try {
			highMysqlPersistence.setCommand(System.getProperty("sun.java.command"));
			} catch(RuntimeException e)
			{
				e.printStackTrace();
				throw e;
			}
			highMysqlPersistence.setAlgorithmExecutionConfig(execConfig);
			
			MySQLTargetAlgorithmEvaluator highMySQLTAE = new MySQLTargetAlgorithmEvaluator(execConfig, highMysqlPersistence);
			
			
			
			final AtomicInteger completeRuns = new AtomicInteger();
			
			final AtomicBoolean failure = new AtomicBoolean(false);
			
			final AtomicReference<RunConfig> ref = new AtomicReference<RunConfig>();
			
			final Semaphore complete = new Semaphore(-TARGET_RUNS_IN_LOOPS+1);
			
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
					RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 1001, config);
					
					normalMySQLTAE.evaluateRunsAsync(Collections.singletonList(rc), new TargetAlgorithmEvaluatorCallback() {

						@Override
						public void onSuccess(List<AlgorithmRun> runs) {
							
							if(!runs.get(0).getRunConfig().equals(ref.get()))
							{
								completeRuns.incrementAndGet();
							}
							
							complete.release();
							
							System.err.println("DONE:" + runs.get(0).getRunConfig() + " high: " + runs.get(0).getRunConfig().equals(ref.get()));
						}

						@Override
						public void onFailure(RuntimeException t) {
							
						}
						
					});
					
					runConfigs.add(rc);
				}
			}
			
			
			
			RunConfig goodRun = runConfigs.get(rand.nextInt(runConfigs.size()));
			ref.set(goodRun);
			System.out.println("High Priority Run is: " + goodRun);
						
			highMySQLTAE.evaluateRunsAsync(Collections.singletonList(goodRun), new TargetAlgorithmEvaluatorCallback() {

				@Override
				public void onSuccess(List<AlgorithmRun> runs) {
					//complete.release();
					boolean swap = completeRuns.compareAndSet(0, 1);
					
					if(!swap)
					{
						failure.set(true);
					}
					
					System.err.println("HIGH DONE:" + runs.get(0).getRunConfig());
					
				}

				@Override
				public void onFailure(RuntimeException t) {
					
				}
				
			});

			
			System.err.println("Sleeping for 10 seconds");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.setupWorker();
			
			try {
				complete.acquire();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
			
			assertFalse("Expected high priority job to finish first " , failure.get());
			
			
			
			
			
			proc.destroy();
			highMySQLTAE.notifyShutdown();
			normalMySQLTAE.notifyShutdown();
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
