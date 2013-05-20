package ca.ubc.cs.beta.mysqldbtae.exceptions;

public class AlgorithmExecutionConfigIDBlacklistedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2565142079328968851L;

	public AlgorithmExecutionConfigIDBlacklistedException(int i, Exception e)
	{
		super("AlgorithmExecutionConfigID has been blacklisted because of: ", e);
	}

}
