package ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator;

import org.mangosdk.spi.ProviderFor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.factory.TargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;

@ProviderFor(TargetAlgorithmEvaluatorFactory.class)
public class MySQLDBTargetAlgorithmEvaluatorFactory implements
		TargetAlgorithmEvaluatorFactory {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	@Override
	public String getName() {
		return "MYSQLDB";
	}

	
	@Override
	public TargetAlgorithmEvaluator getTargetAlgorithmEvaluator(
			AlgorithmExecutionConfig execConfig, int maxConcurrentExecutions) {
		
		/**
		 * Get MYSQL Connection
		 */
		
		String hostname = getEnvVariable("MYSQL_HOSTNAME");
		String port = getEnvVariable("MYSQL_PORT","3306");
		String databaseName = getEnvVariable("MYSQL_DATABASE_NAME", "mysql_db_tae");
		String username = getEnvVariable("MYSQL_USERNAME");
		String password = getEnvVariable("MYSQL_PASSWORD", "");
		String pool = getEnvVariable("MYSQL_POOL","default");
		
		MySQLPersistence mysqlPersistence = new MySQLPersistence(hostname, port, databaseName, username, password,pool);
		
		
		mysqlPersistence.setCommand(System.getProperty("sun.java.command"));
		mysqlPersistence.setAlgorithmExecutionConfig(execConfig);
	
		
		
		return new MySQLDBTAE(execConfig, mysqlPersistence);
		
	}
	
	private String getEnvVariable(String name)
	{
		return getEnvVariable(name, null);
	}
	
	private String getEnvVariable(String name, String defaultValue)
	{
		String propertyValue = System.getenv(name);
		if(propertyValue == null && defaultValue == null)
		{
			throw new IllegalArgumentException("To use the MYSQL DB TAE you must set enviroment variables MYSQL_HOSTNAME, MYSQL_DATABASE, MYSQL_USERNAME, MYSQL_PASSWORD, MYSQL_PORT. Property " + name + " has an illegal value");
		} else if(propertyValue == null)
		{
			log.debug("No property set {} using default {}", name, defaultValue );
			propertyValue = defaultValue;
		}
		
		return propertyValue;
	}
	
	

}
