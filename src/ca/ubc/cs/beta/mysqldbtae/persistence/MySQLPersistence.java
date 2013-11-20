package ca.ubc.cs.beta.mysqldbtae.persistence;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLNonTransientException;
import java.sql.SQLTransientException;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.mysqldbtae.version.MySQLDBTAEVersionInfo;

import com.beust.jcommander.ParameterException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.mysql.jdbc.exceptions.MySQLQueryInterruptedException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLTransientException;

public class MySQLPersistence {

	//private final Connection conn;
	
	/**
	 * String that workers will have in their query when sleeping
	 */
	public static final String SLEEP_STRING = "MySQLWorker Sleeping on pool: ";
	
	
	public static final int MAX_RETRYS_ATTEMPTS = 100;
	public static final int MAX_SLEEP_TIME_IN_MS = 300000;
	
	
	public final String TABLE_COMMAND;
	public final String TABLE_EXECCONFIG;
	public final String TABLE_RUNCONFIG;
	public final String TABLE_WORKERS;
	public  final String TABLE_VERSION;
	
	
	protected final String POOL;
	
	protected final double MYSQL_SLEEP_CUTOFF_WINDOW = 5.0;
	
	
	
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
	
	ComboPooledDataSource cpds = new ComboPooledDataSource();

	protected final String SLEEP_COMMENT_TEXT;
	
	protected final String DATABASE;
	
	public MySQLPersistence(String host, int port, String databaseName, String username, String password, String pool, Boolean createTables)
	{
		
		if(pool == null) throw new ParameterException("Must specify a pool name ");
		if(pool.length() > 32) throw new ParameterException("Pool name must be at most 32 characters");
		
		if(!pool.matches("^[0-9a-zA-Z\\$_]+$"))
		{
			throw new ParameterException("Pool name can only consist of alpha numeric characters underscores and $");
		}
		
		if(databaseName == null)
		{
			throw new ParameterException("Must specify a valid database name");
		}
		
		if(username == null)
		{
			throw new ParameterException("Must specify a valid username");
		}
		
		
		String url="jdbc:mysql://" + host + ":" + port + "/" + databaseName;
		
		try {
			
			
			
			cpds.setDriverClass( "com.mysql.jdbc.Driver" ); //loads the jdbc driver            
			cpds.setJdbcUrl( url );
			cpds.setUser(username);                                  
			cpds.setPassword(password);                                  
				
		
	
			// the settings below are optional -- c3p0 can work with defaults
			cpds.setMinPoolSize(1);                                     
			cpds.setAcquireIncrement(1);
			cpds.setMaxPoolSize(200);
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
			
			 TABLE_COMMAND = pool + "_commandTable";
			 TABLE_EXECCONFIG = pool + "_execConfig";
			 TABLE_RUNCONFIG = pool + "_runConfigs";
			 TABLE_WORKERS = pool + "_workers";
			 TABLE_VERSION = pool + "_version";
			 POOL = pool;
			 this.SLEEP_COMMENT_TEXT = SLEEP_STRING + POOL;
			 this.DATABASE = databaseName;
			 
			
			 
			boolean databaseExists = false;
			
			Connection conn = cpds.getConnection();
			try {
				try {
					conn.createStatement().execute("SELECT 1 FROM " + TABLE_VERSION + " LIMIT 1");
					databaseExists = true;
				} catch(SQLException e)
				{
					log.info("Autodetecting of tables for pool {} failed suggesting it doesn't exist, will create tables", pool);
					
				}
			} finally
			{
				conn.close();
			}
						
			createTables = (createTables == null) ? !databaseExists : createTables;

			if(!createTables && !databaseExists)
			{
				throw new ParameterException("Pool will not be created and the database does not exist please check your command line arguments pool :" + pool);
			}
			
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
				Object args[] = { url, hostname, host, port };
				log.info("Attempting database connection to {} , if nothing is happening it probably means the database is inaccessible. Please try connecting to the database from host {} to {} : {}",args );
				conn = cpds.getConnection();
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
			
			
			 
			 checkVersion(versionHash);
			 
		} catch (Throwable e) {
			e.printStackTrace();
			if(e instanceof RuntimeException)
			{
				throw (RuntimeException) e;
			} else
			{
				throw new IllegalStateException("Unexpected Exception ", e);
			}
				
			
			
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
	
	/**
	 * Executes a SQL query against the database
	 * 
	 * This is mainly used for debug purposes and is not very robust, it is protected 
	 * to prevent abuse, and you must use the {@link ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistenceUtil} class to actually invoke it
	 * 
	 * @param query SQL Query to execute
	 */
	protected void debugExecuteUpdate(String query)
	{
		try {
			Connection conn = null;
			try {
				 conn = getConnection();
				
				
				Statement stmt = conn.createStatement();
				log.debug("Executing Query: {} " ,query);
				boolean isResultSet = stmt.execute(query);
				
				if(isResultSet)
				{
					log.debug("Query has result set ");
					ResultSet rs = stmt.getResultSet();
					int columnCount = rs.getMetaData().getColumnCount();
					
					
					
					StringBuilder sb = new StringBuilder();
					int j=0;
					while(rs.next())
					{
						for(int i=1; i <= columnCount; i++)
						{
							sb.append(rs.getString(i));
							if( i != columnCount)
							{
								sb.append(",");
							}
							
						}
						
						
						log.debug("Result ({}) : {} )",j, sb.toString());
						j++;
					}
					
					
					
				} else
				{
					int updateCount = stmt.getUpdateCount();
					if(updateCount != -1)
					{
						log.debug("Query completed successfully, update count is {} ", updateCount );
					} else
					{
						log.debug("Query completed successfully");
					}
					
				}
			
			} finally
			{
				if(conn != null) conn.close();
			}
		} catch(SQLException e)
		{
			throw new IllegalStateException(e);
		}
	}
	


	

	protected void executePS(final PreparedStatement s)
	{
		
			execute(new Callable<Void>() {

			@Override
			public Void call() throws SQLException {
				s.execute();
				return null;
			}
			
		});
	}
	
	protected  ResultSet executePSQuery(final PreparedStatement s)
	{
		return execute(new Callable<ResultSet>() {

			@Override
			public ResultSet call() throws SQLException {
				return s.executeQuery();
			}
			
		});
	}
	
	protected int executePSUpdate(final PreparedStatement s)
	{
		return execute(new Callable<Integer>() {

			@Override
			public Integer call() throws SQLException {
				return s.executeUpdate();
			}
			
		});
		
	}

	private <K> K execute(Callable<K> f)
	{
		
		long sleepTime = 0;
		int retryFailures = 0;
		for(int i=0; i < MAX_RETRYS_ATTEMPTS; i++ )
		{
			try {
				return f.call();
			} catch(MySQLQueryInterruptedException e)
			{
				log.warn("Unexpected query interruption, probably a race condition with worker wakeup. (You can disregard this warning, it's for developers)",e);
			} catch(com.mysql.jdbc.exceptions.jdbc4.MySQLTransientException e)
			{
				log.debug("Transient Exception Detection disregarding",e);
			} catch(com.mysql.jdbc.exceptions.MySQLTransientException e)
			{
				log.debug("Transient Exception Detection disregarding",e);
			
			} catch(com.mysql.jdbc.exceptions.MySQLNonTransientException e)
			{
				log.warn("Unexpected Non Transient Exception ", e);
			} catch(com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientException e)
			{
				log.warn("Unexpected Non Transient Exception ", e);
			} catch(SQLException e)
			{
				log.warn("Unexpected exception while executing query, but logging {}", e);
			} catch(Exception e)
			{
				log.error("This was unexpected, an exception that wasn't a SQL exception", e);
			}
			
			if(sleepTime > 0)
			{
				log.info("Query failed, sleeping for {} seconds before retrying query (if you ", sleepTime / 1000);
				try {
					Thread.sleep(sleepTime);
				} catch(InterruptedException e)
				{
					Thread.currentThread().interrupt();
					throw new IllegalStateException("Thread interrupted while sleeping, probably shutting down?");
				}
				
			}
			
			sleepTime = Math.min( (long) ((sleepTime + 1000)*1.5), MAX_SLEEP_TIME_IN_MS);
		}
		
		throw new IllegalStateException("Database queries have failed too many times in a row, giving up");
	
	}
	
}
