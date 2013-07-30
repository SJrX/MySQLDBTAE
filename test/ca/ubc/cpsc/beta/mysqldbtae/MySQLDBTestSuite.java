package ca.ubc.cpsc.beta.mysqldbtae;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
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
	
}
