package ca.ubc.cs.beta.mysqldbtae.persistence.migration;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



















import ca.ubc.cs.beta.aeatk.algorithmexecutionconfiguration.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunconfiguration.AlgorithmRunConfiguration;
import ca.ubc.cs.beta.aeatk.json.JSONHelper;
import ca.ubc.cs.beta.aeatk.misc.watch.AutoStartStopWatch;
import ca.ubc.cs.beta.aeatk.misc.watch.StopWatch;
import ca.ubc.cs.beta.mysqldbtae.exceptions.AlgorithmExecutionConfigurationIDBlacklistedException;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;
import ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient;
import ca.ubc.cs.beta.mysqldbtae.util.ACLibHasher;
import ca.ubc.cs.beta.mysqldbtae.util.PathStripper;
import ca.ubc.cs.beta.mysqldbtae.version.MySQLDBTAEVersionInfo;

public class MySQLPersistenceMigration extends MySQLPersistence{

	private Logger log = LoggerFactory.getLogger(getClass());
	private final boolean resetAllRunHashes;
	public MySQLPersistenceMigration(String host, int port,
			String databaseName, String username, String password, String pool, boolean resetAllHashes) {
		super(host, port, databaseName, username, password, pool, false);
		this.resetAllRunHashes = resetAllHashes;
	}

	
	/**
	 * Performs the migration of the database
	 */
	public void migrate()
	{
		
		
		try {
			
		
		preMigrate();
		
		
		fixAlgorithmRunsTable(250);
		
		} catch(SQLException e)
		{
			log.error("Unfortunately an error occured during migration utility", e);
			
		}
		
		
		
	}
	
	
	
	public void preMigrate() throws SQLException
	{
		fixTableSchemaIfNecessary();
		
		
		
		
		fixWorkersTable();
		
		fixAlgorithmExecutionConfigurationTable();
		
		prepareAlgorithmRunsTable();
		
	}
	
	
	/**
	 * Checks if the version information in the table matches our version, if not runs the migration script and replaces it.
	 * @throws SQLException 
	 */
	public void fixTableSchemaIfNecessary() throws SQLException {
		
		//TODO Check existing version
		
		BufferedReader br = new BufferedReader(new InputStreamReader(MySQLPersistence.class.getClassLoader().getResourceAsStream("tables.sql")));
		
		StringBuilder sb = new StringBuilder();
		
		String line;
		boolean nothingFound = true;
    	try {
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
				nothingFound = false;
			}
		} catch (IOException e) {
			throw new IllegalStateException("Couldn't read line from tables.sql file",e);
		} 
    	
    	if(nothingFound) throw new IllegalStateException("Couldn't load tables.sql");
		String sql = sb.toString();
		
		
		String versionHash = getHash(sql);
		
		

		try(Connection conn = getConnection())
		{
			ResultSet rs = conn.createStatement().executeQuery("SELECT version, hash FROM " + TABLE_VERSION + " ORDER BY id LIMIT 1");
			
			rs.next();
			
			String oVersion = rs.getString(1);
			String oHash = rs.getString(2);
			 
			log.info("Pool is presently version {} created by version {} and the most recent hash is {} ",oHash, oVersion, versionHash);
		
			if(oHash.equals(versionHash))
			{
				log.info("Version looks correct, will not attempt to alter");
				return;
			}
		}
		
		
		
		
		br = new BufferedReader(new InputStreamReader(MySQLPersistence.class.getClassLoader().getResourceAsStream("migration.sql")));
		
		sb = new StringBuilder();
		
		List<String> queries = new ArrayList<String>();
		
		nothingFound = true;
    	try {
			while ((line = br.readLine()) != null) {
				
				if(line.trim().startsWith("--"))
				{
					continue;
				}
				
				if(line.trim().startsWith("/*"))
				{
					continue;
				}
				
				
				sb.append(line).append("\n");
				
				
				if(line.contains(";"))
				{
					queries.add(sb.toString().replace("ACLIB_POOL_NAME", this.POOL).trim());
					
					sb = new StringBuilder();
				}
				
				nothingFound = false;
			}
		} catch (IOException e) {
			throw new IllegalStateException("Couldn't read line from tables.sql file",e);
		} 
    	
    	if(nothingFound) throw new IllegalStateException("Couldn't load migration.sql");
		//String sql = sb.toString();
		
		
    	log.info("Trying to repair table structure");
    	for(String query : queries)
    	{
    		try(Connection conn = getConnection())
    		{
    		
    			try {
    				conn.createStatement().execute(query);
    				log.info("Query completed successfully");
    			} catch(SQLException e)
    			{
    				log.info("Query did not execute successfully, this may be harmless and suggests that you are upgrading from an intermediate version, here is the query that failed and the reason:\n==== QUERY ====\n{}\n===== REASON =====\n{}\n{}",query, e.getClass().getCanonicalName() + ":" + e.getMessage(), Arrays.toString(e.getStackTrace()));
    			}
    		}
    		
    	}
    		
    	
    	try(Connection conn = getConnection())
		{
    		
    		log.info("Repairing table version information");
    		//Fixes the version in the table
    		PreparedStatement stmt = conn.prepareStatement("REPLACE " + TABLE_VERSION + " (version, hash) VALUES (?,?) ",Statement.RETURN_GENERATED_KEYS );
			
			
			MySQLDBTAEVersionInfo myinfo = new MySQLDBTAEVersionInfo();
			stmt.setString(1, myinfo.getVersion());
			stmt.setString(2, versionHash);
			
			
			stmt.executeUpdate();
			
			ResultSet rs =stmt.getGeneratedKeys();
			
			
			rs.next();
			
			rs.getInt(1);
			
			conn.createStatement().execute("DELETE FROM " + TABLE_VERSION + " WHERE id <> " + rs.getInt(1)); 
		}
		
    	
    	
		
		
	}


	private void prepareAlgorithmRunsTable() throws SQLException
	{
		if(this.resetAllRunHashes)
		{
			try(Connection conn = getConnection())
			{
				/***
				 * We are overloading the ABORT flag to tell us whether the run was completed or not.
				 */
				StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET status=\"PAUSED\", result_status=\"ABORT\" WHERE status IN (\"NEW\",\"ASSIGNED\")");
				
				
				log.info(sb.toString());
				int updatedRows = conn.createStatement().executeUpdate(sb.toString());
				
				log.info("Moved {} runs that were NEW or ASSIGNED to PAUSED state", updatedRows);
			}
			
			try(Connection conn = getConnection())
			{
				
				
				StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET status=\"PAUSED\" WHERE status IN (\"COMPLETE\")");
				
				
				int updatedRows = conn.createStatement().executeUpdate(sb.toString());
				
				log.info("Moved {} runs that were COMPLETED back to PAUSED state", updatedRows);
			}
		}
		
	}
	public void fixAlgorithmRunsTable(int batchSize) throws SQLException {
	
		
		
		
		
		
		//int totalRows = 0;
		
		
		log.info("Beginning repair of hashes in {}", TABLE_RUNCONFIG);
		
		List<AlgorithmRunConfiguration> results;
		//int totalRunsDone = 0;
		
	
		
		long lastLogTime = 0;
		
		do
		{
			StopWatch watch = new AutoStartStopWatch();
			Map<AlgorithmRunConfiguration, String> runConfigIDMap = new HashMap<>();
			Map<AlgorithmRunConfiguration, Integer> runConfigToRunPartitionMap = new HashMap<>();
			MySQLDBTAEVersionInfo mysqlVersionInfo = new MySQLDBTAEVersionInfo();
			results = super.getRuns(batchSize, 172800, runConfigIDMap, 1, "MigrationUtility-" + mysqlVersionInfo.getVersion().substring(mysqlVersionInfo.getVersion().length()-14), 10, 172800, "PAUSED",runConfigToRunPartitionMap);
		
			
			StringBuilder sb = new StringBuilder();
			for(Entry<AlgorithmRunConfiguration, String> ent : runConfigIDMap.entrySet())
			{
				AlgorithmRunConfiguration rc = ent.getKey();
				String runID = ent.getValue();
				sb.append("UPDATE ").append(TABLE_RUNCONFIG).append(" SET runHashCode=\"").append(MySQLPersistenceClient.getHash(rc, runConfigToRunPartitionMap.get(rc), new PathStripper())).append("\", status=IF(result_status=\"ABORT\",\"NEW\",\"COMPLETE\") WHERE runID=\"" + runID + "\";\n");
			}
			
			
			
			//log.info("Query to execute {}", sb);
			
			if(results.size() > 0)
			{
				try(Connection conn = getConnection())
				{
					
					do
					{
						try 
						{
							conn.createStatement().executeUpdate(sb.toString());
						} catch(com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException e)
						{
							continue;
						}
					} while(false);
				} catch(SQLException e)
				{
				
					
					System.err.println(sb.toString());
					throw e;
					
				}
			}
			
			//Want to only log about every 5 seconds, but don't want to deal with race conditions, so we simply grab a random number and on expectation this is about right
			
			if(System.currentTimeMillis() - lastLogTime > 5000 && (Math.random() * Runtime.getRuntime().availableProcessors() < 1.5))
			{
				
				
				int totalRows;
				int totalRunsDone;
				try(Connection conn = getConnection())
				{
					
					
					 sb = new StringBuilder("SELECT COUNT(*) FROM ").append(TABLE_RUNCONFIG);
					
					
					ResultSet rs = conn.createStatement().executeQuery(sb.toString());
					
					rs.next();
					totalRows = rs.getInt(1);
				} 
				
				
				try(Connection conn = getConnection())
				{
					
					
					 sb = new StringBuilder("SELECT COUNT(*) FROM ").append(TABLE_RUNCONFIG).append(" WHERE status=\"PAUSED\"");
					
					
					ResultSet rs = conn.createStatement().executeQuery(sb.toString());
					
					rs.next();
					totalRunsDone = totalRows - rs.getInt(1);
				} 
				
				
				log.info("Successfully repaired {} runs so far {} left {} total, estimated completion in {} seconds", totalRunsDone, totalRows-totalRunsDone,totalRows, (totalRows - totalRunsDone) /(results.size()/((watch.time() / 1000.0))) );
				lastLogTime = System.currentTimeMillis();
			}
			
		}while(results.size() > 0);
	}

	
	private void fixWorkersTable() throws SQLException
	{
		try(Connection conn = getConnection())
		{
			StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_WORKERS).append(" SET status=\"DONE\", crashInfo=\"Migration Utility Started\",upToDate=0,endTime_UPDATEABLE=NOW() WHERE status=\"RUNNING\"");
			int workersUpdated = conn.prepareStatement(sb.toString()).executeUpdate();
			
			
			
			if(workersUpdated > 0)
			{
				log.warn("Repaired table {} marked {} workers as completed, sleeping 10 seconds to wait for workers to shutdown hopefully ", TABLE_WORKERS, workersUpdated);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					throw new IllegalStateException("Interrupted while waiting for workers to shutdown");
				}
			} else
			{
				log.info("Repaired table {}, all workers previously shutdown", TABLE_WORKERS);
			}
		}
		
		
	}

	private void fixAlgorithmExecutionConfigurationTable() throws SQLException
	{
		Set<Integer> execConfigurationsToRepair = new HashSet<Integer>();
		ACLibHasher aclibHasher = new ACLibHasher();
		try 
		{
			try(Connection conn = getConnection())
			{
				StringBuilder sb = new StringBuilder("SELECT algorithmExecutionConfigID FROM ").append(TABLE_EXECCONFIG);
				ResultSet rs = conn.prepareStatement(sb.toString()).executeQuery();
				
			
				while(rs.next())
				{
					 execConfigurationsToRepair.add(rs.getInt(1));
				}
				
				
				
			}
		
			log.info("Beginning repair of {} table need to repair {} entries", TABLE_EXECCONFIG, execConfigurationsToRepair.size());
			
			try(Connection conn = getConnection())
			{
				
				StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_EXECCONFIG).append(" SET algorithmExecutionConfigHashCode=? WHERE algorithmExecutionConfigID=?");
				
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
				
				for(Integer id : execConfigurationsToRepair)
				{
					
					AlgorithmExecutionConfiguration execConfig = this.getAlgorithmExecutionConfiguration(id);
					
							
					stmt.setString(1, aclibHasher.getHash(execConfig));
					stmt.setInt(2, id);
					
					try 
					{
						stmt.executeUpdate();
					} catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ex)
					{
						log.error("The table {} is corrupted as a repair of the hash has created a duplicate row. This was probably caused by running something else at the same time. You can fix this by simply deleting the old row with ID = {} and pointing all the run entries to the row with the hash = {}" , TABLE_EXECCONFIG, id,  aclibHasher.getHash(execConfig));
						throw new IllegalStateException("Cannot continue please check previous error");
					}
					
				}
			}
			
			log.info("Repair of {} table completed", TABLE_EXECCONFIG);
			
		} catch (AlgorithmExecutionConfigurationIDBlacklistedException e) {
			
			throw new IllegalStateException("Couldn't complete migration due to corruption in the " + this.TABLE_EXECCONFIG  + " table, see exception for more details:",e);
		}
		
			
	
	}
	
	
}
