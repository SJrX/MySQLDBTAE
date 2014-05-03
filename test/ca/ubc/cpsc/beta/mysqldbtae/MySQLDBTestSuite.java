package ca.ubc.cpsc.beta.mysqldbtae;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ca.ubc.cs.beta.aeatk.options.MySQLOptions;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	/* Alphabetical order to make it easy to see if all of them are added */
	MySQLAsyncThreadPoolTester.class,
	MySQLClientKillSingleRunTester.class,
	MySQLClientKillTester.class,
	MySQLDBObserverTester.class,
	MySQLDBTAEAutoAdjustBatchSizeTester.class,
	MySQLDBTAEAbortRetryTester.class,
	MySQLDBTAEConcurrencyFactorTester.class,
	MySQLDBTAECutoffIdleTester.class,
	MySQLDBTAEEndTimeTester.class,
	MySQLDBTAEIdleTester.class,
	MySQLDBTAEJobPushBackTester.class,
	MySQLDBTAEJobReclaimTester.class,
	MySQLDBTAEKillRetryTester.class,
	//MySQLDBTAELoadTest.class
	MySQLDBTAEMarkDoneTester.class,
	MySQLDBTAEPoolAutoDetectTester.class,
	MySQLDBTAEPoolIdleTester.class,
	MySQLDBTAEPoolSwitchTester.class,
	MySQLDBTAEPriorityOrderTester.class,
	MySQLDBTAERunConfigurationEqualityTester.class,
	MySQLDBTAETester.class,
	MySQLDBTAEWorkerWakingTester.class,
	MySQLDBTAEWorkerWakingTester.class,
	MySQLDZQTester.class,

	
	
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
