package ca.ubc.cs.beta.mysqldbtae.exceptions;

public class PoolChangedException extends Exception {

	/**
	 * 
	 */
	
	private final String newPool;

	private static final long serialVersionUID = 2367779157817399727L;
	
	/**
	 * Switch to a new pool
	 * @param val - no value, this is just to ensure that coders use this exception properly and read the doc
	 * @param newPool - new pool
	 */
	public PoolChangedException(Void v, String newPool)
	{
		super("changed to new pool: " + newPool);
		this.newPool = newPool;
	}
	
	public String getNewPool()
	{
		return newPool;
	}
}
