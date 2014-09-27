package ca.ubc.cs.beta.mysqldbtae.persistence;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLRecoverableException;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aeatk.algorithmexecutionconfiguration.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunconfiguration.AlgorithmRunConfiguration;
import ca.ubc.cs.beta.aeatk.json.JSONHelper;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParamFileHelper;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfigurationSpace;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration.ParameterStringFormat;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.exceptions.AlgorithmExecutionConfigurationIDBlacklistedException;
import ca.ubc.cs.beta.mysqldbtae.version.MySQLDBTAEVersionInfo;

import com.beust.jcommander.ParameterException;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import com.mysql.jdbc.exceptions.MySQLQueryInterruptedException;

public class MySQLPersistence implements AutoCloseable{

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
			throw new IllegalStateException("Couldn't get connection",e);
		}
	}
	
	ComboPooledDataSource cpds; 

	protected final String SLEEP_COMMENT_TEXT;
	
	protected final String DATABASE;
	
	public MySQLPersistence(String host, int port, String databaseName, String username, String password, String pool, Boolean createTables)
	{

		pool = pool.trim();
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
		
		
		
		
		try {
			
			String url="jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?allowMultiQueries=true";
			
			cpds = getComboPooledDataSource(username, password, url);
			
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

	/**
	 * @param username
	 * @param password
	 * @param url
	 * @throws PropertyVetoException
	 */
	public static ComboPooledDataSource getComboPooledDataSource(String username, String password,
			String url) throws PropertyVetoException {
		ComboPooledDataSource cpds = new ComboPooledDataSource();;
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
		
		return cpds;
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
	
	private final AtomicBoolean closed = new AtomicBoolean(false);

	/**
	 * Stores a mapping of blacklisted keys.
	 */
	protected final Map<Integer, AlgorithmExecutionConfigurationIDBlacklistedException> blacklistedKeys = new HashMap<Integer, AlgorithmExecutionConfigurationIDBlacklistedException>();


	/**
	 * Stores primary keys of execution configurations
	 */
	protected final Map<Integer, AlgorithmExecutionConfiguration> execConfigMap = new ConcurrentHashMap<Integer, AlgorithmExecutionConfiguration>();
	

	protected AlgorithmExecutionConfiguration getAlgorithmExecutionConfiguration(int execConfigID) throws SQLException, AlgorithmExecutionConfigurationIDBlacklistedException {
		
	
		AlgorithmExecutionConfigurationIDBlacklistedException e = blacklistedKeys.get(execConfigID);
		
		if(e != null)
		{
			throw e;
		}
		try {
			if(!execConfigMap.containsKey(execConfigID)) 
			{
			 StringBuilder sb = new StringBuilder();
			 sb.append("SELECT algorithmExecutable, algorithmExecutableDirectory, parameterFile, executeOnCluster, deterministicAlgorithm, cutoffTime, algorithmExecutionConfigurationJSON  FROM ").append(TABLE_EXECCONFIG).append("  WHERE algorithmExecutionConfigID = " + execConfigID);
			 Connection conn = null;
			 try {
				 conn = getConnection();
			 
				 PreparedStatement stmt = conn.prepareStatement(sb.toString());
				 ResultSet rs = stmt.executeQuery();
				 
				 
				 
				 
				 boolean hasNextResult = rs.next();
				 
				 if(!hasNextResult)
				 {
					 throw new IllegalStateException("Database table " + TABLE_EXECCONFIG + " does not have an entry for " + execConfigID + " tables must be corrupted or something");
				 }
				 
				 AlgorithmExecutionConfiguration execConfig;
				 
				 try 
				 {
					 if ((rs.getString(7).trim().length()) == 0)
					 {
						 throw new IllegalArgumentException("No JSON found in database, maybe this is an old version");
					 }
						
					execConfig = JSONHelper.getAlgorithmExecutionConfiguration(rs.getString(7));
					 
				 } catch(RuntimeException e2)
				 {
					 log.warn("Algorithm Execution Configuration ID: {} has no JSON stored, falling back to column representation but note that this may have lost information, such as context", execConfigID);
					 log.debug("Exception when parsing JSON was ", e2);
					 String algorithmExecutable = rs.getString(1);
					 String algorithmExecutionDirectory = rs.getString(2);
					 
					 ParameterConfigurationSpace paramFile = ParamFileHelper.getParamFileParser(rs.getString(3));
					 
					 boolean executeOnCluster = rs.getBoolean(4);
					 boolean deterministicAlgorithm = rs.getBoolean(5);
					 double cutoffTime = rs.getDouble(6);
					 
					 
					execConfig = new AlgorithmExecutionConfiguration(algorithmExecutable, algorithmExecutionDirectory, paramFile,  executeOnCluster, deterministicAlgorithm, cutoffTime);
					 
					 
				 }
				 
				 
				 execConfigMap.put(execConfigID, execConfig);
					
				 conn.close();
				 rs.close();
				 stmt.close();
			 } finally
			 {
				 if(conn != null) 
				 {
					 conn.close();
				 }
			 }
			}
			
			
			return execConfigMap.get(execConfigID);
		} catch(RuntimeException rt)
		{
			log.error("Exception occured while trying to process execConfigID: " + execConfigID , rt);
			
			AlgorithmExecutionConfigurationIDBlacklistedException e2 = new AlgorithmExecutionConfigurationIDBlacklistedException(execConfigID, rt); 
			blacklistedKeys.put(execConfigID, e2);
			throw e2;
		}
		
		
	}
	
	
	
	public boolean isClosed()
	{
		return closed.get();
	}
	public void shutdown()
	{
		if(closed.compareAndSet(false, true))
		{
			try {
				DataSources.destroy(cpds);
			} catch (SQLException e) {
				log.error("Unknown exception occurred on shutdown {}",e );
			}
		} else
		{
			log.debug("Already closed");
		}
	}
	
	@Override
	public void close() {
		shutdown();		
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

	public ResultSet executeStatement(final Statement stmt, final String string) {
		return execute(new Callable<ResultSet>() {

			@Override
			public ResultSet call() throws SQLException {
				 return stmt.executeQuery(string);
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
			} catch(com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException e)
			{
				log.error("Exception while processing query:", e);
				
				
				throw new IllegalStateException("Syntax Error in SQL statement, retrying will not fix this", e);
			} catch(MySQLQueryInterruptedException e)
			{
				log.warn("Unexpected query interruption, probably a race condition with worker wakeup. (You can disregard this warning, it's for developers)",e);
			} catch( com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException e) 
			{
				log.debug("Dead lock, we will try it again");
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
			} catch(com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e)
			{
				throw new IllegalStateException("Unfortunately this query died, and restarting it will never get it to work (maybe the connection closed), can only shutdown.");
			}catch(SQLRecoverableException e)
			{
				throw new IllegalStateException("Unfortunately this query died, and restarting it will never get it to work (maybe the connection closed), can only shutdown.");
			} catch(SQLException e)
			{
				log.warn("Unexpected exception while executing query, but logging ", e);
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
	
	/**
	 * Repairs the state of the table, this method is meant to not put load on the database,
	 * that is why we do a bunch of selects and conditional updates, and without a transaction. 
	 */
	public void fixJobState() {
		try ( Connection conn = getConnection())
		{
			try
			{
				conn.createStatement().execute("SELECT GET_LOCK(\"" + this.DATABASE + ".workerFixLock\",172800);" );
			
				TreeSet<String> deadWorkers = new TreeSet<String>();
				String[] workers = new String[5];
				//Don't want to grab too many workers at any one time
				StringBuilder sb = new StringBuilder("SELECT workerUUID, jobID FROM ").append(TABLE_WORKERS).append(" WHERE status='RUNNING' AND worstCaseNextUpdateWhenRunning < NOW() LIMIT 100");
			
				int i=0;
				try (Statement stmt = conn.createStatement())
				{
				
					ResultSet rs =  stmt.executeQuery(sb.toString());
					
				
					
					i++;
					while(rs.next())
					{
						
						deadWorkers.add(rs.getString(1));
						workers[i%workers.length] = " " + rs.getString(2) + " (" + rs.getString(1) + ")";
					}
				}

				TreeSet<String> workersToLog = new TreeSet<>();
				
				for(String s : workers)
				{
					if (s != null)
					{
						workersToLog.add(s);
					}
				}
				
				boolean cleanupRunsTable = false;
				if(deadWorkers.size() > 0)
				{
					try (Statement stmt = conn.createStatement())
					{
						sb = new StringBuilder();
						sb.append("UPDATE ").append(TABLE_WORKERS).append( " SET status='DONE', crashInfo='Missed worstcase end time checkin' WHERE workerUUID IN (");
						
						for(String workerUUID : deadWorkers)
						{
							sb.append("\""+workerUUID+"\" ").append(",");
						}
						sb.setCharAt(sb.length() - 1, ')');
						sb.append(" AND status='RUNNING' AND worstCaseNextUpdateWhenRunning < NOW()");
						
						//System.out.println(sb);
						int updateCount = stmt.executeUpdate(sb.toString());
						
						if(deadWorkers.size() > 0 )
						{
							log.warn("Detected {} workers that did not respond in time, successfully repaired {} of them (these numbers may not be equal, it just means something changed in the interim.). Some of the workers that did not seem updated were: {} ", deadWorkers.size(), updateCount, workersToLog);
							log.debug("Worker UUIDs:{}", deadWorkers);
						}
						if(updateCount > 0)
						{
							cleanupRunsTable = true;
						}
						
					}
				
				}
				
				sb = new StringBuilder();
	
				//Don't grab too many jobs at any one time.
				sb.append("SELECT runConfigID FROM ").append(TABLE_RUNCONFIG).append( " WHERE status=\"ASSIGNED\" AND worstCaseNextUpdateWhenAssigned < NOW() LIMIT 5000");
				
				Set<Integer> keys = new TreeSet<>();
	
				try (Statement stmt = conn.createStatement())
				{
					//System.out.println(sb.toString());
					
					ResultSet rs = stmt.executeQuery(sb.toString());
					
					while(rs.next())
					{
						keys.add(rs.getInt(1));
					}
					
				}
				
				if(keys.size() > 0)
				{
					log.warn("Detected {} runs that have seemingly not updated in time, setting back to NEW", keys.size());
					
					try(Statement stmt = conn.createStatement())
					{
						sb = new StringBuilder();
	
						sb.append("UPDATE ").append(TABLE_RUNCONFIG).append( " SET status='NEW', retryAttempts=retryAttempts+1 WHERE status=\"ASSIGNED\"  AND runConfigID IN (");
							
						for(Integer key : keys)
						{
							sb.append("\""+key+"\"").append(",");
						}
						sb.setCharAt(sb.length() - 1, ')');
						sb.append(" AND worstCaseNextUpdateWhenAssigned < NOW()");
						//
						//System.out.println(sb.toString());
						int updatedRuns = stmt.executeUpdate(sb.toString());
						log.debug("Moved {} runs back to NEW by ID", updatedRuns);
					}
				}
				
				
				
				
				
				
				if(cleanupRunsTable)
				{
					sb = new StringBuilder();
	
					sb.append("UPDATE ").append(TABLE_RUNCONFIG).append( " SET status='NEW', retryAttempts=retryAttempts+1 WHERE status=\"ASSIGNED\"  AND workerUUID IN (");
						
	
					for(String workerUUID : deadWorkers)
					{
						sb.append("\""+workerUUID+"\"").append(",");
					}
					sb.setCharAt(sb.length() - 1, ')');
					sb.append(" AND worstCaseNextUpdateWhenAssigned < NOW()");
					
					//
					try (Statement stmt = conn.createStatement())
					{
					
						int updatedRuns = stmt.executeUpdate(sb.toString());
						
						if(updatedRuns > 0)
						{ 
							log.debug("Moved {} runs back to NEW by worker", updatedRuns);
						}
					}
					
					
				}
				
			} finally
			{
				try {
					conn.createStatement().execute("SELECT RELEASE_LOCK(\"" + this.DATABASE + ".workerFixLock\");");
				} catch(SQLException e)
				{
					log.error("Couldn't release lock, this is very bad. Deadlock possible ", e);
				}
					

				
			}
			
			//Now check for runs that may have been orphaned
			
			
		} catch (SQLException e) {
			log.error("Couldn't fix job state, exception:",e);

		}
		
		
		
	}

	
	protected List<AlgorithmRunConfiguration> getRuns(int n, int delayBetweenRequests, Map<AlgorithmRunConfiguration, String> runConfigIDMap, int lockToGet, String workerUUID, int worstCaseMultplier,  int minWorstCaseTime, String statusToRequest, Map<AlgorithmRunConfiguration, Integer> runConfigurationToRunParitionMap)
	{

		StringBuffer sb = new StringBuffer();
		
		
		try {
		
		
			//Pull workers off the queue in order of priority, do not take workers where we already tried
			//This isn't a perfect heuristic. I'm hoping it's good enough.
			
			int lock = -1;  
			
			if(lockToGet > 0)
			{
				lock = lockToGet;
				//We won't actually check the result and so it doesn't matter if it's once every 2 days
				sb.append("SELECT GET_LOCK(\"" + this.DATABASE + "." + TABLE_RUNCONFIG + ".readLock_" + lock + "\",172800);\n" );
			}
			
			sb.append("UPDATE ").append(TABLE_RUNCONFIG).append( " A JOIN (\n\t").append(
					"SELECT runConfigID, priority FROM (");
					int i=0;
					for(JobPriority job : JobPriority.values())
					{
						sb.append("\n\t\t(SELECT runConfigID,").append(i).append(" AS priority FROM ").append(TABLE_RUNCONFIG).append(" WHERE status=\"").append(statusToRequest).append("\" AND priority=\"").append(job).append("\" ORDER BY runConfigID LIMIT " + n +  ")\n\t\t").append("UNION ALL");
						i++;
					}
			
					sb.replace(sb.length()-"UNION ALL".length(), sb.length(), "\n");
					
							
					sb.append("\t) innerTable ORDER BY priority DESC LIMIT " + n + "\n").append(
					" ) B ON B.runConfigID=A.runConfigID SET status=\"ASSIGNED\", workerUUID=\"" + workerUUID.toString() + "\", retryAttempts = retryAttempts+1,worstCaseNextUpdateWhenAssigned=DATE_ADD(NOW(),INTERVAL ").append(Math.max(worstCaseMultplier*delayBetweenRequests,minWorstCaseTime)).append(" SECOND)");
					
					
			//System.out.println(sb.toString());
			
				
			
			try (Connection conn = getConnection())
			{
			
				Statement stmt = conn.createStatement();
				
				
						
						
						//conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
				
				//stmt.addBatch(sb.toString());
				
				
				//executePS(stmt);
				
				//if(true) return Collections.emptyMap();
				//conn.commit();
				
			 
				//
				sb.append(";\nSELECT runConfigID , execConfigID, problemInstance, instanceSpecificInformation, seed, cutoffTime, paramConfiguration, cutoffLessThanMax, killJob,runPartition FROM ").append(TABLE_RUNCONFIG);
				sb.append(" WHERE status=\"ASSIGNED\" AND workerUUID=\"" + workerUUID.toString() + "\" ORDER BY priority DESC;");
				
		
				//stmt.close();
				
				String releaseLockSQL = "\nSELECT RELEASE_LOCK(\"" + this.DATABASE + "." + TABLE_RUNCONFIG + ".readLock_" + lock + "\");";
				if(lockToGet > 0)
				{
						sb.append(releaseLockSQL);
				}

				
				//stmt.addBatch(sb.toString());
				//stmt = conn.prepareStatement(sb.toString());
				log.debug("SQL Query for Job Processing:\n {}", sb);
				try {
					//this.executeUpdate(stmt,sb.toString());
					this.executeStatement(stmt, sb.toString());
					//stmt.execute(sb.toString());
				} catch(RuntimeException e)
				{
					
					try 
					{
						stmt.execute(releaseLockSQL);
					} catch(SQLException e2)
					{
						log.error("Couldn't release lock: ", e2);
						conn.close();
					}
					
					throw e;
				}
				
				ResultSet rs = stmt.getResultSet();
				
				//System.out.println(Arrays.toString(res));
				
				
				if(lockToGet > 0)
				{
					stmt.getMoreResults();
				}
								
				if(stmt.getMoreResults())
				{
					
					rs = stmt.getResultSet();
				} 
			
				List<AlgorithmRunConfiguration> rcList = new ArrayList<AlgorithmRunConfiguration>();
			
				while(rs.next())
				{
					AlgorithmExecutionConfiguration execConfig = null;
					AlgorithmRunConfiguration rc = null;
					int execConfigID = -1;
					String rcID = "?";
					boolean killJob = false;
					try {
					
						rcID = rs.getString(1);
						
							try {
								
								execConfigID =  rs.getInt(2);
								execConfig = getAlgorithmExecutionConfiguration(execConfigID);
								
								
							} catch (AlgorithmExecutionConfigurationIDBlacklistedException e) {
								log.debug("Execution ID has been blacklisted skipping run {} ", rcID);
								continue;
							}
							
						log.debug("Assigned Run {} ", rcID);
						
						String problemInstance = rs.getString(3);
						String instanceSpecificInformation = rs.getString(4);
						long seed = rs.getLong(5);
						double cutoffTime = rs.getDouble(6);
						String paramConfiguration = rs.getString(7);
						boolean cutoffLessThanMax = rs.getBoolean(8);
						killJob = rs.getBoolean(9);
						
						
						int instanceId =0;
						try
						{
							 instanceId = Integer.valueOf(rcID);
						} catch(NumberFormatException e)
						{
							log.debug("Should have been able to cast to integer", e);
						}
						
						
						ProblemInstance pi = new ProblemInstance(problemInstance,instanceId, Collections.<String, Double> emptyMap(), instanceSpecificInformation );
						ProblemInstanceSeedPair pisp = new ProblemInstanceSeedPair(pi,seed);
						ParameterConfiguration config = execConfig.getParameterConfigurationSpace().getParameterConfigurationFromString(paramConfiguration, ParameterStringFormat.ARRAY_STRING_SYNTAX);
						
						rc = new AlgorithmRunConfiguration(pisp, cutoffTime, config, execConfig);
						if(killJob)
						{
							log.warn("Run {} was killed when we pulled it, this version of the workers will start processing this job then abort, if this keeps happening we may want to improve the logic on the worker. This log message just gives us an idea of how often this is happening" , rc);
						}
						
						runConfigIDMap.put(rc, rcID);
						runConfigurationToRunParitionMap.put(rc,rs.getInt(10));
					} catch(RuntimeException e)
					{
						log.error("Exception occured while trying to process run " + rcID + " with execConfigID: "+ execConfigID , e);
						
						AlgorithmExecutionConfigurationIDBlacklistedException e2 = new AlgorithmExecutionConfigurationIDBlacklistedException(execConfigID, e); 
						blacklistedKeys.put(execConfigID, e2);
						
						continue;
					}
					

					rcList.add( rc);					
				}
				rs.close();
				conn.close();
				
				/*
				if(rcList.size() > runs)
				{
					log.warn("We somehow got more runs than we asked for");
				}*/
					
				
				return rcList;
			}
			
		} catch (SQLException e) {
			throw new IllegalStateException("SQL Error", e);
		}
	}
	
}
