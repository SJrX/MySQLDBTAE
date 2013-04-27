package ca.ubc.cs.beta.mysqldbtae.exceptions;

public class AlgorithmExecutionConfigIDBlacklistedException extends Exception {

	public AlgorithmExecutionConfigIDBlacklistedException(int i, Exception e)
	{
		super("AlgorithmExecutionConfigID has been blacklisted because of: ", e);
	}

}
