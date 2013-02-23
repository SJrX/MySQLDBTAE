package ca.ubc.cpsc.beta.mysqldbtae;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ca.ubc.cs.beta.configspace.ParamConfigurationTest;
import ca.ubc.cs.beta.configspace.RandomConfigurationTest;
import ca.ubc.cs.beta.instancespecificinfo.AlgoExecutionInstanceSpecificInfoTest;
import ca.ubc.cs.beta.objectives.RunObjectiveTester;
import ca.ubc.cs.beta.probleminstance.BuggyFeatureFilesTester;
import ca.ubc.cs.beta.probleminstance.ProblemInstanceHelperTester;
import ca.ubc.cs.beta.runhistory.RunHistoryTester;
import ca.ubc.cs.beta.state.legacy.LegacyStateDeserializerTester;
import ca.ubc.cs.beta.targetalgorithmevaluator.DynamicCappingTestSet;
import ca.ubc.cs.beta.targetalgorithmevaluator.RetryCrashedTAETester;
import ca.ubc.cs.beta.targetalgorithmevaluator.TAETestSet;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	MySQLDBObserverTester.class,
	MySQLDBTAETester.class
})

public class MySQLDBTestSuite
{
	
}
