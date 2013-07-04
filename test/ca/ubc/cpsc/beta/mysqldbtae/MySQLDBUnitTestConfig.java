package ca.ubc.cpsc.beta.mysqldbtae;

import java.util.Collections;

import com.beust.jcommander.JCommander;

import ca.ubc.cs.beta.aclib.misc.jcommander.JCommanderHelper;
import ca.ubc.cs.beta.aclib.options.MySQLOptions;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorOptions;

public class MySQLDBUnitTestConfig {

	public static MySQLOptions getMySQLConfig()
	{
		
		MySQLOptions mysqlConfig = new MySQLOptions();
		
		/*
		mysqlConfig.host = "arrowdb.cs.ubc.ca";
		mysqlConfig.port = 4040;
		mysqlConfig.password = "october-127";
		mysqlConfig.databaseName = "mysql_db_tae_junit";
		mysqlConfig.username = "mysql_db_tae";
		*/
		JCommander jcom = JCommanderHelper.getJCommander(mysqlConfig, Collections.EMPTY_MAP);
		String fileLocation;
		try {
			fileLocation = jcom.getClass().getClassLoader().getResource("mysql.opt").getFile().toString();
		} catch(NullPointerException e)
		{
			System.err.println("Could not find database file named mysql.opt on the classpath. Check the conf/ source, which has an example file to use when running unit tests");
			throw new IllegalArgumentException("Could not find database file named mysql.opt on the classpath. Check the conf/ source, which has an example to use when running unit tests");
		}
		System.out.println("Reading " + fileLocation);
		//System.exit(100);
		String[] args = { "--mysqlParameterFile " , fileLocation};
		jcom.parse(args);
		
		return mysqlConfig;
		
	}
	
	public static MySQLTargetAlgorithmEvaluatorOptions getMySQLDBTAEOptions()
	{
		MySQLTargetAlgorithmEvaluatorOptions opts =new MySQLTargetAlgorithmEvaluatorOptions();
		
		MySQLOptions mysqlOpts = getMySQLConfig();
		
		
		opts.host = mysqlOpts.host;
		opts.port = mysqlOpts.port;
		opts.password = mysqlOpts.password;
		opts.databaseName = mysqlOpts.databaseName;
		opts.username = mysqlOpts.username;
		return opts;
	}
}
