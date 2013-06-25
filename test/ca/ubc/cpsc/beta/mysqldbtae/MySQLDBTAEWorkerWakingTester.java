package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.*;
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
import ca.ubc.cs.beta.aclib.options.MySQLOptions;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLDBTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorOptions;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;

@SuppressWarnings("unused")
public class MySQLDBTAEWorkerWakingTester {

	
	private static Process proc;
	
	private static AlgorithmExecutionConfig execConfig;

	private static  ParamConfigurationSpace configSpace;
	
	private static MySQLOptions mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_killtest";
	

	
	//Negative partitions won't be deleted
	private static final int MYSQL_PERMANENT_RUN_PARTITION = -10;
	

	
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
			b.append(" --pool ").append(MYSQL_POOL);
			b.append(" --mysqlDatabase ").append(mysqlConfig.databaseName);
			b.append(" --timeLimit 1d --updateFrequency 2");
			b.append(" --tae PARAMECHO --delayBetweenRequests 240 --idleLimit 300s --runsToBatch 100" );
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
	public void testWorkerWakeup()
	{
		
			MySQLDBTargetAlgorithmEvaluatorFactory fact = new MySQLDBTargetAlgorithmEvaluatorFactory();
			
			MySQLTargetAlgorithmEvaluatorOptions opts = new MySQLTargetAlgorithmEvaluatorOptions();
			
			
			opts.databaseName = mysqlConfig.databaseName;
			opts.wakeUpWorkersOnSubmit = true;
			opts.pool = MYSQL_POOL;
			opts.deletePartitionDataOnShutdown = true;
			
			TargetAlgorithmEvaluator tae = fact.getTargetAlgorithmEvaluator(execConfig, opts);
			
				
			List<RunConfig> runConfigs = new ArrayList<RunConfig>(5);
			for(int i=0; i < 5; i++)
			{
				ParamConfiguration config = configSpace.getDefaultConfiguration();
			
				config.put("seed", String.valueOf(i));
				config.put("runtime", String.valueOf(2*i+1));
				RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance("RunPartition"), Long.valueOf(config.get("seed"))), 1001, config);
				runConfigs.add(rc);
				
			}
			
			long startTime = System.currentTimeMillis();
			
			List<AlgorithmRun> runs = tae.evaluateRun(runConfigs);
			
			
			long endTime = System.currentTimeMillis();
			
			assertTrue("Expected SleepTime to be less than 10 seconds with worker wakeup", (endTime - startTime) < 10000);
			
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
			
			tae.notifyShutdown();
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