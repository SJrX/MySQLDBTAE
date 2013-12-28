package ca.ubc.cpsc.beta.mysqldbtae;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ca.ubc.cs.beta.TestHelper;
import ca.ubc.cs.beta.aclib.configspace.ParamConfigurationSpace;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.options.MySQLOptions;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistenceUtil;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	/* Alphabetical order to make it easy to see if all of them are added */
	MySQLAsyncThreadPoolTester.class,
	MySQLClientKillSingleRunTester.class,
	MySQLClientKillTester.class,
	MySQLDBObserverTester.class,
	MySQLDBTAEAbortRetryTester.class,
	MySQLDBTAECutoffIdleTester.class,
	MySQLDBTAEEndTimeTester.class,
	MySQLDBTAEIdleTester.class,
	MySQLDBTAEJobPushBackTester.class,
	MySQLDBTAEKillRetryTester.class,
	//MySQLDBTAELoadTest.class
	MySQLDBTAEMarkDoneTester.class,
	MySQLDBTAEPoolAutoDetectTester.class,
	MySQLDBTAEPoolIdleTester.class,
	MySQLDBTAEPoolSwitchTester.class,
	MySQLDBTAEPriorityOrderTester.class,
	MySQLDBTAETester.class,
	MySQLDBTAEWorkerWakingTester.class,
	MySQLDBTAEWorkerWakingTester.class

	
	
	//Do not add this test!  May kill database
	
})

public class MySQLDBTestSuite
{
	
	private static MySQLOptions mysqlConfig;
	
	@BeforeClass
	public static void setup()
	{
		
	
				
				
				
	}
}
