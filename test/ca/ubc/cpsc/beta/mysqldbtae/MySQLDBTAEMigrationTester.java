package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.*;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
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

import com.mchange.v2.c3p0.ComboPooledDataSource;

import ca.ubc.cs.beta.TestHelper;
import ca.ubc.cs.beta.aeatk.algorithmexecutionconfiguration.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunconfiguration.AlgorithmRunConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.AlgorithmRunResult;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfigurationSpace;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistenceUtil;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.persistence.migration.MySQLPersistenceMigration;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ec.util.MersenneTwister;

@SuppressWarnings("unused")
/**
 * This test is very different in that it loads up an old version of the database, migrates it and then checks if it can retrieve runs.
 * 
 * @author Steve Ramage <seramage@cs.ubc.ca>
 */
public class MySQLDBTAEMigrationTester {

	
	private static Process proc;
	
	private static AlgorithmExecutionConfiguration execConfig;

	private static  ParameterConfigurationSpace configSpace;
	
	private static MySQLOptions mysqlConfig;
	
	/**
	 * This test will break if you change the pool name, you'll need to fix the SQL file. 
	 */
	private static final String MYSQL_POOL = "junit_migratetest";
	

	private static final int TARGET_RUNS_IN_LOOPS = 5;
	private static final int BATCH_INSERT_SIZE = TARGET_RUNS_IN_LOOPS/10;
	
	

	private static Random rand;
	
	private JobPriority priority = JobPriority.HIGH;
	@BeforeClass
	public static void beforeClass()
	{
		mysqlConfig = MySQLDBUnitTestConfig.getMySQLConfig();
		
		File paramFile = TestHelper.getTestFile("paramFiles/paramEchoParamFile.txt");
		configSpace = new ParameterConfigurationSpace(paramFile);
		execConfig = new AlgorithmExecutionConfiguration("ignore", System.getProperty("user.dir"), configSpace, false, false, 500);
		rand = new MersenneTwister();

		
	}
	
	/*
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
			b.append(" --tae PARAMECHO --runsToBatch 1 --delayBetweenRequests 2 --idleLimit 30s" );
			b.append(" --mysql-hostname ").append(mysqlConfig.host).append(" --mysql-password ").append(mysqlConfig.password).append(" --mysql-database ").append(mysqlConfig.databaseName).append(" --mysql-username ").append(mysqlConfig.username).append(" --mysql-port ").append(mysqlConfig.port);
			proc = Runtime.getRuntime().exec(b.toString());
			
			InputReader.createReadersForProcess(proc);
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	@Test
	public void testMigration() throws IOException, PropertyVetoException, SQLException
	{

			rand = new MersenneTwister(10_10_1986);

		
			String fileToRead = MySQLDBTAEMigrationTester.class.getPackage().getName().toString().replace(".", "/")+"/old_premigration_data_for_migration_test.sql";
			System.out.println("Reading:" + fileToRead);
			InputStream in = MySQLDBTAEMigrationTester.class.getClassLoader().getResourceAsStream(fileToRead);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			
			
			
			List<String> queries= new ArrayList<String>();
			StringBuilder sb = new StringBuilder();
			
			String line  = null;
			
			while((line = br.readLine()) != null)
			{
				
				if(line.trim().startsWith("--"))
				{
					continue;
				}
				
				if(line.trim().startsWith("/*"))
				{
					continue;
				}
				
				
				
				/**
				 * WARNING: If this fails this is because of the poor parsing I do here, which is skip lines that start with -- or /*
				 */
				
				
				sb.append(line).append('\n');
				
				if(line.contains(";"))
				{
					queries.add(sb.toString());
					sb = new StringBuilder();
				}
					
			}
			
			//System.out.println(sb);
			
			//System.out.println(queries);
			//if(true) { throw new IllegalStateException("Test");}
			//
			
			String url="jdbc:mysql://" + mysqlConfig.host + ":" + mysqlConfig.port + "/" + mysqlConfig.databaseName + "?allowMultiQueries=true";
			
			ComboPooledDataSource cpds = MySQLPersistence.getComboPooledDataSource(mysqlConfig.username, mysqlConfig.password, url);			
			
		
			System.out.println("Importing old database");
			try(Connection conn = cpds.getConnection())
			{
				//Drop the target of the migration
				conn.createStatement().execute("DROP TABLE IF EXISTS `junit_migratetest_runs`");
				conn.createStatement().execute("DROP TABLE IF EXISTS `junit_migratetest_algoExecConfig`");
				
				System.out.print(".");
				
				
				for(String query : queries)
				{
					conn.createStatement().execute(query);
					System.out.print(".");
					System.out.flush();
				}
				
				
					
				
			}
			
			System.out.println("Done importing old version");
			
			System.out.println("Starting Test");
			
			
			
			
			MySQLPersistenceMigration mpm = new MySQLPersistenceMigration(mysqlConfig.host, mysqlConfig.port, mysqlConfig.databaseName, mysqlConfig.username, mysqlConfig.password,MYSQL_POOL , true);
			
			
			mpm.migrate();
			
			
			
			List<AlgorithmRunConfiguration> runConfigs = new ArrayList<AlgorithmRunConfiguration>(10);
			
			
			for(int i=0; i < TARGET_RUNS_IN_LOOPS; i++)
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
					
					/*
					normalMySQLTAE.evaluateRunsAsync(Collections.singletonList(rc), new TargetAlgorithmEvaluatorCallback() {

						@Override
						public void onSuccess(List<AlgorithmRunResult> runs) {
							
							if(!runs.get(0).getAlgorithmRunConfiguration().equals(ref.get()))
							{
								completeRuns.incrementAndGet();
							}
							
							complete.release();
							
							System.err.println("DONE:" + runs.get(0).getAlgorithmRunConfiguration() + " high: " + runs.get(0).getAlgorithmRunConfiguration().equals(ref.get()));
						}

						@Override
						public void onFailure(RuntimeException t) {
							t.printStackTrace();
						}
						
					});*/
					
					runConfigs.add(rc);
				}
			}
			
			
			MySQLTargetAlgorithmEvaluator normalMySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, 25, true, 1, false, JobPriority.NORMAL);
			
			System.out.println(normalMySQLTAE.evaluateRun(runConfigs));
		
			normalMySQLTAE.notifyShutdown();
			
			
			normalMySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, 25, true, 2, false, JobPriority.NORMAL);
			
			System.out.println(normalMySQLTAE.evaluateRun(runConfigs));
		
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
