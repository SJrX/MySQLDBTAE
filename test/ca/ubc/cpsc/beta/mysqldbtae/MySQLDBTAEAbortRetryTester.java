package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.exceptions.TargetAlgorithmAbortException;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ca.ubc.cs.beta.targetalgorithmevaluator.ParamEchoExecutor;
import ca.ubc.cs.beta.targetalgorithmevaluator.ParamEchoExecutorNineTenthChanceAbort;
import ec.util.MersenneTwister;

@SuppressWarnings("unused")
public class MySQLDBTAEAbortRetryTester {


	
	private static Process proc;
	
	private static AlgorithmExecutionConfig execConfig;

	private static  ParamConfigurationSpace configSpace;
	
	private static MySQLOptions mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_aborttest";
	

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
		b.append(ParamEchoExecutorNineTenthChanceAbort.class.getCanonicalName());
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
			b.append(" --tae CLI --runsToBatch 1 --delayBetweenRequests 1 --idleLimit 60s " );
			b.append(" --mysql-hostname ").append(mysqlConfig.host).append(" --mysql-password ").append(mysqlConfig.password).append(" --mysql-database ").append(mysqlConfig.databaseName).append(" --mysql-username ").append(mysqlConfig.username).append(" --mysql-port ").append(mysqlConfig.port);
			
			//System.out.println(b.toString());
			proc = Runtime.getRuntime().exec(b.toString());
			
			InputReader.createReadersForProcess(proc);
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * This unit test either passes because a run result that was ABORT is now SAT 
	 * or never returns
	 */
	public void testAbortRetry()
	{
			setupWorker();
			
			MySQLTargetAlgorithmEvaluator highMySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, 1500, true, MYSQL_RUN_PARTITION, false, JobPriority.HIGH);		
		
			List<RunConfig> runConfigs = new ArrayList<RunConfig>(TARGET_RUNS_IN_LOOPS);
			
			
			do
			{
				ParamConfiguration config = configSpace.getRandomConfiguration(rand);
				if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED"))
				{
					//Only want good configurations
					continue;
				} else
				{
					config.put("runtime", "0.5");
					config.put("solved", "SAT");
					RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance","SLEEP"), Long.valueOf(config.get("seed"))), 1001, config,execConfig);
					
					runConfigs.add(rc);
					break;
				}
			} while(true);
			
			long time = System.currentTimeMillis();
			List<AlgorithmRun> runs = null;
			for(int i=0; i < 50; i++)
			{
				try {
					assertTrue(isRunning(proc));
					 runs = highMySQLTAE.evaluateRun(runConfigs);
	
					if(i == 0)
					{
						System.err.println("First Test Result was a pass so we need to try again, recursively");
						testAbortRetry();
						return;
					}
					break;
				} catch(TargetAlgorithmAbortException e)
				{
					System.err.println("Good got abort");
					
					
				}
			}

			assertEquals(RunResult.SAT, runs.get(0).getRunResult());
		
			highMySQLTAE.notifyShutdown();
			
			
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


