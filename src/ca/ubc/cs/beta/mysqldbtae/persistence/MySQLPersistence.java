package ca.ubc.cs.beta.mysqldbtae.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration.StringFormat;
import ca.ubc.cs.beta.mysqldbtae.version.MySQLDBTAEVersionInfo;

import com.beust.jcommander.ParameterException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

public class MySQLPersistence {

	//private final Connection conn;
	
	protected final String TABLE_COMMAND;
	protected final String TABLE_EXECCONFIG;
	protected final String TABLE_RUNCONFIG;
	
	protected final String TABLE_WORKERS;
	
	protected final String TABLE_VERSION;
	
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
		
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
	
	
	public MySQLPersistence(String host, int port, String databaseName, String username, String password, String pool, boolean createTables)
	{
		
		if(pool == null) throw new ParameterException("Must specify a pool name ");
		if(pool.length() > 32) throw new ParameterException("Pool name must be at most 32 characters");
		
		String url="jdbc:mysql://" + host + ":" + port + "/" + databaseName;
		
		try {
			//Class.forName("com.mysql.jdbc.Driver").newInstance();
			this.url = url; 
			this.username = username;
			this.password = password;
			//conn = DriverManager.getConnection(url,username, password);
			
			
			cpds.setDriverClass( "com.mysql.jdbc.Driver" ); //loads the jdbc driver            
			cpds.setJdbcUrl( url );
			cpds.setUser(username);                                  
			cpds.setPassword(password);                                  
				
		
	
			// the settings below are optional -- c3p0 can work with defaults
			cpds.setMinPoolSize(1);                                     
			cpds.setAcquireIncrement(1);
			cpds.setMaxPoolSize(10);
			cpds.setInitialPoolSize(1);
			cpds.setAutoCommitOnClose(true);
			cpds.setMaxIdleTimeExcessConnections(120);
			cpds.setIdleConnectionTestPeriod(15);
			cpds.setPreferredTestQuery("SELECT 1");
			
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
			
			
			String versionHash = getHash(sql);
			
			if(createTables)
			{
			
				
				
				sql = sql.replace("ACLIB_POOL_NAME", pool).trim();
				
				
				String[] chunks = sql.split(";");
				 String hostname = "[UNABLE TO DETERMINE HOSTNAME]";
					try {
						hostname = InetAddress.getLocalHost().getHostName();
					} catch(UnknownHostException e)
					{ //If this fails it's okay we just use it to output to the log
						
					}
				Object args[] = { hostname, host, port };
				log.info("Attempting database connection, if nothing is happening it probably means the database is inaccessible. Please try connecting to the database from host {} to {} : {}",args );
				Connection conn = cpds.getConnection();
				for(String sqlStatement : chunks)
				{
					if(sqlStatement.trim().length() == 0) continue;
					
					PreparedStatement stmt = conn.prepareStatement(sqlStatement);
					stmt.execute();
					stmt.close();
				}
				
				conn.close();
				br.close();
				log.info("Pool Created");
			} else
			{
				log.info("Skipping table creation");
			}
			
			 TABLE_COMMAND = "commandTable_" + pool;
			 TABLE_EXECCONFIG = "execConfig_"+ pool;
			 TABLE_RUNCONFIG = "runConfigs_"+ pool;
			 TABLE_WORKERS = "workers_" + pool;
			 TABLE_VERSION = "version_" + pool;
			 
			 
			 checkVersion(versionHash);
			 
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
			
		}
		
		
	}
	
		/*
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
	*/

	private final void checkVersion(String hash)
	{
		Connection conn = null;
		boolean warning = true;
		MySQLDBTAEVersionInfo myinfo = new MySQLDBTAEVersionInfo();
		String oVersion = "<UNKNOWN>";
		String oHash = "<UNKNOWN>";
		try {
			try {
				
				conn = getConnection();
			
				PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + TABLE_VERSION + " (version, hash) VALUES (?,?) ON DUPLICATE KEY UPDATE id=id " );
				
			
				
				stmt.setString(1, myinfo.getVersion());
				stmt.setString(2, hash);
				
				stmt.execute();
				
				
				ResultSet rs = conn.createStatement().executeQuery("SELECT version, hash FROM " + TABLE_VERSION + " ORDER BY id LIMIT 1");
				 rs.next();
				
				oVersion = rs.getString(1);
				oHash = rs.getString(2);
				
				if(oHash.equals(hash))
				{
					log.trace("Hashes match {}, original version {} ", hash, oVersion);
					return;
				} else
				{
					warning = true;
				}
				
				
			} finally
			{
				if(conn != null)
				{
					conn.close();
				}
			}
		} catch(SQLException e)
		{
			log.debug("Exception encountered while detecting version: ",  e);
			warning = true;
			//We will ignore the exception here 
			//If something is really wrong something else will break
			
		}
		
		
		if(warning)
		{
			log.warn("Database version discrepancy detected. This pool was originally created by version {}, our version is {}, and this may cause problems if you aren't careful. It's also possible the pool doesn't exist yet in which case something else will fail soon.", oVersion, myinfo.getVersion());
			log.debug("Our Hash {}, Other Hash {}", hash, oHash);
		}
		
	}
	private final String getHash(String hash)
	{
		MessageDigest digest = DigestUtils.getSha1Digest();
		
		try {
			byte[] result = digest.digest(hash.getBytes("UTF-8"));
			return new String(Hex.encodeHex(result));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Could not get database version due to exception:", e);
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
