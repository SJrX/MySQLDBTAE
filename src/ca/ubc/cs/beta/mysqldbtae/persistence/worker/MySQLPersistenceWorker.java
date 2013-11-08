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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
import ca.ubc.cs.beta.aclib.misc.associatedvalue.Pair;
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
	private final Map<Integer, AlgorithmExecutionConfig> execConfigMap = new ConcurrentHashMap<Integer, AlgorithmExecutionConfig>();
	
	
	/**
	 * Stores a mapping of RunConfigs to the actual runConfigIDs in the database.
	 */
	private final Map<RunConfig, String> runConfigIDMap = new ConcurrentHashMap<RunConfig, String>();
	
	/**
	 * UUID of Worker
	 */
	protected final UUID workerUUID = UUID.randomUUID();
	
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	public MySQLPersistenceWorker(MySQLOptions mysqlOptions, String pool, String jobID, Date startDateTime, Date endDateTime, int runsToBatch, int delayBetweenRequest, int poolIdleTimeLimit, String version, boolean createTables)
	{
		this(mysqlOptions.host, mysqlOptions.port,mysqlOptions.databaseName,mysqlOptions.username,mysqlOptions.password,pool, jobID, startDateTime, endDateTime, runsToBatch, delayBetweenRequest, poolIdleTimeLimit, version, createTables);
	}
	

	/**
	 * Job Indentifier, meaningless except for logging purposes
	 */
	private final String jobID;
	
	/**
	 * Expected end of worker, meaningless except for diagnostics
	 */
	private final Date endDateTime;
	private final Date startDateTime;
	
	public MySQLPersistenceWorker(String host, int port,
			String databaseName, String username, String password, String pool,String jobID, Date startDateTime, Date endDateTime, int runsToBatch, int delayBetweenRequest, int poolIdleTimeLimit, String version, boolean createTables) {
		super(host, port, databaseName, username, password, pool, createTables);

		log.info("My Worker ID is " + workerUUID.toString());
		this.jobID = jobID;
		this.endDateTime = endDateTime;
		this.startDateTime = startDateTime;
		
		logWorker(runsToBatch, delayBetweenRequest, pool, poolIdleTimeLimit, version);
	}

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
			 sb.append("SELECT algorithmExecutable, algorithmExecutableDirectory, parameterFile, executeOnCluster, deterministicAlgorithm, cutoffTime FROM ").append(TABLE_EXECCONFIG).append("  WHERE algorithmExecutionConfigID = " + execConfigID);
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
					algorithmExecutable = algorithmExecutable.replace(AlgorithmExecutionConfig.MAGIC_VALUE_ALGORITHM_EXECUTABLE_PREFIX,"");
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
	public List<Pair<AlgorithmExecutionConfig, RunConfig>> getRuns(int n)
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
						sb.append("\n\t\t(SELECT runConfigID,").append(i).append(" AS priority FROM ").append(TABLE_RUNCONFIG).append(" WHERE status=\"NEW\" AND priority=\"").append(job).append("\" ORDER BY runConfigID LIMIT " + n +  ")\n\t\t").append("UNION ALL");
						i++;
					}
			
					sb.replace(sb.length()-"UNION ALL".length(), sb.length(), "\n");
					
							
					sb.append("\t) innerTable ORDER BY priority DESC LIMIT " + n + "\n").append(
					" ) B ON B.runConfigID=A.runConfigID SET status=\"ASSIGNED\", workerUUID=\"" + workerUUID.toString() + "\", retryAttempts = retryAttempts+1;");
					
					
			//System.out.println(sb.toString());
			
					log.debug("SQL Query for Job Processing: {}", sb);
			PreparedStatement stmt = null;
			try {
				Connection conn = getConnection();
				stmt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
				
				executePS(stmt);
				
				//if(true) return Collections.emptyMap();
				//conn.commit();
				
				sb = new StringBuffer();
				//
				sb.append("SELECT runConfigID , execConfigID, problemInstance, instanceSpecificInformation, seed, cutoffTime, paramConfiguration, cutoffLessThanMax, killJob FROM ").append(TABLE_RUNCONFIG);
				sb.append(" WHERE status=\"ASSIGNED\" AND workerUUID=\"" + workerUUID.toString() + "\"");
				
		
				stmt.close();
				
				stmt = conn.prepareStatement(sb.toString());
				
				
				ResultSet rs = stmt.executeQuery();
				
				List<Pair<AlgorithmExecutionConfig, RunConfig>> configList = new ArrayList<Pair<AlgorithmExecutionConfig, RunConfig>>();
			
				while(rs.next())
				{
					AlgorithmExecutionConfig execConfig = null;
					RunConfig rc = null;
					int execConfigID = -1;
					String rcID = "?";
					boolean killJob = false;
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
						ParamConfiguration config = execConfig.getParamFile().getConfigurationFromString(paramConfiguration, StringFormat.ARRAY_STRING_SYNTAX);
						
						rc = new RunConfig(pisp, cutoffTime, config, cutoffLessThanMax);
						if(killJob)
						{
							log.warn("Run {} was killed when we pulled it, this version of the workers will start processing this job then abort, if this keeps happening we may want to improve the logic on the worker. This log message just gives us an idea of how often this is happening" , rc);
						}
						
						runConfigIDMap.put(rc, rcID);
					} catch(RuntimeException e)
					{
						log.error("Exception occured while trying to process run " + rcID + " with execConfigID: "+ execConfigID , e);
						
						AlgorithmExecutionConfigIDBlacklistedException e2 = new AlgorithmExecutionConfigIDBlacklistedException(execConfigID, e); 
						blacklistedKeys.put(execConfigID, e2);
						
						continue;
					}
					
					configList.add(new Pair(execConfig, rc));
					
				}
				rs.close();
				conn.close();
				
					
				
				return configList;
			} finally
			{
				if(stmt != null) stmt.close();
			}
			
			
			
			
			
			
		} catch (SQLException e) {
			throw new IllegalStateException("SQL Error", e);
		}
		
	}

	/**
	 * add run results to the database
	 * @param runResult a List of AlgorithmRuns to add to the database
	 */
	public void setRunResults(List<AlgorithmRun> runResult)
	{
	
		
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET runResult=?, runLength=?, quality=?, resultSeed=?, runtime=?, additionalRunData=?, walltime=?, status='COMPLETE' WHERE workerUUID = ? AND runConfigID=?");
		
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

						stmt.setDouble(7, run.getWallclockExecutionTime());
						stmt.setString(8, workerUUID.toString());
						stmt.setString(9,runConfigID);
						
						executePS(stmt);
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
	
	/**
	 * Attempts to execute an SQL statement with increasingly spaced out retry attempts on failure
	 * @param stmt  PreparedStatment to execute
	 * @throws SQLException
	 */
	//private void execute(PreparedStatement stmt) throws SQLException
	//{
		//executePS(stmt);
		/*
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
	
	/**
	 * Take a given runConfig and set it to ABORT
	 * @param runConfigUUID  String with the UUID of the runConfig
	 */
	private void setAbortRun(String runConfigUUID)
	{
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET runResult='ABORT', status='COMPLETE'  WHERE runConfigID=?");
		
		try {
			
			PreparedStatement stmt = null;
			try {
				Connection conn = getConnection();
				stmt = conn.prepareStatement(sb.toString());
				stmt.setString(1, runConfigUUID);
				executePS(stmt);
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
	

	/**
	 * Take assigned runs that have not completed and reset them to an unassigned state as well as setting their priority to 'LOW'
	 */
	public void resetUnfinishedRuns()
	{
		log.debug("Resetting Unfinished Runs");
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET status=\"NEW\", priority=\""+JobPriority.LOW+ "\" WHERE status=\"ASSIGNED\"  AND workerUUID=\""+ workerUUID.toString() +"\"");
		
		try {
			Connection conn = getConnection();
			
			try {
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
				executePSUpdate(stmt);
				if(stmt.executeUpdate()>0)
					log.warn("Some runs were not completed and are being reset");

				stmt.close();
			} finally
			{
				conn.close();
			}
			
		}catch(SQLException e)
		{
			log.error("Failed writing unfinished runs to database, something very bad is happening");
			
			throw new IllegalStateException(e);
		}
		

	}

	/**
	 * Inserts the worker info into the database
	 * @param runsToBatch
	 * @param delayBetweenRequests
	 * @param pool
	 * @param poolIdleTimeLimit
	 * @param version
	 */
	private void logWorker(int runsToBatch, int delayBetweenRequests, String pool, int poolIdleTimeLimit, String version)
	{
		
		StringBuilder sb = new StringBuilder("INSERT ").append(TABLE_WORKERS).append(" (workerUUID, hostname, username, jobID, startTime, startWeekYear, originalEndTime, endTime_UPDATEABLE, runsToBatch_UPDATEABLE, delayBetweenRequests_UPDATEABLE, pool_UPDATEABLE, poolIdleTimeLimit_UPDATEABLE, workerIdleTime_UPDATEABLE, upToDate, version)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,0,1,?)");
		
		
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
			
			stmt.setTimestamp(5, new java.sql.Timestamp(startDateTime.getTime()));
			
			stmt.setInt(6, Integer.parseInt(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)+""+Calendar.getInstance().get(Calendar.YEAR)));
						
			stmt.setTimestamp(7, new java.sql.Timestamp(endDateTime.getTime()));
			stmt.setTimestamp(8, new java.sql.Timestamp(endDateTime.getTime()));
			
			stmt.setInt(9,runsToBatch);
			stmt.setInt(10, delayBetweenRequests);
			stmt.setString(11, pool);
			stmt.setLong(12, poolIdleTimeLimit);
			stmt.setString(13,version);
			executePS(stmt);
			stmt.close();
			conn.close();
			
		} catch(SQLException e)
		{
			log.error("Failed writing worker Information to database, something very bad is happening");
			throw new IllegalStateException(e);
		}
	}


	/**
	 * Set a worker's status to 'DONE' and record relevant crash info
	 * @param crashInfo  String to record to crashInfo in the database
	 */
	public void markWorkerCompleted(String crashInfo) {
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_WORKERS).append(" SET status='DONE', crashInfo=? WHERE status='RUNNING' AND workerUUID=\""+workerUUID.toString()+"\" ");
		
		try {
			Connection conn = getConnection();
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
	
			
			stmt.setString(1, crashInfo);
			
			executePS(stmt);
			stmt.close();
			//conn.commit();
			conn.close();
		} catch(SQLException e)
		{
			log.error("Failed writing worker Information to database, something very bad is happening");
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * If the worker parameters have been changed in the database since our last update, retrieve these new parameters
	 * @return UpdatedWorkerParameters
	 */
	public UpdatedWorkerParameters getUpdatedParameters() {
		StringBuilder sb = new StringBuilder("SELECT endTime_UPDATEABLE, runsToBatch_UPDATEABLE, delayBetweenRequests_UPDATEABLE,pool_UPDATEABLE,poolIdleTimeLimit_UPDATEABLE FROM ").append(TABLE_WORKERS).append(" WHERE status='RUNNING' AND upToDate=0 AND workerUUID=\""+workerUUID.toString()+"\" ");
		
		
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
				
				long timeLimit = rs.getTimestamp(1).getTime()-startDateTime.getTime();
				UpdatedWorkerParameters newParameters = new UpdatedWorkerParameters(timeLimit, rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getInt(5));
				stmt.close();
				
				sb = new StringBuilder("UPDATE ").append(TABLE_WORKERS).append(" SET upToDate=1 WHERE workerUUID=\""+workerUUID.toString()+"\" ");
				
				stmt = conn.prepareStatement(sb.toString());
				executePS(stmt);
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
	
	/**
	 * Sums the idle times of all workers in the pool, launched within the last 3 weeks
	 * @return int representing the summed idle times
	 */
	public int sumIdleTimes() {
		//Calendar.add method saves state.  This retrieves the current week and the previous 2 weeks
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
		
				ResultSet rs = executePSQuery(stmt);
				
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
	
	
	/**
	 * Gets the minimum cutoffTime of all 'NEW' runConfigs
	 * @return int representing the minimum cutoffTime of all 'NEW' runConfigs
	 */
	public int getMinCutoff() {
		
		StringBuilder sb = new StringBuilder("SELECT MIN(cutoffTime) FROM ").append(TABLE_RUNCONFIG).append(" WHERE status='NEW'");
				
		try {
			Connection conn = null;
			try {
				conn = getConnection();
			
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
		
				ResultSet rs = executePSQuery(stmt);
				
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
	
	/**
	 * Updates the workerIdleTime_UPDATEABLE field in the workers table
	 * @param idleTime  the value to place in the database field
	 */
	public void updateIdleTime(int idleTime) {
		
		try {
			Connection conn = null;
			try {
				conn = getConnection();
			
				StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_WORKERS).append(" SET workerIdleTime_UPDATEABLE=\""+idleTime+"\" WHERE workerUUID=\""+workerUUID.toString()+"\" ");
				
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
				executePSUpdate(stmt);
				
				
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
	 * Takes a list of runs to reset to an unassigned state
	 * @param extraRuns  List of AlgorithmExecutionConfig and RunConfig Pairs
	 */
	public void resetRunConfigs(List<Pair<AlgorithmExecutionConfig, RunConfig>> extraRuns) {
		if(extraRuns.isEmpty())
			return;
		
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET  status='NEW', workerUUID=0 WHERE status=\"ASSIGNED\"  AND workerUUID=\""+ workerUUID.toString() +"\" AND runConfigID IN (" );

		for(Pair<AlgorithmExecutionConfig, RunConfig> ent : extraRuns)
		{
			sb.append(this.runConfigIDMap.get(ent.getSecond())+",");
		}
		sb.setCharAt(sb.length()-1, ')');

		try {
			Connection conn = getConnection();
			
			try {
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
				executePSUpdate(stmt);
				stmt.close();
			} finally
			{
				conn.close();
			}
			
		}catch(SQLException e)
		{
			log.error("Failed writing pushed back runs to database, something very bad is happening"+e.toString());
			
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
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET  runtime=?, runLength=?, worstCaseEndtime=?, walltime=? WHERE runConfigID=? AND workerUUID=\""+ workerUUID.toString() +"\" AND status=\"ASSIGNED\" AND killJob=0" );
		
		
		try {
			Connection conn = getConnection();
			Calendar worstCaseEndTime = Calendar.getInstance();
			if(run.getRuntime()==0)
				worstCaseEndTime.add(Calendar.SECOND, (int)(run.getRunConfig().getCutoffTime()*1.5-run.getWallclockExecutionTime()+120));
			else
				worstCaseEndTime.add(Calendar.SECOND, (int)(run.getRunConfig().getCutoffTime()*1.5-run.getRuntime()+120));
			try {
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
				stmt.setDouble(1, run.getRuntime());
				stmt.setDouble(2, run.getRunLength());
				stmt.setTimestamp(3, new java.sql.Timestamp(worstCaseEndTime.getTime().getTime()));
				stmt.setDouble(4, run.getWallclockExecutionTime());
				stmt.setString(5, this.runConfigIDMap.get(run.getRunConfig()));
				boolean shouldKill = (executePSUpdate(stmt) == 0);

				stmt.close();
				return shouldKill;
			} finally
			{
				conn.close();
			}
			
		}catch(SQLException e)
		{
			log.error("Failed writing run status to database, something very bad is happening");
			
			throw new IllegalStateException(e);
		}
		
	}
	
	/**
	 * Sleeps via the database the amount of time required
	 * 
	 * Note: Because of a race condition in MySQL we take 5 seconds off the sleep time, and sleep internally, and then resleep in the database without the required text. this is to prevent the query from being interrupted.
	 * We then issue a dummy query to the server to clear any flags.
	 *
	 * See issue #1900 and also http://bugs.mysql.com/bug.php?id=45679 and http://bugs.mysql.com/bug.php?id=70618
	 * 
	 * Note: There is a subtle expectation that client kill requests can't be more than 5 seconds delayed, so see also: {@link ca.ubc.cs.beta.mysqldbtae.persistence.client.MySQLPersistenceClient#wakeWorkers()} 
	 * 
	 * 
	 * 
	 * 
	 * @param sleeptime 
	 * @return true if the sleep completed successfully false otherwise.
	 */
	public boolean sleep(double sleeptime)
	{
		
		if(sleeptime < 0)
		{
			return true;
		}
		
		
		double mysqlSleepTime = Math.max(0, sleeptime - MYSQL_SLEEP_CUTOFF_WINDOW);
		
		
		double threadSleepTime = Math.min(sleeptime, MYSQL_SLEEP_CUTOFF_WINDOW);
		
		
		boolean successfulDBSleep = true;
		if(mysqlSleepTime > 0)
		{
			StringBuilder sb = new StringBuilder("SELECT SLEEP(").append(mysqlSleepTime).append("); /* " + SLEEP_COMMENT_TEXT + " */" );
					
					
					try {
						Connection conn = getConnection();
						
						try {
							PreparedStatement stmt = conn.prepareStatement(sb.toString());
							
							ResultSet rs = stmt.executeQuery();
							
							if(!rs.next())
							{
								stmt.close();
								return false;
							}
							
							if(rs.getInt(1) != 0)
							{
								successfulDBSleep = false;
							}
							
						} finally
						{
							conn.close();
						}
						
					}catch(SQLException e)
					{
						log.error("Failed sleeping through the database, something very bad is happening");
						
						throw new IllegalStateException(e);
					}
		} 
				
		
		if(successfulDBSleep == true)
		{
			try {
				Thread.sleep( (long) (threadSleepTime * 1000) - 100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		
		if(mysqlSleepTime > 0)
		{ //Retry a trip to the database to clear any interrupt flags, etc...
				StringBuilder sb = new StringBuilder("SELECT SLEEP(").append(0.01).append(");" );
					
					
					try {
						Connection conn = getConnection();
						
						try {
							PreparedStatement stmt = conn.prepareStatement(sb.toString());
							
							 stmt.executeQuery();
							
						} finally
						{
							conn.close();
						}
						
					}catch(SQLException e)
					{
						log.error("Failed sleeping through the database, something very bad is happening");
						
						throw new IllegalStateException(e);
					}
		} 
			
		return true;
		
	}

	/**
	 * Return the number of new rows in the database
	 * @return number of new rows in the database.
	 */
	public int getNumberOfNewRuns() 
	{
		
		
		
		StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM ").append(TABLE_RUNCONFIG).append(" WHERE status='NEW' AND `priority` IN (");
		for(JobPriority jb : JobPriority.values())
		{
			sb.append("\"").append(jb).append("\",");
		}
		
		sb.setCharAt(sb.length()-1, ')');
		
		
		try {
			Connection conn = null;
			try {
				conn = getConnection();
			
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
		
				ResultSet rs = executePSQuery(stmt);
				
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
			log.error("Failed executing query,\"{}\", something very bad is happening", sb);
			throw new IllegalStateException(e);
		}
	}
}
