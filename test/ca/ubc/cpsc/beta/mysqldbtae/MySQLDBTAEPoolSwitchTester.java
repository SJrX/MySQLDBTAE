package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.ubc.cs.beta.TestHelper;
import ca.ubc.cs.beta.aeatk.algorithmexecutionconfiguration.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunconfiguration.AlgorithmRunConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.AlgorithmRunResult;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfigurationSpace;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistenceUtil;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ec.util.MersenneTwister;

@SuppressWarnings("unused")
public class MySQLDBTAEPoolSwitchTester {

	
	private static Process proc;
	
	private static AlgorithmExecutionConfiguration execConfig;

	private static  ParameterConfigurationSpace configSpace;
	
	private static MySQLOptions mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_pool_switch";
	

	private static final int TARGET_RUNS_IN_LOOPS = 50;
	private static final int BATCH_INSERT_SIZE = TARGET_RUNS_IN_LOOPS/10;
	
	private static final int MYSQL_RUN_PARTITION = 0;
	
	//Negative partitions won't be deleted
	private static final int MYSQL_PERMANENT_RUN_PARTITION = -10;
	
	private static Random rand;
	
	private final JobPriority priority = JobPriority.HIGH;
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
			b.append(" --pool ").append(MYSQL_POOL + 1);
			b.append(" --mysqlDatabase ").append(mysqlConfig.databaseName);
			b.append(" --timeLimit 1d");
			b.append(" --tae PARAMECHO --runsToBatch 200 --delayBetweenRequests 1 --idleLimit 30s" );
			b.append(" --mysql-hostname ").append(mysqlConfig.host).append(" --mysql-password ").append(mysqlConfig.password).append(" --mysql-database ").append(mysqlConfig.databaseName).append(" --mysql-username ").append(mysqlConfig.username).append(" --mysql-port ").append(mysqlConfig.port);
			
			proc = Runtime.getRuntime().exec(b.toString());
			
			InputReader.createReadersForProcess(proc);
			
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		File paramFile = TestHelper.getTestFile("paramFiles/paramEchoParamFile.txt");
		configSpace = new ParameterConfigurationSpace(paramFile);
		execConfig = new AlgorithmExecutionConfiguration("ignore", System.getProperty("user.dir"), configSpace, false, false, 500);
		
		rand = new MersenneTwister();
		

		
	}
	

	@Test
	public void testPoolSwitch()
	{
		
		
			
			MySQLTargetAlgorithmEvaluator mysqlDBTae = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL+1, 25, true, MYSQL_PERMANENT_RUN_PARTITION, false, priority);
			
			
			
			List<AlgorithmRunConfiguration> runConfigs = new ArrayList<AlgorithmRunConfiguration>(5);
			for(int i=0; i < 5; i++)
			{
				ParameterConfiguration config = configSpace.getDefaultConfiguration();
			
				config.put("seed", String.valueOf(i));
				config.put("runtime", String.valueOf(2*i+1));
				AlgorithmRunConfiguration rc = new AlgorithmRunConfiguration(new ProblemInstanceSeedPair(new ProblemInstance("RunPartition"), Long.valueOf(config.get("seed"))), 1001, config,execConfig);
				runConfigs.add(rc);
				
			}
			
			System.out.println("Performing " + runConfigs.size() + " runs");
			MySQLTargetAlgorithmEvaluator tae = mysqlDBTae;
			
			List<AlgorithmRunResult> runs = tae.evaluateRun(runConfigs);
			
			
			for(AlgorithmRunResult run : runs)
			{
				ParameterConfiguration config  = run.getAlgorithmRunConfiguration().getParameterConfiguration();
				assertDEquals(config.get("runtime"), run.getRuntime(), 0.1);
				assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
				assertDEquals(config.get("quality"), run.getQuality(), 0.1);
				assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
				assertEquals(config.get("solved"), run.getRunStatus().name());
				//This executor should not have any additional run data
				assertEquals("",run.getAdditionalRunData());

			}
			
			//Same runPartition
			runs = tae.evaluateRun(runConfigs);
			for(AlgorithmRunResult run : runs)
			{
				ParameterConfiguration config  = run.getAlgorithmRunConfiguration().getParameterConfiguration();
				assertDEquals(config.get("runtime"), run.getRuntime(), 0.1);
				assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
				assertDEquals(config.get("quality"), run.getQuality(), 0.1);
				assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
				assertEquals(config.get("solved"), run.getRunStatus().name());
				//This executor should not have any additional run data
				assertEquals("",run.getAdditionalRunData());

			}
			
			tae = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL+2, 25, true, MYSQL_PERMANENT_RUN_PARTITION, false, priority);

			final MySQLTargetAlgorithmEvaluator mySQL = tae;
			runs = tae.evaluateRun(runConfigs,new TargetAlgorithmEvaluatorRunObserver()
			{

				boolean poolSwitchAttempted = false;
				@Override
				public void currentStatus(
						List<? extends AlgorithmRunResult> runs) {
					/*
					System.err.println("For this test to pass you must the following query:  UPDATE " + mysqlConfig.databaseName+".workers_" + MYSQL_POOL+1 + " SET pool_UPDATEABLE=\"" + MYSQL_POOL+2 +"\", upToDate=0");
					System.err.println("For this test to pass you must the following query:  UPDATE " + mysqlConfig.databaseName+".workers_" + MYSQL_POOL+1 + " SET pool_UPDATEABLE=\"" + MYSQL_POOL+2 +"\", upToDate=0");
					System.err.println("For this test to pass you must the following query:  UPDATE " + mysqlConfig.databaseName+".workers_" + MYSQL_POOL+1 + " SET pool_UPDATEABLE=\"" + MYSQL_POOL+2 +"\", upToDate=0");
					System.err.println("For this test to pass you must the following query:  UPDATE " + mysqlConfig.databaseName+".workers_" + MYSQL_POOL+1 + " SET pool_UPDATEABLE=\"" + MYSQL_POOL+2 +"\", upToDate=0");
					*/
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					
						e.printStackTrace();
					}
				
					MySQLPersistenceUtil.executeQueryForDebugPurposes("UPDATE " + mysqlConfig.databaseName+"." + MYSQL_POOL+1 + "_workers SET pool_UPDATEABLE=\"" + MYSQL_POOL+2 +"\", upToDate=0", mySQL);
				}
				
			});
			
			for(AlgorithmRunResult run : runs)
			{
				ParameterConfiguration config  = run.getAlgorithmRunConfiguration().getParameterConfiguration();
				assertDEquals(config.get("runtime"), run.getRuntime(), 0.1);
				assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
				assertDEquals(config.get("quality"), run.getQuality(), 0.1);
				assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
				assertEquals(config.get("solved"), run.getRunStatus().name());
				//This executor should not have any additional run data
				assertEquals("",run.getAdditionalRunData());

			}
			
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
