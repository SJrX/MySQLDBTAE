package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.beust.jcommander.ParameterException;

import ca.ubc.cs.beta.TestHelper;
import ca.ubc.cs.beta.aeatk.algorithmexecutionconfiguration.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aeatk.algorithmrunconfiguration.AlgorithmRunConfiguration;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfigurationSpace;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistenceUtil;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLDBTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorOptions;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;

@SuppressWarnings("unused")
public class MySQLDBTAEPoolAutoDetectTester {

	
	private static Process proc;
	
	private static AlgorithmExecutionConfiguration execConfig;

	private static  ParameterConfigurationSpace configSpace;
	
	private static MySQLOptions mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_poolauto";
	

	
	//Negative partitions won't be deleted
	private static final int MYSQL_PERMANENT_RUN_PARTITION = -10;
	

	
	private final JobPriority priority = JobPriority.HIGH;
	@BeforeClass
	public static void beforeClass()
	{
		mysqlConfig = MySQLDBUnitTestConfig.getMySQLConfig();
	
		File paramFile = TestHelper.getTestFile("paramFiles/paramEchoParamFile.txt");
		configSpace = new ParameterConfigurationSpace(paramFile);
		execConfig = new AlgorithmExecutionConfiguration("ignore", System.getProperty("user.dir"), configSpace, false, false, 500);
		
	}
	

	@Test
	public void testPoolCreation()
	{
		
			MySQLTargetAlgorithmEvaluator mySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, 25, null, MYSQL_PERMANENT_RUN_PARTITION, false, priority);
			
			//MySQLPersistenceClient  mysqlPersistence = new MySQLPersistenceClient(mysqlConfig, MYSQL_POOL, 25, null ,MYSQL_PERMANENT_RUN_PARTITION,false, priority);
			
			MySQLPersistenceClient mysqlPeristence = MySQLPersistenceUtil.getPersistence(mySQLTAE);
			
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
			
			
	}
	
	
	
	@AfterClass
	public static void afterClass()
	{

		
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
