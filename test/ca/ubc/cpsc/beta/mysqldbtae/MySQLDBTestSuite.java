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
	MySQLDBTAEKillRetryTester.class
	
})

public class MySQLDBTestSuite
{
	
}
