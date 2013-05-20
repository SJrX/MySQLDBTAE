package ca.ubc.cpsc.beta.mysqldbtae;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	MySQLDBObserverTester.class,
	MySQLDBTAETester.class,
	MySQLDBTAEPoolSwitchTester.class,
	MySQLDBTAEPriorityOrderTester.class
})

public class MySQLDBTestSuite
{
	
}
