package ca.ubc.cs.beta.mysqldbtae.persistence.worker;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
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
import ca.ubc.cs.beta.aclib.algorithmrun.kill.KillableAlgorithmRun;
import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration;
import ca.ubc.cs.beta.aclib.configspace.ParamConfigurationSpace;
import ca.ubc.cs.beta.aclib.configspace.ParamFileHelper;
import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration.StringFormat;
import ca.ubc.cs.beta.aclib.exceptions.DeveloperMadeABooBooException;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.options.MySQLOptions;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.dzq.exec.DangerZoneQueue;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.exceptions.AlgorithmExecutionConfigIDBlacklistedException;
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
	
	
	public MySQLPersistenceWorker(MySQLOptions mysqlOptions, String pool, String jobID, Date endDateTime, int runsToBatch, int delayBetweenRequest, int poolIdleTimeLimit, String version, boolean createTables)
	{
		this(mysqlOptions.host, mysqlOptions.port,mysqlOptions.databaseName,mysqlOptions.username,mysqlOptions.password,pool, jobID, endDateTime, runsToBatch, delayBetweenRequest, poolIdleTimeLimit, version, createTables);
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
			String databaseName, String username, String password, String pool,String jobID, Date endDateTime, int runsToBatch, int delayBetweenRequest, int poolIdleTimeLimit, String version, boolean createTables) {
		super(host, port, databaseName, username, password, pool, createTables);

		log.info("My Worker ID is " + workerUUID.toString());
		this.jobID = jobID;
		this.endDateTime = endDateTime;
		
		logWorker(runsToBatch, delayBetweenRequest, pool, poolIdleTimeLimit, version);
	}

	private final long BASIC_SLEEP_MS = 50;
	private final long MAX_SLEEP_MS = 15000;
	
	private final Map<Integer, AlgorithmExecutionConfigIDBlacklistedException> blacklistedKeys = new HashMap<Integer, AlgorithmExecutionConfigIDBlacklistedException>();
	
	private AlgorithmExecutionConfig getAlgorithmExecutionConfig(int execConfigID) throws SQLException, AlgorithmExecutionConfigIDBlacklistedException {
		
	
		AlgorithmExecutionConfigIDBlacklistedException e = blacklistedKeys.get(execConfigID);
		
		if(e != null)
		{
			throw e;
		}
		try {
			if(!execConfigMap.containsKey(execConfigID)) 
			{
			 StringBuilder sb = new StringBuilder();
			 sb.append("SELECT algorithmExecutable, algorithmExecutableDIrectory, parameterFile, executeOnCluster, deterministicAlgorithm, cutoffTime FROM ").append(TABLE_EXECCONFIG).append("  WHERE algorithmExecutionConfigID = " + execConfigID);
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
				 
				 String algorithmExecutable = rs.getString(1);
				 String algorithmExecutionDirectory = rs.getString(2);
				 
				 ParamConfigurationSpace paramFile = ParamFileHelper.getParamFileParser(rs.getString(3));
				 
				 boolean executeOnCluster = rs.getBoolean(4);
				 boolean deterministicAlgorithm = rs.getBoolean(5);
				 double cutoffTime = rs.getDouble(6);
				 
 				 if(algorithmExecutable.startsWith(AlgorithmExecutionConfig.MAGIC_VALUE_ALGORITHM_EXECUTABLE_PREFIX))
				 {
					algorithmExecutable = DangerZoneQueue.getExecutionString("BUILTIN").replace(AlgorithmExecutionConfig.MAGIC_VALUE_ALGORITHM_EXECUTABLE_PREFIX,"");
				 }
				 
				 
				 
				 AlgorithmExecutionConfig execConfig = new AlgorithmExecutionConfig(algorithmExecutable, algorithmExecutionDirectory, paramFile,  executeOnCluster, deterministicAlgorithm, cutoffTime);
				 
				 
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
			
			AlgorithmExecutionConfigIDBlacklistedException e2 = new AlgorithmExecutionConfigIDBlacklistedException(execConfigID, rt); 
			blacklistedKeys.put(execConfigID, e2);
			throw e2;
		}
		
		
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
			
			
			sb.append("UPDATE ").append(TABLE_RUNCONFIG).append( " A JOIN (\n\t").append(
					"SELECT runConfigID, priority FROM (");
					int i=0;
					for(JobPriority job : JobPriority.values())
					{
						sb.append("\n\t\t(SELECT runConfigID,").append(i).append(" AS priority FROM ").append(TABLE_RUNCONFIG).append(" WHERE status=\"NEW\" AND priority=\"").append(job).append("\" ORDER BY runConfigID LIMIT " + n +  ")\n\t\t").append("UNION");
						i++;
					}
			
					sb.replace(sb.length()-"UNION".length(), sb.length(), "\n");
					
							
					sb.append("\t) innerTable ORDER BY priority DESC LIMIT " + n + "\n").append(
					" ) B ON B.runConfigID=A.runConfigID SET status=\"ASSIGNED\", workerUUID=\"" + workerUUID.toString() + "\", retryAttempts = retryAttempts+1;");
					
					
			System.out.println(sb.toString());
			
			PreparedStatement stmt = null;
			try {
				Connection conn = getConnection();
				stmt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
				
				execute(stmt);
				
				//if(true) return Collections.emptyMap();
				//conn.commit();
				
				sb = new StringBuffer();
				//
				sb.append("SELECT runConfigID , execConfigID, problemInstance, instanceSpecificInformation, seed, cutoffTime, paramConfiguration, cutoffLessThanMax, killJob FROM ").append(TABLE_RUNCONFIG);
				sb.append(" WHERE status=\"ASSIGNED\" AND workerUUID=\"" + workerUUID.toString() + "\"");
				
		
				stmt.close();
				
				stmt = conn.prepareStatement(sb.toString());
				
				
				ResultSet rs = stmt.executeQuery();
				
				Map<AlgorithmExecutionConfig, List<RunConfig>> myMap = new LinkedHashMap<AlgorithmExecutionConfig, List<RunConfig>>();
				
			
				while(rs.next())
				{
					AlgorithmExecutionConfig execConfig = null;
					RunConfig rc = null;
					int execConfigID = -1;
					String rcID = "?";
					try {
					
						rcID = rs.getString(1);
						
							try {
								
								execConfigID =  rs.getInt(2);
								execConfig = getAlgorithmExecutionConfig(execConfigID);
								
								
							} catch (AlgorithmExecutionConfigIDBlacklistedException e) {
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
						boolean killJob = rs.getBoolean(9);
						if(killJob)
						{
							cutoffTime = 0;
						}
						
						ProblemInstance pi = new ProblemInstance(problemInstance, instanceSpecificInformation);
						ProblemInstanceSeedPair pisp = new ProblemInstanceSeedPair(pi,seed);
						ParamConfiguration config = execConfig.getParamFile().getConfigurationFromString(paramConfiguration, StringFormat.ARRAY_STRING_SYNTAX);
						
						rc = new RunConfig(pisp, cutoffTime, config, cutoffLessThanMax);
						runConfigIDMap.put(rc, rcID);
					} catch(RuntimeException e)
					{
						log.error("Exception occured while trying to process run " + rcID + " with execConfigID: "+ execConfigID , e);
						
						AlgorithmExecutionConfigIDBlacklistedException e2 = new AlgorithmExecutionConfigIDBlacklistedException(execConfigID, e); 
						blacklistedKeys.put(execConfigID, e2);
						
						continue;
					}
							
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
	
		
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET runResult=?, runLength=?, quality=?, resultSeed=?, runtime=?, additionalRunData=?, status='COMPLETE'  WHERE runConfigID=?");
		
		try {
			
			PreparedStatement stmt = null;
			Connection conn = null;
			try {
			conn = getConnection();
			stmt = conn.prepareStatement(sb.toString());
			
				for(AlgorithmRun run : runResult)
				{	
					String runConfigID = runConfigIDMap.get(run.getRunConfig());
						
					try {
						
						stmt.setString(1,run.getRunResult().name());
						stmt.setDouble(2, run.getRunLength());
						stmt.setDouble(3, run.getQuality());
						stmt.setLong(4,run.getResultSeed());
						stmt.setDouble(5, run.getRuntime());
						stmt.setString(6, run.getAdditionalRunData());
						
						stmt.setString(7,runConfigID);
						
						execute(stmt);
					} catch(SQLException e)
					{
						log.error("SQL Exception while saving run {}", run);
						log.error("Error occured", e);
						log.error("Saving ABORT Manually");
						setAbortRun(runConfigID);
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
	
	private void execute(PreparedStatement stmt) throws SQLException
	{
		long sleepMS = BASIC_SLEEP_MS;
		for(int i=0; i < 25; i++)
		{
			try {
				stmt.execute();
				return;
			} catch(MySQLTransactionRollbackException e)
			{
				//Deadlock detected
				
				try {
					Thread.sleep(sleepMS + (long) ((Math.random()*sleepMS)/2));
				} catch (InterruptedException e1) {
					Thread.currentThread().interrupt();
					throw new IllegalStateException("Unknown interruption occurred", e1);
				}
				sleepMS = Math.min(2*sleepMS, MAX_SLEEP_MS);
				continue;
			}
		}
		throw new SQLException("After 25 attempts we could not get a successful Transaction");
	}
	private void setAbortRun(String runConfigUUID)
	{
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET runResult='ABORT', status='COMPLETE'  WHERE runConfigID=?");
		
		try {
			
			PreparedStatement stmt = null;
			try {
				Connection conn = getConnection();
				stmt = conn.prepareStatement(sb.toString());
				stmt.setString(1, runConfigUUID);
				execute(stmt);
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
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET status=\"NEW\", priority=\""+JobPriority.LOW+ "\" WHERE status=\"ASSIGNED\"  AND workerUUID=\""+ workerUUID.toString() +"\"");
		
		try {
			Connection conn = getConnection();
			
			try {
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
				if(stmt.executeUpdate()>0)
					log.error("Some runs were not completed and are being reset");

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

	private void logWorker(int runsToBatch, int delayBetweenRequests, String pool, int poolIdleTimeLimit, String version)
	{
		
		StringBuilder sb = new StringBuilder("INSERT ").append(TABLE_WORKERS).append(" (workerUUID, hostname, username, jobID, startTime, startWeekYear, originalEndTime, endTime_UPDATEABLE, runsToBatch_UPDATEABLE, delayBetweenRequests_UPDATEABLE, pool_UPDATEABLE, poolIdleTimeLimit_UPDATEABLE, workerIdleTime_UPDATEABLE, upToDate, version)  VALUES (?,?,?,?,NOW(),?,?,?,?,?,?,?,0,1,?)");
		
		
		try {
			Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
			

			String hostname = null;
			String username = null;
			
			try {
				  InetAddress addr = InetAddress.getLocalHost();
				  
				  hostname = addr.getHostName();
				  
			  } catch (UnknownHostException e) {
				  //This shouldn't happen cause we are asking for local host.
				  throw new DeveloperMadeABooBooException(e);
			  }
			
			username = System.getProperty("user.name");
		
			stmt.setString(1, workerUUID.toString());
			stmt.setString(2, hostname);
			stmt.setString(3, username);
			stmt.setString(4, jobID +"/" + ManagementFactory.getRuntimeMXBean().getName());
			
			stmt.setInt(5, Integer.parseInt(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)+""+Calendar.getInstance().get(Calendar.YEAR)));
						
			stmt.setTimestamp(6, new java.sql.Timestamp(endDateTime.getTime()));
			stmt.setTimestamp(7, new java.sql.Timestamp(endDateTime.getTime()));
			
			stmt.setInt(8,runsToBatch);
			stmt.setInt(9, delayBetweenRequests);
			stmt.setString(10, pool);
			stmt.setLong(11, poolIdleTimeLimit);
			stmt.setString(12,version);
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
		StringBuilder sb = new StringBuilder("SELECT startTime, endTime_UPDATEABLE, runsToBatch_UPDATEABLE, delayBetweenRequests_UPDATEABLE,pool_UPDATEABLE,poolIdleTimeLimit_UPDATEABLE FROM ").append(TABLE_WORKERS).append(" WHERE status='RUNNING' AND upToDate=0 AND workerUUID=\""+workerUUID.toString()+"\" ");
		
		
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
				

				log.debug("Flushing blacklist which previously had {} entries", this.blacklistedKeys.size());
				this.blacklistedKeys.clear();
				
				long timeLimit = rs.getTimestamp(2).getTime()-rs.getTimestamp(1).getTime();
				UpdatedWorkerParameters newParameters = new UpdatedWorkerParameters(timeLimit, rs.getInt(3), rs.getInt(4), rs.getString(5), rs.getInt(6));
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
	
	
	public int sumIdleTimes() {
		Calendar cal = Calendar.getInstance();
		int current = Integer.parseInt(cal.get(Calendar.WEEK_OF_YEAR)+""+cal.get(Calendar.YEAR));
		
		cal.add(Calendar.WEEK_OF_YEAR, -1);
		int previous1 = Integer.parseInt(cal.get(Calendar.WEEK_OF_YEAR)+""+cal.get(Calendar.YEAR));
		
		cal.add(Calendar.WEEK_OF_YEAR, -1);
		int previous2 = Integer.parseInt(cal.get(Calendar.WEEK_OF_YEAR)+""+cal.get(Calendar.YEAR));
		
		StringBuilder sb = new StringBuilder("SELECT SUM(workerIdleTime_UPDATEABLE) FROM ").append(TABLE_WORKERS).append(" WHERE startWeekYear IN ("+current+", "+previous1+", "+previous2+")");
	
		try {
			Connection conn = null;
			try {
				conn = getConnection();
			
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
		
				ResultSet rs = stmt.executeQuery();
				
				if(!rs.next())
				{
					stmt.close();
					return -1;
				}				
				
				return rs.getInt(1);
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
	
	
	public void updateIdleTime(int idleTime) {
		
		try {
			Connection conn = null;
			try {
				conn = getConnection();
			
				StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_WORKERS).append(" SET workerIdleTime_UPDATEABLE=\""+idleTime+"\" WHERE workerUUID=\""+workerUUID.toString()+"\" ");
				
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
		
				stmt.executeUpdate();
				
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


	/**
	 * Updates the run in the database
	 * @param killableAlgorithmRun
	 * @return <code>true</code> if the job has been killed or otherwise is no longer updatedable by us
	 */
	public boolean updateRunStatusAndCheckKillBit(KillableAlgorithmRun run) {
		
		//This query is designed to update the database IF and only IF
		//The run hasn't been killed. If we get 0 runs back, then we know the run has been killed
		//This saves us another trip to the database
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET  runtime=?, runLength=? WHERE runConfigID=? AND workerUUID=\""+ workerUUID.toString() +"\" AND status=\"ASSIGNED\" AND killJob=0" );
		
		
		try {
			Connection conn = getConnection();
			
			try {
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
				stmt.setDouble(1, run.getRuntime());
				stmt.setDouble(2, run.getRunLength());
				stmt.setString(3, this.runConfigIDMap.get(run.getRunConfig()));
				boolean shouldKill = (stmt.executeUpdate() == 0);

				stmt.close();
				return shouldKill;
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
	
	/**
	 * Sleeps via the database the amount of time required
	 * @param sleeptime
	 */
	public int sleep(double sleeptime)
	{
		
		StringBuilder sb = new StringBuilder("SELECT SLEEP(").append(sleeptime).append("); /* " + SLEEP_COMMENT_TEXT + " */" );
				
				
				try {
					Connection conn = getConnection();
					
					try {
						PreparedStatement stmt = conn.prepareStatement(sb.toString());
						
						ResultSet rs = stmt.executeQuery();
						
						if(!rs.next())
						{
							stmt.close();
							return 0;
						}
						
						return rs.getInt(1);
						
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
}
