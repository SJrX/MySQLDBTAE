package ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator;

import java.util.List;

import ca.ubc.cs.beta.aclib.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.RunResult;
import ca.ubc.cs.beta.aclib.exceptions.TargetAlgorithmAbortException;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.AbstractTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;
import ca.ubc.cs.beta.mysqldbtae.persistence.RunToken;

public class MySQLDBTAE extends AbstractTargetAlgorithmEvaluator {


	private final MySQLPersistence persistence;

	
	

	public MySQLDBTAE(AlgorithmExecutionConfig execConfig, MySQLPersistence persistence) {
		super(execConfig);
		this.persistence = persistence;
	}

	@Override
	public List<AlgorithmRun> evaluateRun(List<RunConfig> runConfigs)
	{
		
		
		RunToken token = persistence.enqueueRunConfigs(runConfigs);
		
		
		List<AlgorithmRun> runs = persistence.getRunResults(token);
		
		for(AlgorithmRun run : runs)
		{
			if(run.getRunResult().equals(RunResult.ABORT))
			{
				throw new TargetAlgorithmAbortException(run);
			}
		}
		
		return runs;
	}


	@Override
	public void notifyShutdown() {
		// TODO Auto-generated method stub
		
	}

	
	
}
