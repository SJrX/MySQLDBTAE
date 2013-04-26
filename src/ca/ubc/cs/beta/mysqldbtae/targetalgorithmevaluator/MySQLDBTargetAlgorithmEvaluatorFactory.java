package ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.mangosdk.spi.ProviderFor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.ParameterException;

import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.options.AbstractOptions;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.factory.TargetAlgorithmEvaluatorFactory;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
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
			AlgorithmExecutionConfig execConfig, AbstractOptions options) {
		
		MySQLTargetAlgorithmEvaluatorOptions opts = (MySQLTargetAlgorithmEvaluatorOptions) options;                                        
		/**
		 * Get MYSQL Connection
		 */
		
		checkOldEnvironmentVariables();
		
		String hostname = opts.host;
		String port = String.valueOf(opts.port);
		String databaseName = opts.databaseName;
		String username = opts.username;
		String password = opts.password;
		String pool = opts.pool;
		if(pool == null)
		{
			throw new ParameterException("MySQL Pool cannot be null");
		}
		pool = pool.trim();
		
		if( (pool.length() < 1) || (pool.length() > 30))
		{
			throw new ParameterException("MySQL Pool name must be between 1 and 30 characters");
		}
		boolean createTables = opts.createTables;
		
		int batchInsertSize = opts.batchInsertSize;
		
		int runPartition = opts.runPartition;
		
		//String hostname = getEnvVariable("MYSQL_HOSTNAME", opts.mysqlOptions.host);
		//String port = getEnvVariable("MYSQL_PORT","3306");
		//String databaseName = getEnvVariable("MYSQL_DATABASE_NAME", "mysql_db_tae");
		//String username = getEnvVariable("MYSQL_USERNAME");
		//String password = getEnvVariable("MYSQL_PASSWORD", "");
		//String pool = getEnvVariable("MYSQL_POOL","default");
		/*boolean createTables;
		try {
			 createTables = stringToBoolean(getEnvVariable("MYSQL_CREATE_TABLES","TRUE"));
		} catch (IllegalArgumentException e)
		{
			throw new IllegalArgumentException("Error occured while processing variable " + " MYSQL_SKIP_TABLE_CREATE, must be set to true or false.",e);
		}*/
		
		/*
		int batchInsertSize;
		try {
			 batchInsertSize = Integer.valueOf(getEnvVariable("MYSQL_BATCH_INSERT_SIZE", "500"));
		} catch(NumberFormatException e)
		{
			throw new ParameterException("MYSQL_BATCH_INSERT_SIZE enviroment variable must be an Integer not " + getEnvVariable("MYSQL_BATCH_INSERT_SIZE"));
		}
		*/
	/*
		
		try {
			runPartition = Integer.valueOf(getEnvVariable("MYSQL_RUN_PARTITION"));
		} catch(NumberFormatException e)
		{
			throw new ParameterException("MYSQL_RUN_PARTITION environment variable must be set to an Integer not " + getEnvVariable("MYSQL_RUN_PARTITION",String.valueOf(Integer.MIN_VALUE)));
		}
		*/
		boolean deletePartitionDataOnShutdown = opts.deletePartitionDataOnShutdown;
		/*
		try {
			deletePartitionDataOnShutdown = stringToBoolean(getEnvVariable("MYSQL_DELETE_PARTITION_DATA_ON_SHUTDOWN","FALSE"));
		} catch(IllegalArgumentException e)
		{
			throw new IllegalArgumentException("Error occured while processing variable " + " MYSQL_DELETE_PARTITION_DATA_ON_SHUTDOWN, must be set to true or false",e);
		}
		
		*/
		
		if(deletePartitionDataOnShutdown && runPartition < 0)
		{
			throw new ParameterException("Sorry you cannot automatically delete partitions with negative ids, this is a protection mechanism so you don't delete a bunch of data you aren't expecting");
		}
		
		
		//String illegalPathPrefixToken = "\\=2421@%!%@!!@4"; //Can't use null because that means it's required
		
		
		String pathStrip = opts.pathStrip;
		
		if(pathStrip != null)
		{
			pathStrip = pathStrip.trim();
		}
		

		if(pathStrip != null && pathStrip.trim().endsWith("/"))
		{
			log.warn("Path strip variable has a / at the end this may behave unexpectedly" );
		}
		
		MySQLPersistenceClient mysqlPersistence = new MySQLPersistenceClient(hostname, port, databaseName, username, password,pool,pathStrip, batchInsertSize, createTables, runPartition, deletePartitionDataOnShutdown, JobPriority.HIGH);
		String command = System.getProperty("sun.java.command");
		if((command == null) || (command.trim().length() < 1))
		{
			command = "JRE did not provide this information (sorry)";
		}
		
		
		try {
		mysqlPersistence.setCommand(command);
		} catch(RuntimeException e)
		{
			log.error("Unknown exception occured while trying to set the command {} (We will continue anyway, since the command is just informational)", e);
		}
		mysqlPersistence.setAlgorithmExecutionConfig(execConfig);

		return new MySQLTargetAlgorithmEvaluator(execConfig, mysqlPersistence);
		
	}
	
	private void checkOldEnvironmentVariables() {
		
		String mysqlSuppressKey = "MYSQL_SUPPRESS_ENV_WARNING";
		
		Map<String, String> envVariables = System.getenv();
		Set<String> foundTokens = new TreeSet<String>();
		
		for(Entry<String, String> ent : envVariables.entrySet())
		{
			if(ent.getKey().equals(mysqlSuppressKey))
			{
				String key = ent.getKey();
				
				try {
					boolean suppress = stringToBoolean(ent.getValue());
					
					if(suppress)
					{
						return;
					}
				} catch(IllegalArgumentException e)
				{
					
				}
				
			}
			
			
			if(ent.getKey().startsWith("MYSQL_"))
			{
				foundTokens.add(ent.getKey());
			}
		}
		
		if(foundTokens.size() > 0)
		{
			log.warn("Using the MySQL DB TAE with Environment Variables is no longer supported (you should use the options), we have detected the following ({}). If this is used for something else you can set the \"{}\" environment variable to true, and this warning will be suppressed", foundTokens, mysqlSuppressKey);
			log.warn("Sleeping for 10 seconds");
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
			
			
		return;
		
		
		
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

 /*
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

*/
	@Override
	public AbstractOptions getOptionObject() {
		
		MySQLTargetAlgorithmEvaluatorOptions opts =  new MySQLTargetAlgorithmEvaluatorOptions();
		opts.databaseName = "mysql_db_tae";
		return opts;
		
	}
	
	
	

}
