package ca.ubc.cs.beta.mysqldbtae.persistence.worker;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException;

import ca.ubc.cs.beta.aclib.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration;
import ca.ubc.cs.beta.aclib.configspace.ParamConfigurationSpace;
import ca.ubc.cs.beta.aclib.configspace.ParamFileHelper;
import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration.StringFormat;
import ca.ubc.cs.beta.aclib.exceptions.DeveloperMadeABooBooException;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.misc.options.MySQLConfig;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;

public class MySQLPersistenceWorker extends MySQLPersistence {

	/**
	 * Stores primary keys of execution configurations
	 */
	private final Map<Integer, AlgorithmExecutionConfig> execConfigMap = new HashMap<Integer, AlgorithmExecutionConfig>();
	
	
	/**
	 * Stores a mapping of RunConfigs to the actual runConfigIDs in the database.
	 */
	private final Map<RunConfig, String> runConfigIDMap = new HashMap<RunConfig, String>();
	
	/**
	 * UUID of Worker
	 */
	protected final UUID workerUUID = UUID.randomUUID();
	
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	public MySQLPersistenceWorker(MySQLConfig mysqlOptions, String pool, String jobID, Date endDateTime, int runsToBatch, int delayBetweenRequest, String version, boolean createTables)
	{
		this(mysqlOptions.host, mysqlOptions.port,mysqlOptions.databaseName,mysqlOptions.username,mysqlOptions.password,pool, jobID, endDateTime, runsToBatch, delayBetweenRequest, version, createTables);
	}
	

	/**
	 * Job Indentifier, meaningless except for logging purposes
	 */
	private final String jobID;
	
	/**
	 * Expected end of worker, meaningless except for diagnostics
	 */
	private final Date endDateTime;
	
	public MySQLPersistenceWorker(String host, int port,
			String databaseName, String username, String password, String pool,String jobID, Date endDateTime, int runsToBatch, int delayBetweenRequest, String version, boolean createTables) {
		super(host, port, databaseName, username, password, pool, createTables);

		log.info("My Worker ID is " + workerUUID.toString());
		this.jobID = jobID;
		this.endDateTime = endDateTime;
		
		logWorker(runsToBatch, delayBetweenRequest, pool, version);
	}

	
	
	private AlgorithmExecutionConfig getAlgorithmExecutionConfig(int execConfigID) throws SQLException {
		
		if(!execConfigMap.containsKey(execConfigID)) 
		{
		 StringBuilder sb = new StringBuilder();
		 sb.append("SELECT algorithmExecutable, algorithmExecutableDIrectory, parameterFile, executeOnCluster, deterministicAlgorithm, cutoffTime FROM ").append(TABLE_EXECCONFIG).append("  WHERE algorithmExecutionConfigID = " + execConfigID);
		 Connection conn = getConnection();
		 PreparedStatement stmt = conn.prepareStatement(sb.toString());
		 ResultSet rs = stmt.executeQuery();
		 
		 rs.next();
		 
		 String algorithmExecutable = rs.getString(1);
		 String algorithmExecutionDirectory = rs.getString(2);
		 
		 ParamConfigurationSpace paramFile = ParamFileHelper.getParamFileParser(rs.getString(3), 1);
		 
		 boolean executeOnCluster = rs.getBoolean(4);
		 boolean deterministicAlgorithm = rs.getBoolean(5);
		 double cutoffTime = rs.getDouble(6);
		 
			
		 AlgorithmExecutionConfig execConfig = new AlgorithmExecutionConfig(algorithmExecutable, algorithmExecutionDirectory, paramFile,  executeOnCluster, deterministicAlgorithm, cutoffTime);
		 
		 
		 execConfigMap.put(execConfigID, execConfig);
			
		 conn.close();
		 rs.close();
		 stmt.close();
		}
		
		
		return execConfigMap.get(execConfigID);
		
		
		
	}
	

	/**
	 * Returns up to n runs for this worker
	 * @param n number of runs to attempt to get
	 * @return runs
	 */
	public Map<AlgorithmExecutionConfig, List<RunConfig>> getRuns(int n)
	{
	
		StringBuffer sb = new StringBuffer();
		
		
		try {
			
			
		
			//Pull workers off the queue in order of priority, do not take workers where we already tried
			//This isn't a perfect heuristic. I'm hoping it's good enough.
			sb.append("UPDATE ").append(TABLE_RUNCONFIG).append( " A JOIN ( SELECT runConfigUUID FROM ").append(TABLE_RUNCONFIG).append(" WHERE status=\"NEW\" AND (workerUUID IS NULL OR workerUUID <> \""+ workerUUID.toString() +"\") LIMIT " + n +  "  FOR UPDATE) B ON B.runConfigUUID=A.runConfigUUID SET status=\"ASSIGNED\", workerUUID=\"" + workerUUID.toString() + "\";");
					
					
			System.out.println(sb.toString());
			
			PreparedStatement stmt = null;
			try {
				Connection conn = getConnection();
				stmt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
				try {
					stmt.execute();
				} catch(MySQLTransactionRollbackException e)
				{
					log.info("Deadlock detected, trying again");
					return getRuns(n);
				}
				//if(true) return Collections.emptyMap();
				//conn.commit();
				
				sb = new StringBuffer();
				//
				sb.append("SELECT runConfigUUID , execConfigID, problemInstance, instanceSpecificInformation, seed, cutoffTime, paramConfiguration, cutoffLessThanMax FROM ").append(TABLE_RUNCONFIG);
				sb.append(" WHERE status=\"ASSIGNED\" AND workerUUID=\"" + workerUUID.toString() + "\"");
				
		
				stmt.close();
				
				stmt = conn.prepareStatement(sb.toString());
				
				
				ResultSet rs = stmt.executeQuery();
				
				Map<AlgorithmExecutionConfig, List<RunConfig>> myMap = new LinkedHashMap<AlgorithmExecutionConfig, List<RunConfig>>();
				
			
				while(rs.next())
				{
					
					String uuid = rs.getString(1);
					log.debug("Assigned Run {} ", uuid);
					//if(true) continue;
					AlgorithmExecutionConfig execConfig = getAlgorithmExecutionConfig(rs.getInt(2));
					String problemInstance = rs.getString(3);
					String instanceSpecificInformation = rs.getString(4);
					long seed = rs.getLong(5);
					double cutoffTime = rs.getDouble(6);
					String paramConfiguration = rs.getString(7);
					boolean cutoffLessThanMax = rs.getBoolean(8);
									
					
					ProblemInstance pi = new ProblemInstance(problemInstance, instanceSpecificInformation);
					ProblemInstanceSeedPair pisp = new ProblemInstanceSeedPair(pi,seed);
					ParamConfiguration config = execConfig.getParamFile().getConfigurationFromString(paramConfiguration, StringFormat.ARRAY_STRING_SYNTAX);
					
					RunConfig rc = new RunConfig(pisp, cutoffTime, config, cutoffLessThanMax);
					runConfigIDMap.put(rc, uuid);
					if(myMap.get(execConfig) == null)
					{
						myMap.put(execConfig, new ArrayList<RunConfig>(n));
					}
					
					myMap.get(execConfig).add(rc);
				}
				rs.close();
				conn.close();
				return myMap;
			} finally
			{
				if(stmt != null) stmt.close();
			}
			
			
			
			
			
			
		} catch (SQLException e) {
			throw new IllegalStateException("SQL Error", e);
		}
		
	}
	

	public void setRunResults(List<AlgorithmRun> runResult)
	{
	
		
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET runResult=?, runLength=?, quality=?, result_seed=?, result_line=?, runtime=?, additional_run_data=?, status='COMPLETE'  WHERE runConfigUUID=?");
		
		try {
			
			PreparedStatement stmt = null;
			Connection conn = null;
			try {
			conn = getConnection();
			stmt = conn.prepareStatement(sb.toString());
			
				for(AlgorithmRun run : runResult)
				{	
					String runConfigUUID = runConfigIDMap.get(run.getRunConfig());
						
					try {
						
						stmt.setString(1,run.getRunResult().name());
						stmt.setDouble(2, run.getRunLength());
						stmt.setDouble(3, run.getQuality());
						stmt.setLong(4,run.getResultSeed());
						stmt.setString(5, run.getResultLine());
						stmt.setDouble(6, run.getRuntime());
						stmt.setString(7, run.getAdditionalRunData());
						
						stmt.setString(8,runConfigUUID);
						stmt.execute();
						
					} catch(SQLException e)
					{
						log.error("SQL Exception while saving run {}", run);
						log.error("Error occured", e);
						log.error("Saving ABORT Manually");
						setAbortRun(runConfigUUID);
					}
					
				} 
				//conn.commit();
			} finally
			{
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
				conn.close();
			}
			
		}catch(SQLException e)
		{
			throw new IllegalStateException("SQL Error", e);
		}
		
	}
	
	private void setAbortRun(String runConfigUUID)
	{
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET runResult='ABORT', status='COMPLETE'  WHERE runConfigUUID=?");
		
		try {
			
			PreparedStatement stmt = null;
			try {
				Connection conn = getConnection();
				stmt = conn.prepareStatement(sb.toString());
				stmt.setString(1, runConfigUUID);
				stmt.execute();
				//conn.commit();
				conn.close();
			} finally
			{
				if(stmt != null) stmt.close();
			}
		}catch(SQLException e)
		{
			log.error("Failed writing abort to database, something very bad is happening");
			throw new IllegalStateException(e);
		}
		
			
	}
	

	public void resetUnfinishedRuns()
	{
		log.info("Resetting Unfinished Runs");
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET status=\"NEW\" WHERE status=\"ASSIGNED\"  AND workerUUID=\""+ workerUUID.toString() +"\"");
		
		try {
			Connection conn = getConnection();
			
			try {
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
				stmt.execute();

				stmt.close();
			} finally
			{
				conn.close();
			}
			
		}catch(SQLException e)
		{
			log.error("Failed writing abort to database, something very bad is happening");
			
			throw new IllegalStateException(e);
		}
		

	}

	private void logWorker(int runsToBatch, int delayBetweenRequests, String pool, String version)
	{
		
		StringBuilder sb = new StringBuilder("INSERT ").append(TABLE_WORKERS).append(" (workerUUID, hostname, jobID,endTime, startTime, runsToBatch, delayBetweenRequests, pool,upToDate, version)  VALUES (?,?,?,?,NOW(),?,?,?,1,?)");
		
		
		try {
			Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
			
	
			String hostname = null;
			
			try {
				  InetAddress addr = InetAddress.getLocalHost();
				  
				  hostname = addr.getHostName();
				  
			  } catch (UnknownHostException e) {
				  //This shouldn't happen cause we are asking for local host.
				  throw new DeveloperMadeABooBooException(e);
			  }
		
			stmt.setString(1, workerUUID.toString());
			stmt.setString(2, hostname);
			stmt.setString(3, jobID +"/" + ManagementFactory.getRuntimeMXBean().getName());
			
			
			stmt.setTimestamp(4, new java.sql.Timestamp(endDateTime.getTime()));
			
			stmt.setInt(5,runsToBatch);
			stmt.setInt(6, delayBetweenRequests);
			stmt.setString(7, pool);
			stmt.setString(8,version);
			stmt.execute();
			stmt.close();
			conn.close();
			
		} catch(SQLException e)
		{
			log.error("Failed writing worker Information to database, something very bad is happening");
			throw new IllegalStateException(e);
		}
	}


	public void markWorkerCompleted(String crashInfo) {
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_WORKERS).append(" SET status='DONE', crashInfo=? WHERE status='RUNNING' AND workerUUID=\""+workerUUID.toString()+"\" ");
		
		
		try {
			Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
	
			
			stmt.setString(1, crashInfo);
			
			stmt.execute();
			stmt.close();
			//conn.commit();
			conn.close();
		} catch(SQLException e)
		{
			log.error("Failed writing worker Information to database, something very bad is happening");
			throw new IllegalStateException(e);
		}
		
		
	}
	
	public UpdatedWorkerParameters getUpdatedParameters() {
		StringBuilder sb = new StringBuilder("SELECT runsToBatch, delayBetweenRequests FROM ").append(TABLE_WORKERS).append(" WHERE status='RUNNING' AND upToDate=0 AND workerUUID=\""+workerUUID.toString()+"\" ");
		
		
		try {
			Connection conn = null;
			try {
				conn = getConnection();
			
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
		
				ResultSet rs = stmt.executeQuery();
				
				if(!rs.next())
				{
					stmt.close();
					return null;
				}
				
				UpdatedWorkerParameters newParameters = new UpdatedWorkerParameters(rs.getInt(1), rs.getInt(2));
				stmt.close();
				
				sb = new StringBuilder("UPDATE ").append(TABLE_WORKERS).append(" SET upToDate=1 WHERE workerUUID=\""+workerUUID.toString()+"\" ");
				
				stmt = conn.prepareStatement(sb.toString());
				stmt.execute();
				stmt.close();
				//conn.commit();
				return newParameters;
			} finally
			{
				if(conn != null) conn.close();
				
			}
			
		} catch(SQLException e)
		{
			log.error("Failed writing worker Information to database, something very bad is happening");
			throw new IllegalStateException(e);
		}
		
		
		
	}
	
}
