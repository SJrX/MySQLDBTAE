package ca.ubc.cs.beta.mysqldbtae.persistence.migration;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	public MySQLPersistenceMigration(String host, int port,
			String databaseName, String username, String password, String pool) {
		super(host, port, databaseName, username, password, pool, false);
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
		fixWorkersTable();
		
		fixAlgorithmExecutionConfigurationTable();
		
		prepareAlgorithmRunsTable();
		
	}
	
	
	private void prepareAlgorithmRunsTable() throws SQLException
	{
		try(Connection conn = getConnection())
		{
			/***
			 * We are overloading the ABORT flag to tell us whether the run was completed or not.
			 */
			StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET status=\"PAUSED\", runResult=\"ABORT\" WHERE status IN (\"NEW\",\"ASSIGNED\")");
			
			
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
	public void fixAlgorithmRunsTable(int batchSize) throws SQLException {
	
		
		
		
		
		
		//int totalRows = 0;
		
		
		log.info("Beginning repair of hashes in {}", TABLE_RUNCONFIG);
		
		List<AlgorithmRunConfiguration> results;
		//int totalRunsDone = 0;
		
		StopWatch watch = new AutoStartStopWatch();
		
		long lastLogTime = 0;
		
		do
		{
			Map<AlgorithmRunConfiguration, String> runConfigIDMap = new HashMap<>();
			Map<AlgorithmRunConfiguration, Integer> runConfigToRunPartitionMap = new HashMap<>();
			MySQLDBTAEVersionInfo mysqlVersionInfo = new MySQLDBTAEVersionInfo();
			results = super.getRuns(batchSize, 172800, runConfigIDMap, 1, "MigrationUtility-" + mysqlVersionInfo.getVersion().substring(mysqlVersionInfo.getVersion().length()-14), 10, 172800, "PAUSED",runConfigToRunPartitionMap);
		
			
			StringBuilder sb = new StringBuilder();
			for(Entry<AlgorithmRunConfiguration, String> ent : runConfigIDMap.entrySet())
			{
				AlgorithmRunConfiguration rc = ent.getKey();
				String runConfigID = ent.getValue();
				sb.append("UPDATE ").append(TABLE_RUNCONFIG).append(" SET runConfigUUID=\"").append(MySQLPersistenceClient.getHash(rc, runConfigToRunPartitionMap.get(rc), new PathStripper())).append("\", status=IF(runResult=\"ABORT\",\"NEW\",\"COMPLETE\") WHERE runConfigID=\"" + runConfigID + "\";\n");
			}
			
			
			
			//log.info("Query to execute {}", sb);
			
			if(results.size() > 0)
			{
				try(Connection conn = getConnection())
				{
					
					conn.createStatement().executeUpdate(sb.toString());
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
				
				
				log.info("Successfully repaired {} runs so far {} left {} total, estimated completion in {} seconds", totalRunsDone, totalRows-totalRunsDone,totalRows, (totalRows - totalRunsDone) /(totalRunsDone/((watch.time() / 1000.0))) );
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
