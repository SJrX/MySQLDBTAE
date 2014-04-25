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
	MySQLDBTAEAbortRetryTester.class,
	MySQLDBTAEConcurrencyFactorTester.class,
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
