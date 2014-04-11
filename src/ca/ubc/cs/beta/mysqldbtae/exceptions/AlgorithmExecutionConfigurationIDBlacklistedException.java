package ca.ubc.cs.beta.mysqldbtae.exceptions;

public class AlgorithmExecutionConfigurationIDBlacklistedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2565142079328968851L;

	public AlgorithmExecutionConfigurationIDBlacklistedException(int i, Exception e)
	{
		super("AlgorithmExecutionConfigurationID has been blacklisted because of: ", e);
	}

}
