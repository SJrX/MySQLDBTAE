package ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator;

import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;


public final class MySQLTargetAlgorithmEvaluatorPersistenceRetriever {

	/**
	 * Debug method used for JUnit
	 * @param tae
	 * @return
	 */
	public final static MySQLPersistenceClient getPersistence(MySQLTargetAlgorithmEvaluator tae)
	{
		return tae.persistence;
		
	}
	
	private MySQLTargetAlgorithmEvaluatorPersistenceRetriever()
	{
		throw new IllegalStateException("No instantiation for you");
	}
}
