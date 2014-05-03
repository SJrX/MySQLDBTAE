package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.beust.jcommander.ParameterException;

import ca.ubc.cs.beta.TestHelper;
import ca.ubc.cs.beta.aeatk.algorithmexecutionconfiguration.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunconfiguration.AlgorithmRunConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.AlgorithmRunResult;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfigurationSpace;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistenceUtil;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;

import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorOptions;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorkerTaskProcessor;
import ec.util.MersenneTwister;

@SuppressWarnings("unused")
public class MySQLDBTAEConcurrencyFactorTester {

	
	private static Process proc;
	
	private static AlgorithmExecutionConfiguration execConfig;

	private static  ParameterConfigurationSpace configSpace;
	
	private static MySQLOptions mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_concurrencyFactor";
	

	
	//Negative partitions won't be deleted
	private static final int MYSQL_PERMANENT_RUN_PARTITION = -10;
	
	private static final AtomicBoolean lastLineSeen = new AtomicBoolean(false);
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
			b.append(" --pool ").append(MYSQL_POOL);
			b.append(" --mysqlDatabase ").append(mysqlConfig.databaseName);
			b.append(" --timeLimit 1d --idleLimit 5s");
			b.append(" --tae PARAMECHO --runsToBatch 200 --delayBetweenRequests 1 --concurrency-factor 1 " );
			
			b.append("--mysql-hostname ").append(mysqlConfig.host).append(" --mysql-password ").append(mysqlConfig.password).append(" --mysql-database ").append(mysqlConfig.databaseName).append(" --mysql-username ").append(mysqlConfig.username).append(" --mysql-port ").append(mysqlConfig.port);
			
			proc = Runtime.getRuntime().exec(b.toString());
			
			InputReader.createReadersForProcess(proc, "WORKER>", new InputReader.LineHandler() 
			{

				@Override
				public void processLine(String line) {
					// TODO Auto-generated method stub
					//System.out.println("Saw: " + line);
					if(line.contains("Main Method Ended"))
					{
						System.out.println("Setting last line to true");
						lastLineSeen.set(true);
					}
				}
				
			});
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		File paramFile = TestHelper.getTestFile("paramFiles/paramEchoParamFileWithKilled.txt");
		configSpace = new ParameterConfigurationSpace(paramFile);
		execConfig = new AlgorithmExecutionConfiguration("ignore", System.getProperty("user.dir"), configSpace, false, false, 500);
		rand = new MersenneTwister();

	}

	@Test
	public void testConcurrencyFactor()
	{
		
			MySQLTargetAlgorithmEvaluator mySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, 25, null, MYSQL_PERMANENT_RUN_PARTITION, false, priority);
			
			//MySQLPersistenceClient  mysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, 25, null ,MYSQL_PERMANENT_RUN_PARTITION,false, priority);
			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
			
			
			List<AlgorithmRunConfiguration> runConfigs = new ArrayList<AlgorithmRunConfiguration>(20);
			for(int i=0; i < 5; i++)
			{
				ParameterConfiguration config = configSpace.getRandomParameterConfiguration(rand);
				if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED"))
				{
					//Only want good configurations
					i--;
					continue;
				} else
				{
					AlgorithmRunConfiguration rc = new AlgorithmRunConfiguration(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 1001, config,execConfig);
					runConfigs.add(rc);
				}
			}
			
			
			System.out.println(mySQLTAE.evaluateRun(runConfigs));
			
			
			runConfigs = new ArrayList<AlgorithmRunConfiguration>(20);
			for(int i=0; i < 5; i++)
			{
				ParameterConfiguration config = configSpace.getRandomParameterConfiguration(rand);
				if(config.get("solved").equals("INVALID") || config.get("solved").equals("ABORT") || config.get("solved").equals("CRASHED"))
				{
					//Only want good configurations
					i--;
					continue;
				} else
				{
					AlgorithmRunConfiguration rc = new AlgorithmRunConfiguration(new ProblemInstanceSeedPair(new ProblemInstance("TestInstance"), Long.valueOf(config.get("seed"))), 1001, config,execConfig);
					runConfigs.add(rc);
				}
			}
			
			
			MySQLPersistenceClient mysqlPeristence = MySQLPersistenceUtil.getPersistence(mySQLTAE);
			
			try (Connection conn = MySQLPersistenceUtil.getConnection(mysqlPeristence))
			{
				Statement stmt = conn.createStatement();
				
				for(int i=0; i < 1; i++)
				{
					String lockSQL = "SELECT GET_LOCK(\"" + mysqlConfig.databaseName + "." + MySQLPersistenceUtil.getRunConfigTable(mySQLTAE) + ".readLock_" + i + "\",172800);";
					
					stmt.execute(lockSQL);
					
					ResultSet rs = stmt.getResultSet();
					rs.next();
					System.out.println(lockSQL + "=>" + rs.getString(1));
				}
			
				final AtomicBoolean sawResults = new AtomicBoolean(false);
				
				mySQLTAE.evaluateRunsAsync(runConfigs, new TargetAlgorithmEvaluatorCallback() {
					
					@Override
					public void onSuccess(List<AlgorithmRunResult> runs) {
						sawResults.compareAndSet(false, true);
						
					}
					
					@Override
					public void onFailure(RuntimeException e) {
						e.printStackTrace();
						
					}
				});
				
				try {
					Thread.sleep(2048);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					return;
				}
				
				
				assertFalse("Expected that we hadn't seen the results yet", sawResults.get());
				
				String lockSQL = "SELECT RELEASE_LOCK(\"" + mysqlConfig.databaseName + "." + MySQLPersistenceUtil.getRunConfigTable(mySQLTAE) + ".readLock_" + 0 + "\");";
				
				stmt.execute(lockSQL);
				
				try {
					Thread.sleep(2048);
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					return;
				}
				
				assertTrue("Expected that we have seen the results now", sawResults.get());
				
				
			} catch (SQLException e) {

				e.printStackTrace();
			} 
			
			
	
			
			/*
			
			
			
			MySQLPersistenceUtil.executeQueryForDebugPurposes("DROP TABLE " + mysqlPeristence.TABLE_COMMAND,mysqlPeristence);
			
			MySQLPersistenceUtil.executeQueryForDebugPurposes("DROP TABLE " + mysqlPeristence.TABLE_EXECCONFIG,mysqlPeristence);
			
			MySQLPersistenceUtil.executeQueryForDebugPurposes("DROP TABLE " + mysqlPeristence.TABLE_RUNCONFIG,mysqlPeristence);
			
			MySQLPersistenceUtil.executeQueryForDebugPurposes("DROP TABLE " + mysqlPeristence.TABLE_WORKERS,mysqlPeristence);
			
			MySQLPersistenceUtil.executeQueryForDebugPurposes("DROP TABLE " + mysqlPeristence.TABLE_VERSION,mysqlPeristence);
			
			
			try {
				
				mySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, 25, false, MYSQL_PERMANENT_RUN_PARTITION, false, priority);
				
				

				
		
				fail("Expected Exception");
			} catch(ParameterException e)
			{
				//We expect this because we deleted the table and aren't creating it.
			}
			
			mySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, 25, true, MYSQL_PERMANENT_RUN_PARTITION, false, priority);
			
			mySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, 25, null, MYSQL_PERMANENT_RUN_PARTITION, false, priority);
			
			mySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, 25, false, MYSQL_PERMANENT_RUN_PARTITION, false, priority);
			*/
			
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
