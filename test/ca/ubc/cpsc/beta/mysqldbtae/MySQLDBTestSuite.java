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
	MySQLDBTAEPoolAutoDetectTester.class,
	MySQLDBTAEIdleTester.class,
	MySQLDBTAEWorkerWakingTester.class,
	MySQLDBObserverTester.class,
	MySQLDBTAETester.class,
	MySQLDBTAEPoolSwitchTester.class,
	MySQLDBTAEPriorityOrderTester.class,
	MySQLDBTAEKillRetryTester.class,
	MySQLClientKillTester.class,
	MySQLDBTAEAbortRetryTester.class,
	MySQLAsyncThreadPoolTester.class,
	MySQLDBTAEEndTimeTester.class,
	MySQLDBTAEMarkDoneTester.class,
	MySQLDBTAEPoolIdleTester.class,
	MySQLDBTAECutoffIdleTester.class,
	MySQLDBTAEJobPushBackTester.class
	
	
	//Do not add this test!  May kill database
	//MySQLDBTAELoadTest.class
})

public class MySQLDBTestSuite
{
	
	private static MySQLOptions mysqlConfig;
	
	@BeforeClass
	public static void setup()
	{
		
	
				
				
				
	}
}
