package ca.ubc.cpsc.beta.mysqldbtae;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.ubc.cs.beta.TestHelper;
import ca.ubc.cs.beta.aeatk.algorithmexecutionconfiguration.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunconfiguration.AlgorithmRunConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.AlgorithmRunResult;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.RunStatus;
import ca.ubc.cs.beta.aeatk.options.AbstractOptions;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParamFileHelper;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfigurationSpace;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.WaitableTAECallback;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.decorators.debug.EqualTargetAlgorithmEvaluatorTester;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.decorators.resource.BoundedTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.exceptions.TargetAlgorithmAbortException;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.experimental.queuefacade.basic.BasicTargetAlgorithmEvaluatorQueue;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.experimental.queuefacade.basic.BasicTargetAlgorithmEvaluatorQueueResultContext;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.experimental.queuefacade.general.TargetAlgorithmEvaluatorQueueFacade;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.init.TargetAlgorithmEvaluatorLoader;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.exceptions.PoolChangedException;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistenceUtil;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorker;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorkerOptions;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorkerTaskProcessor;
import ca.ubc.cs.beta.targetalgorithmevaluator.EchoTargetAlgorithmEvaluator;
import ec.util.MersenneTwister;

@SuppressWarnings("unused")
public class MySQLDBTAEIdleTester {

	
	private static Process proc;
	
	private static AlgorithmExecutionConfiguration execConfig;

	private static  ParameterConfigurationSpace configSpace;
	
	private static MySQLOptions mysqlConfig;
	
	private static final String MYSQL_POOL = "junit_tae_test_idle";
	

	private static final int TARGET_RUNS_IN_LOOPS = 50;
	private static final int BATCH_INSERT_SIZE = TARGET_RUNS_IN_LOOPS/10;
	
	private static final int MYSQL_RUN_PARTITION = 0;
	
	//Negative partitions won't be deleted
	private static final int MYSQL_PERMANENT_RUN_PARTITION = -10;
	
	private static final AtomicBoolean lastLineSeen = new AtomicBoolean(false);
	private static Random rand;
	
	private JobPriority priority = JobPriority.HIGH;
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
			b.append(" --timeLimit 1d --idleLimit 10s");
			b.append(" --tae PARAMECHO --runsToBatch 200 --delayBetweenRequests 1 " );
			
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
	
	
	
	
	
	/**
	 * This tests that the worker will eventually idle timeout at the appropriate time
	 * 
	 * NOTE: THIS MUST BE THE LAST TEST IN THE FILE SINCE IT WILL KILL THE WORKER
	 * 
	 * Essentially we sleep for 5 seconds,
	 * submit a job,
	 * then make sure after 15 more seconds the worker has exited
	 * 
	 * 
	 * This test is incredibly sensitive to timings unfortunately :(
	 */
	@Test
	public void testIdleShutdown()
	{
		
	
		MySQLTargetAlgorithmEvaluator mysqlDBTae = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL,  25, true, MYSQL_RUN_PARTITION, true, priority);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			Thread.currentThread().interrupt();
		}
		
		List<AlgorithmRunConfiguration> runConfigs = new ArrayList<AlgorithmRunConfiguration>(1);
		for(int i=0; i < 1; i++)
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
		
		System.out.println("Performing " + runConfigs.size() + " runs");
		TargetAlgorithmEvaluator tae =mysqlDBTae;
		List<AlgorithmRunResult> runs = tae.evaluateRun(runConfigs);
			
		
		for(int i = 0; i < 8; i++)
		{
			try {
				Thread.sleep(1000);
				try {
					if(proc.exitValue() >= 0)
					{
						fail("Process shouldn't have exited yet");
						return;
					}
				} catch(IllegalThreadStateException e)
				{
					//We have no other way of checking if a process has ended except for waiting for catching this exception if it isn't
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		assertFalse("Expected worker would NOT have exited by now ", lastLineSeen.get());
		System.out.println("Waiting 2 seconds for idle");
		
		try {
			Thread.sleep(4000);
			assertTrue("Expected worker would have exited by now ", lastLineSeen.get());
		}  catch (InterruptedException e) {
			System.out.println("Interrupted");
			Thread.currentThread().interrupt();
		}
		
		
		
	}

	/****
	 * 
	 * DO NOT PUT NEW TESTS HERE, THE PREVIOUS TEST NEEDS TO BE THE LAST ONE BECAUSE IT WILL CAUSE THE WORKER TO TIMEOUT
	 * DO NOT PUT NEW TESTS HERE, THE PREVIOUS TEST NEEDS TO BE THE LAST ONE BECAUSE IT WILL CAUSE THE WORKER TO TIMEOUT
	 * DO NOT PUT NEW TESTS HERE, THE PREVIOUS TEST NEEDS TO BE THE LAST ONE BECAUSE IT WILL CAUSE THE WORKER TO TIMEOUT
	 * DO NOT PUT NEW TESTS HERE, THE PREVIOUS TEST NEEDS TO BE THE LAST ONE BECAUSE IT WILL CAUSE THE WORKER TO TIMEOUT
	 * DO NOT PUT NEW TESTS HERE, THE PREVIOUS TEST NEEDS TO BE THE LAST ONE BECAUSE IT WILL CAUSE THE WORKER TO TIMEOUT
	 * 
	 */

	
	/***
	 * This test is an exception because it doesn't rely on the worker :)
	 * 
	 * 
	 */
	@Test
	/**
	 * This tests that garbage in the database (for instance a para file that doesn't exist
	 * doesn't cause the worker to crash)
	 * 
	 */
	public void testBadExecution()
	{
		ExecutorService execService = Executors.newCachedThreadPool();
		try {
			
			
			final MySQLTAEWorkerOptions options = new MySQLTAEWorkerOptions();
			
			options.pool = MYSQL_POOL+"badexec";
			options.timeLimit = 86400*1;
			options.idleLimit = 10;
			options.delayBetweenRequests = 1;
			options.taeOptions.targetAlgorithmEvaluator = "CLI";
			options.createTables = true;
			final Map<String, AbstractOptions> opts = TargetAlgorithmEvaluatorLoader.getAvailableTargetAlgorithmEvaluators();
			
			CountDownLatch latch = new CountDownLatch(1);
			
			
			
			final List<MySQLTAEWorkerTaskProcessor> processors = Collections.synchronizedList(new ArrayList<MySQLTAEWorkerTaskProcessor>());
			for(int i=0; i < 1; i++)
			{
			
				MySQLTAEWorkerTaskProcessor taeTaskProcessor = new MySQLTAEWorkerTaskProcessor(System.currentTimeMillis()/1000, options, opts, latch);
				processors.add(taeTaskProcessor);
					
			}
			
			
			
			for(int i=0; i < 1; i++)
			{
				final int myInt= i;
				Runnable run = new Runnable()
				{
					@Override
					public void run()
					{
						MySQLTAEWorkerTaskProcessor taeTaskProcessor = processors.get(myInt);
						try {
							
							
							
							
							taeTaskProcessor.process();
						} catch (PoolChangedException e) {
							//Can't happen
							throw new IllegalStateException(e);
						}
						
						double averageTime = taeTaskProcessor.getTotalRunFetchTimeInMS() / (double) taeTaskProcessor.getTotalRunFetchRequests();
						System.out.println("Average time per request " +  averageTime + " ms (number): " + taeTaskProcessor.getTotalRunFetchRequests());
					}
					
				};
				execService.submit(run);
			}
			
	
			
			
			try {
				File paramFile = File.createTempFile("junittests", "badexec");
				
				FileWriter writer = new FileWriter(paramFile);
				writer.write("boo { yay, hoo, yeah } [hoo]");
				
				writer.flush();
				writer.close();
				ParameterConfigurationSpace configSpace = ParamFileHelper.getParamFileParser(paramFile);
				
				
				AlgorithmExecutionConfiguration execConfig = new AlgorithmExecutionConfiguration("ignore", System.getProperty("user.dir"), configSpace, false, false, 500);
				
			
			
			MySQLTargetAlgorithmEvaluator normalMySQLTAE = MySQLTargetAlgorithmEvaluatorFactory.getMySQLTargetAlgorithmEvaluator(mysqlConfig, MYSQL_POOL+"badexec",  25, true, 1, true, JobPriority.NORMAL);
			
			
	
			
			TargetAlgorithmEvaluatorCallback tae = new TargetAlgorithmEvaluatorCallback()
			{
	
				@Override
				public void onSuccess(List<AlgorithmRunResult> runs) {
	
					System.out.println("Done");
				}
	
				@Override
				public void onFailure(RuntimeException t) {
	
					System.out.println("ROAR");
				}
				
			};
			
			
			WaitableTAECallback wait = new WaitableTAECallback(tae);
			normalMySQLTAE.evaluateRunsAsync(new AlgorithmRunConfiguration(new ProblemInstanceSeedPair(new ProblemInstance("foo"), 123), 0, configSpace.getDefaultConfiguration(),execConfig), wait);
			
			paramFile.delete();
			/*
			 writer = new FileWriter(paramFile);
			writer.write("boo { yay} [yay]");
			
			writer.flush();
			writer.close();
			*/
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			}
			
			
			latch.countDown();
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			}
			
			
			normalMySQLTAE.notifyShutdown();
			
			
			
			try {
				Thread.sleep(5000);
				if(processors.get(0).getCrashReason() != null)
				{
					processors.get(0).getCrashReason().printStackTrace();
				}
				assertNull("Expected that the worker would not crash but got " + processors.get(0).getCrashReason(), processors.get(0).getCrashReason());
			
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
			
			
			} catch(IOException e)
			{
				e.printStackTrace();
				fail("Unexpected exception occured");
			}
		} finally
		{
			execService.shutdownNow();
			
		}
		
	}
	
	
	
	
	
}
