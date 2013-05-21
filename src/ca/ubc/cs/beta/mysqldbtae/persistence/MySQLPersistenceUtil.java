package ca.ubc.cs.beta.mysqldbtae.persistence;

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

}
