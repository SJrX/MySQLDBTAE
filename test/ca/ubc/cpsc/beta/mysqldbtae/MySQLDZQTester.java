package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;

import ca.ubc.cs.beta.aeatk.options.AbstractOptions;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.init.TargetAlgorithmEvaluatorLoader;
import ca.ubc.cs.beta.dzq.exec.DangerZoneQueue;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.exceptions.PoolChangedException;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistenceUtil;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorkerOptions;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorkerTaskProcessor;

public class MySQLDZQTester {


	private static final String MYSQL_POOL = "junit_dzq_test";
	
	
	private static MySQLOptions mysqlConfig;
	
	
	
	private static Random rand;
	
	private JobPriority priority = JobPriority.HIGH;
	@BeforeClass
	public static void beforeClass()
	{
		
		mysqlConfig = MySQLDBUnitTestConfig.getMySQLConfig();
	}	
	
	
	/**
	 * This test works as follows
	 * 
	 * It creates 50 files in some temp folder, then it schedules 50 jobs to delete
	 * them.
	 * @throws InterruptedException 
	 * 
	 * 
	 * 
	 * 
	 */
	@Test
	public void testDZQ() throws InterruptedException
	{
	
		
		
		
		try {
		
			MySQLTargetAlgorithmEvaluator mySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL, 25, true, 0+1, false, priority, 1)	;		
			
			MySQLPersistenceUtil.executeQueryForDebugPurposes("TRUNCATE TABLE " + MySQLPersistenceUtil.getRunConfigTable(mySQLTAE),mySQLTAE);
			
			//File tempDir = createTempDirectory();
		
			File tempDir = Files.createTempDirectory("dzqTestTempDir").toFile();
			
			List<File> expectNotExistant = new ArrayList<>();
			
			for(int i=0; i < 50; i++)
			{
				File newTempFile = new File(tempDir + File.separator + "deleteMe-" + i);
				newTempFile.createNewFile();
				expectNotExistant.add(newTempFile);
				assertTrue("File expected to exist: " + newTempFile, newTempFile.exists());
			}
			
			String[] args = {"--mysqldbtae-pool", MYSQL_POOL, "--runtime", "60", "--exec", "rm " + tempDir + File.separator + "deleteMe-%ID%", "--mysqldbtae-hostname", mysqlConfig.host, "--mysqldbtae-port",mysqlConfig.port+"", "--mysqldbtae-database", mysqlConfig.databaseName,"--mysqldbtae-username",mysqlConfig.username, "--mysqldbtae-password", mysqlConfig.password, "--tae", "MYSQLDB", "--wait-for-runs","true", "--count", "50"};
			try {
				
				
				final MySQLTAEWorkerOptions options = new MySQLTAEWorkerOptions();
				
				options.mysqlOptions = mysqlConfig;
				options.pool = MYSQL_POOL;
				options.timeLimit = 86400*1;
				options.idleLimit = 10;
				options.delayBetweenRequests = 1;
				options.taeOptions.targetAlgorithmEvaluator = "CLI";
				options.autoAdjustRuns = true;
				final Map<String, AbstractOptions> opts = TargetAlgorithmEvaluatorLoader.getAvailableTargetAlgorithmEvaluators();
				
				CountDownLatch latch = new CountDownLatch(0);
				final MySQLTAEWorkerTaskProcessor taeTaskProcessor = new MySQLTAEWorkerTaskProcessor(System.currentTimeMillis()/1000, options, opts, latch);
				
				
				ExecutorService execService = Executors.newCachedThreadPool();
				execService.execute(new Runnable(){

					@Override
					public void run() {
						try {
							taeTaskProcessor.process();
						} catch (PoolChangedException e) {
						
							e.printStackTrace();
						}
						
					}
					
				});
				
				DangerZoneQueue.main(args);
				
				
				try 
				{
					for(File f : expectNotExistant)
					{
						assertFalse("File expected to not exist " + f, f.exists() );
					}
				} finally
				{
					execService.shutdownNow();
					System.out.println("Waiting for shutdown");
					execService.awaitTermination(24, TimeUnit.HOURS);
				}
				
			} catch (IllegalArgumentException | IllegalAccessException
					| InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("Some error occurred");
			}
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception occured");
		}
		
		
		
		
	}
	

}
