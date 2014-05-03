package ca.ubc.cs.beta.mysqldbtae.persistence;

import java.sql.Connection;

import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorPersistenceRetriever;

public class MySQLPersistenceUtil {

	
	/**
	 * Utility method that executes a query via the Persistence object
	 * 
	 * @param sql
	 * @param persistence
	 */
	public static void executeQueryForDebugPurposes(String sql, MySQLPersistence persistence)
	{
		persistence.debugExecuteUpdate(sql);
	}
	
	public static Connection getConnection(MySQLPersistence persistence)
	{
		return persistence.getConnection();
	}
	/**
	 * Utility method that executes a query via the Persistence object
	 * 
	 * @param sql
	 * @param persistence
	 */
	public static void executeQueryForDebugPurposes(String sql, MySQLTargetAlgorithmEvaluator tae)
	{
		executeQueryForDebugPurposes(sql, MySQLTargetAlgorithmEvaluatorPersistenceRetriever.getPersistence(tae));
	}
	
	public static MySQLPersistenceClient getPersistence(MySQLTargetAlgorithmEvaluator tae)
	{
		return MySQLTargetAlgorithmEvaluatorPersistenceRetriever.getPersistence(tae);
	}

	public static void executeQueryForDebugPurposes(String sql,
			TargetAlgorithmEvaluator tae) {
		
		if(!(tae instanceof MySQLTargetAlgorithmEvaluator))
		{
			throw new IllegalArgumentException("This is a debug method, and requires a MySQLTargetAlgorithmEvaluator");
		}
		executeQueryForDebugPurposes(sql, MySQLTargetAlgorithmEvaluatorPersistenceRetriever.getPersistence((MySQLTargetAlgorithmEvaluator) tae));
	}

	public static String getRunConfigTable(MySQLTargetAlgorithmEvaluator tae) {
		return MySQLTargetAlgorithmEvaluatorPersistenceRetriever.getPersistence(tae).TABLE_RUNCONFIG; 
	}
	
	public static String getWorkerTable(MySQLTargetAlgorithmEvaluator tae) {
		return MySQLTargetAlgorithmEvaluatorPersistenceRetriever.getPersistence(tae).TABLE_WORKERS; 
	}
	

}
