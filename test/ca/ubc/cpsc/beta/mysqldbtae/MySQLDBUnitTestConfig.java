package ca.ubc.cpsc.beta.mysqldbtae;

import ca.ubc.cs.beta.aclib.misc.options.MySQLConfig;

public class MySQLDBUnitTestConfig {

	public static MySQLConfig getMySQLConfig()
	{
		
		MySQLConfig mysqlConfig = new MySQLConfig();
		mysqlConfig.host = "arrowdb.cs.ubc.ca";
		mysqlConfig.port = 4040;
		mysqlConfig.password = "october-127";
		mysqlConfig.databaseName = "mysql_db_tae_junit";
		mysqlConfig.username = "mysql_db_tae";
		
		return mysqlConfig;
		
	}
}
