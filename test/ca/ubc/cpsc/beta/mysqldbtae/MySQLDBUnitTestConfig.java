package ca.ubc.cpsc.beta.mysqldbtae;

import ca.ubc.cs.beta.aclib.options.MySQLOptions;

public class MySQLDBUnitTestConfig {

	public static MySQLOptions getMySQLConfig()
	{
		
		MySQLOptions mysqlConfig = new MySQLOptions();
		mysqlConfig.host = "arrowdb.cs.ubc.ca";
		mysqlConfig.port = 4040;
		mysqlConfig.password = "october-127";
		mysqlConfig.databaseName = "mysql_db_tae_junit";
		mysqlConfig.username = "mysql_db_tae";
		
		return mysqlConfig;
		
	}
}
