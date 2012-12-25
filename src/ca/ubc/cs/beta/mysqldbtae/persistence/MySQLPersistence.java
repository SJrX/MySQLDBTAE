package ca.ubc.cs.beta.mysqldbtae.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.sql.SQLException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.ParameterException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

public class MySQLPersistence {

	private final Connection conn;
	
	protected final String TABLE_COMMAND;
	protected final String TABLE_EXECCONFIG;
	protected final String TABLE_RUNCONFIG;
	
	protected final String TABLE_WORKERS;
	
	
	
	
	
	
	

	
	
	
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Worker ID 
	 */
	

	
	
	


	
	protected Connection getConnection()
	{

		try {
			return cpds.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new IllegalStateException("Couldn't get connection");
		}
	}
	
	
	
	
	private final String url;
	private final String username;
	private final String password;
	ComboPooledDataSource cpds = new ComboPooledDataSource();
	
	public MySQLPersistence(String host, int port, String databaseName, String username, String password, String pool)
	{
		
		if(pool == null) throw new ParameterException("Must specify a pool name ");
		if(pool.length() > 15) throw new ParameterException("Pool name must be at most 15 characters");
		
		String url="jdbc:mysql://" + host + ":" + port + "/" + databaseName;
		
		try {
			//Class.forName("com.mysql.jdbc.Driver").newInstance();
			this.url = url; 
			this.username = username;
			this.password = password;
			conn = DriverManager.getConnection(url,username, password);
			
			
			cpds.setDriverClass( "com.mysql.jdbc.Driver" ); //loads the jdbc driver            
			cpds.setJdbcUrl( url );
			cpds.setUser(username);                                  
			cpds.setPassword(password);                                  
				
			// the settings below are optional -- c3p0 can work with defaults
			cpds.setMinPoolSize(5);                                     
			cpds.setAcquireIncrement(5);
			cpds.setMaxPoolSize(20);
			
			
			
			BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("tables.sql")));
			
			StringBuilder sb = new StringBuilder();
			
			String line;
			boolean nothingFound = true;
	    	while ((line = br.readLine()) != null) {
	    		sb.append(line).append("\n");
	    		nothingFound = false;
	    	} 
	    	
	    	if(nothingFound) throw new IllegalStateException("Couldn't load tables.sql");
			String sql = sb.toString();
			sql = sql.replace("ACLIB_POOL_NAME", pool).trim();
			
			
			String[] chunks = sql.split(";");
			
			for(String sqlStatement : chunks)
			{
				if(sqlStatement.trim().length() == 0) continue;
				
				PreparedStatement stmt = conn.prepareStatement(sqlStatement);
				stmt.execute();
				stmt.close();
			}
			
			br.close();
			log.info("Pool Created");
			 TABLE_COMMAND = "commandTable_" + pool;
			 TABLE_EXECCONFIG = "execConfig_"+ pool;
			 TABLE_RUNCONFIG = "runConfigs_"+ pool;
			 TABLE_WORKERS = "workers_" + pool;
			 
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
			
		}
		
		
	}
	
		
	public void startTransaction()
	{
		
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public void commitTransaction()
	{
		
		try {
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
		
	}
	

	public void shutdown()
	{
		try {
			DataSources.destroy(cpds);
		} catch (SQLException e) {
			log.error("Unknown exception occurred {}",e );
		}
	}

}