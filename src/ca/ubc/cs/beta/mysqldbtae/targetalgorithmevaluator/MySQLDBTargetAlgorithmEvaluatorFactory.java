package ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator;

import java.util.Arrays;
import java.util.Collections;

import org.mangosdk.spi.ProviderFor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.ParameterException;

import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.factory.TargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;

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
		boolean createTables;
		try {
			 createTables = stringToBoolean(getEnvVariable("MYSQL_CREATE_TABLES","TRUE"));
		} catch (IllegalArgumentException e)
		{
			throw new IllegalArgumentException("Error occured while processing variable " + " MYSQL_SKIP_TABLE_CREATE",e);
		}
		
		int batchInsertSize;
		try {
			 batchInsertSize = Integer.valueOf(getEnvVariable("MYSQL_BATCH_INSERT_SIZE", "500"));
		} catch(NumberFormatException e)
		{
			throw new ParameterException("MYSQL_BATCH_INSERT_SIZE enviroment variable must be an Integer not " + getEnvVariable("MYSQL_BATCH_INSERT_SIZE"));
		}
		
		
		
		
		
		
		
		String illegalPathPrefixToken = "\\=2421@%!%@!!@4"; //Can't use null because that means it's required
		
		String pathStrip = getEnvVariable("MYSQL_PATH_STRIP",illegalPathPrefixToken);
		
		if(pathStrip.equals(illegalPathPrefixToken))
		{
			pathStrip = null;
		}
		
		if(pathStrip != null && pathStrip.trim().endsWith("/"))
		{
			log.warn("Path strip variable has a / at the end this may behave unexpectedly" );
		}
		
		MySQLPersistenceClient mysqlPersistence = new MySQLPersistenceClient(hostname, port, databaseName, username, password,pool,pathStrip, batchInsertSize, createTables);
		
		
		mysqlPersistence.setCommand(System.getProperty("sun.java.command"));
		mysqlPersistence.setAlgorithmExecutionConfig(execConfig);
	
		
		
		return new MySQLDBTAE(execConfig, mysqlPersistence);
		
	}
	
	private boolean stringToBoolean(String envVariable) {
	
		String[] trueValues = {"1","TRUE","YES","Y","T","PLEASE DO","I DON'T KNOW THE HAVOC I COULD UNLEASH"};
		String[] falseValues = {"0","FALSE","NO","N","F","NO SIR","MYSQL 5.5 LOCKING IS STUPID"};
		if(Arrays.asList(trueValues).contains(envVariable.toUpperCase().trim()))
		{
			return true;
		}
		
		if(Arrays.asList(falseValues).contains(envVariable.toUpperCase().trim()))
		{
			return false;
		}
		
	
		throw new IllegalArgumentException("Boolean variable must have been set to either TRUE or FALSE");
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
