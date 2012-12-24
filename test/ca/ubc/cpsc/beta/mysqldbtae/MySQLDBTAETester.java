package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
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
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.decorators.EqualTargetAlgorithmEvaluatorTester;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLDBTAE;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ca.ubc.cs.beta.targetalgorithmevaluator.EchoTargetAlgorithmEvaluator;

import ec.util.MersenneTwister;


public class MySQLDBTAETester {

	
	private static Process proc;
	
	private static AlgorithmExecutionConfig execConfig;

	private static  ParamConfigurationSpace configSpace;
	
	private static MySQLConfig mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_test_4";
	

	private static final int TARGET_RUNS_IN_LOOPS = 2000;
	private static final int BATCH_INSERT_SIZE = TARGET_RUNS_IN_LOOPS/10;
	
	@BeforeClass
	public static void beforeClass()
	{
		
		mysqlConfig = new MySQLConfig();
		mysqlConfig.host = "arrowdb.cs.ubc.ca";
		mysqlConfig.port = 4040;
		mysqlConfig.password = "october-127";
		mysqlConfig.databaseName = "mysql_db_tae";
		mysqlConfig.username = "mysql_db_tae";
		
		try {
			StringBuilder b = new StringBuilder();
			
			b.append("java -cp ");
			b.append(System.getProperty("java.class.path"));
			b.append(" ");
			b.append(MySQLTAEWorker.class.getCanonicalName());
			b.append(" --pool ").append(MYSQL_POOL);
			b.append(" --timeLimit ").append(100000);
			b.append(" --tae PARAMECHO --runsToBatch 20 --delayBetweenRequests 1 " );
			proc = Runtime.getRuntime().exec(b.toString());
			
			InputReader.createReadersForProcess(proc);
			
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		File paramFile = TestHelper.getTestFile("paramFiles/paramEchoParamFile.txt");
		configSpace = new ParamConfigurationSpace(paramFile);
		execConfig = new AlgorithmExecutionConfig("ignore", System.getProperty("user.dir"), configSpace, false, false, 500);
		

		
	}
	
	
	@Test
	public void testRetrieval()
	{
		
			
			MySQLPersistenceClient  mysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, 25);
			try {
			mysqlPersistence.setCommand(System.getProperty("sun.java.command"));
			} catch(RuntimeException e)
			{
				e.printStackTrace();
				throw e;
			}
			mysqlPersistence.setAlgorithmExecutionConfig(execConfig);
			
			MySQLDBTAE mysqlDBTae = new MySQLDBTAE(execConfig, mysqlPersistence);
			
			configSpace.setPRNG(new MersenneTwister());
			
			List<RunConfig> runConfigs = new ArrayList<RunConfig>(TARGET_RUNS_IN_LOOPS);
			for(int i=0; i < TARGET_RUNS_IN_LOOPS; i++)
			{
				ParamConfiguration config = configSpace.getRandomConfiguration();
				if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED"))
				{
					//Only want good configurations
					i--;
					continue;
				} else
				{
					RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 1001, config);
					runConfigs.add(rc);
				}
			}
			
			System.out.println("Performing " + runConfigs.size() + " runs");
			TargetAlgorithmEvaluator tae = new EqualTargetAlgorithmEvaluatorTester(mysqlDBTae, new EchoTargetAlgorithmEvaluator(execConfig));
			List<AlgorithmRun> runs = tae.evaluateRun(runConfigs);
			
			
			for(AlgorithmRun run : runs)
			{
				ParamConfiguration config  = run.getRunConfig().getParamConfiguration();
				assertDEquals(config.get("runtime"), run.getRuntime(), 0.1);
				assertDEquals(config.get("runlength"), run.getRunLength(), 0.1);
				assertDEquals(config.get("quality"), run.getQuality(), 0.1);
				assertDEquals(config.get("seed"), run.getResultSeed(), 0.1);
				assertEquals(config.get("solved"), run.getRunResult().name());
				//This executor should not have any additional run data
				assertEquals("",run.getAdditionalRunData());

			}
			
			
		
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
