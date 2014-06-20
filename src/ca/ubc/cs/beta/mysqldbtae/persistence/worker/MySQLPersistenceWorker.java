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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aeatk.algorithmexecutionconfiguration.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunconfiguration.AlgorithmRunConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.AlgorithmRunResult;
import ca.ubc.cs.beta.aeatk.exceptions.DeveloperMadeABooBooException;
import ca.ubc.cs.beta.aeatk.json.JSONHelper;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParamFileHelper;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfigurationSpace;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration.ParameterStringFormat;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
import ca.ubc.cs.beta.mysqldbtae.exceptions.AlgorithmExecutionConfigurationIDBlacklistedException;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;
import ca.ubc.cs.beta.mysqldbtae.worker.MySQLTAEWorkerOptions;
import ec.util.MersenneTwister;

public class MySQLPersistenceWorker extends MySQLPersistence {

	/**
	 * Stores primary keys of execution configurations
	 */
	private final Map<Integer, AlgorithmExecutionConfiguration> execConfigMap = new ConcurrentHashMap<Integer, AlgorithmExecutionConfiguration>();
	
	
	/**
	 * Stores a mapping of RunConfigs to the actual runConfigIDs in the database.
	 * Entries created: when worker pulls runs,
	 * Entries deleted: When workers submits runs for last time
	 */
	private final Map<AlgorithmRunConfiguration, String> runConfigIDMap = new ConcurrentHashMap<AlgorithmRunConfiguration, String>();
	
	/**
	 * UUID of Worker
	 */
	protected final UUID workerUUID = UUID.randomUUID();
	
	
	private static final Logger log = LoggerFactory.getLogger(MySQLPersistenceWorker.class);
	
	
	/*
	public MySQLPersistenceWorker(MySQLOptions mysqlOptions, String pool, String jobID, Date startDateTime, Date endDateTime, int runsToBatch, int delayBetweenRequest, int poolIdleTimeLimit, String version, Boolean createTables)
	{
		this(mysqlOptions.host, mysqlOptions.port,mysqlOptions.databaseName,mysqlOptions.username,mysqlOptions.password,pool, jobID, startDateTime, endDateTime, runsToBatch, delayBetweenRequest, poolIdleTimeLimit, version, createTables, 0, 120, 5);
	}*/
	
	/*
	public MySQLPersistenceWorker(MySQLOptions mysqlOptions, String pool, String jobID, Date startDateTime, Date endDateTime, int runsToBatch, int delayBetweenRequest, int poolIdleTimeLimit, String version, Boolean createTables, int concurrencyFactor, int minWorstCaseTime, int worstCaseMultiplier)
	{
		this(mysqlOptions.host, mysqlOptions.port,mysqlOptions.databaseName,mysqlOptions.username,mysqlOptions.password,pool, jobID, startDateTime, endDateTime, runsToBatch, delayBetweenRequest, poolIdleTimeLimit, version, createTables, concurrencyFactor, minWorstCaseTime, worstCaseMultiplier);
	}*/
	
	
	
		

	/**
	 * Job Indentifier, meaningless except for logging purposes
	 */
	private final String jobID;
	
	/**
	 * Expected end of worker, meaningless except for diagnostics
	 */
	private final Date endDateTime;
	private final Date startDateTime;
	
	private final AtomicInteger concurrencyFactor = new AtomicInteger(0);
	
	private final Random rand = new MersenneTwister();


	private final int worstCaseMultiplier;


	private final int minWorstCaseTime;
	
	
	private static final Set<String> shutdownStatements = Collections.newSetFromMap(new ConcurrentHashMap<String,Boolean>());
	static
	{
		
		Runnable r = new Runnable()
		{

			@Override
			public void run() {
				
				
				
				StringBuilder sb = new StringBuilder();
				
				for(String s : shutdownStatements)
				{
					sb.append(s).append("\n");
				}
				
				log.info("Worker process is being shutdown. The following queries will reset the the jobs that this worker did while running to NEW:\n=======================\n{}=======================", sb.toString());
				
				
			}
			
		};
		
		Thread t = new Thread(r);
		Runtime.getRuntime().addShutdownHook(t);
	}
	/*
	public MySQLPersistenceWorker(String host, int port,
			String databaseName, String username, String password, String pool,String jobID, Date startDateTime, Date endDateTime, int runsToBatch, int delayBetweenRequest, int poolIdleTimeLimit, String version, Boolean createTables, int concurrencyFactor, int minWorstCaseTime, int worstCaseMultiplier) {
		super(host, port, databaseName, username, password, pool, createTables);

		log.info("My Worker ID is " + workerUUID.toString());
		this.jobID = jobID;
		this.endDateTime = endDateTime;
		this.startDateTime = startDateTime;
		this.concurrencyFactor.set(concurrencyFactor);
		logWorker(runsToBatch, delayBetweenRequest, pool, poolIdleTimeLimit, version, );
		this.minWorstCaseTime = minWorstCaseTime;
		this.worstCaseMultiplier = worstCaseMultiplier;
	}
	*/
	
	public MySQLPersistenceWorker(MySQLTAEWorkerOptions opts, Date startDateTime, Date endDateTime, String version)
	{
		 
		super(opts.mysqlOptions.host, opts.mysqlOptions.port,opts.mysqlOptions.databaseName,opts.mysqlOptions.username,opts.mysqlOptions.password, opts.pool, opts.createTables);

		log.info("My Worker ID is " + workerUUID.toString());
		this.jobID = opts.jobID;
		this.endDateTime = endDateTime;
		this.startDateTime = startDateTime;
		this.concurrencyFactor.set(opts.concurrencyFactor);
		logWorker(opts.runsToBatch, opts.delayBetweenRequests, opts.pool, opts.poolIdleTimeLimit, version, opts.minRunsToBatch, opts.maxRunsToBatch, opts.autoAdjustRuns );
		this.minWorstCaseTime = opts.minWorstCaseTime;
		this.worstCaseMultiplier = opts.worstCaseMultiplier;
		shutdownStatements.add("UPDATE " + opts.mysqlOptions.databaseName + "." + TABLE_RUNCONFIG + " SET status=\"NEW\" WHERE workerUUID=\""+workerUUID.toString() +"\";" );
	}




	private final Map<Integer, AlgorithmExecutionConfigurationIDBlacklistedException> blacklistedKeys = new HashMap<Integer, AlgorithmExecutionConfigurationIDBlacklistedException>();
	
	private AlgorithmExecutionConfiguration getAlgorithmExecutionConfiguration(int execConfigID) throws SQLException, AlgorithmExecutionConfigurationIDBlacklistedException {
		
	
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
					 
	 				// if(algorithmExecutable.startsWith(AlgorithmExecutionConfiguration.MAGIC_VALUE_ALGORITHM_EXECUTABLE_PREFIX))
					 //{
						//algorithmExecutable = algorithmExecutable.replace(AlgorithmExecutionConfiguration.MAGIC_VALUE_ALGORITHM_EXECUTABLE_PREFIX,"");
					 //}
					 
					 
					 
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
	

	/**
	 * Returns up to n runs for this worker
	 * @param n number of runs to attempt to get
	 * @return runs
	 */
	public List<AlgorithmRunConfiguration> getRuns(int n, int delayBetweenRequests)
	{
	
		StringBuffer sb = new StringBuffer();
		
		
		try {
			
			
		
			//Pull workers off the queue in order of priority, do not take workers where we already tried
			//This isn't a perfect heuristic. I'm hoping it's good enough.
			

			
			int lock = -1;  
			
			if(concurrencyFactor.get() > 0)
			{
				lock = this.rand.nextInt(concurrencyFactor.get());
				//We won't actually check the result and so it doesn't matter if it's once every 2 days
				sb.append("SELECT GET_LOCK(\"" + this.DATABASE + "." + TABLE_RUNCONFIG + ".readLock_" + lock + "\",172800);\n" );
			}
			
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
					" ) B ON B.runConfigID=A.runConfigID SET status=\"ASSIGNED\", workerUUID=\"" + workerUUID.toString() + "\", retryAttempts = retryAttempts+1,worstCaseNextUpdateWhenAssigned=DATE_ADD(NOW(),INTERVAL ").append(Math.max(this.worstCaseMultiplier*delayBetweenRequests,this.minWorstCaseTime)).append(" SECOND)");
					
					
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
				sb.append(";\nSELECT runConfigID , execConfigID, problemInstance, instanceSpecificInformation, seed, cutoffTime, paramConfiguration, cutoffLessThanMax, killJob FROM ").append(TABLE_RUNCONFIG);
				sb.append(" WHERE status=\"ASSIGNED\" AND workerUUID=\"" + workerUUID.toString() + "\" ORDER BY priority DESC;");
				
		
				//stmt.close();
				
				String releaseLockSQL = "\nSELECT RELEASE_LOCK(\"" + this.DATABASE + "." + TABLE_RUNCONFIG + ".readLock_" + lock + "\");";
				if(concurrencyFactor.get() > 0)
				{
						sb.append(releaseLockSQL);
				}

				
				//stmt.addBatch(sb.toString());
				//stmt = conn.prepareStatement(sb.toString());
				log.debug("SQL Query for Job Processing:\n {}", sb);
				try {
					super.executeStatement(stmt, sb.toString());
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
				
				
				if(concurrencyFactor.get() > 0)
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

	/**
	 * add run results to the database
	 * @param runResult a List of AlgorithmRuns to add to the database
	 */
	public void setRunResults(List<AlgorithmRunResult> runResult)
	{
	
		
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET runResult=?, runLength=?, quality=?, resultSeed=?, runtime=?, additionalRunData=?, walltime=?, status='COMPLETE' WHERE workerUUID = ? AND runConfigID=?");
		
		try {
			
			PreparedStatement stmt = null;
			Connection conn = null;
			try {
			conn = getConnection();
			stmt = conn.prepareStatement(sb.toString());
			
				for(AlgorithmRunResult run : runResult)
				{	
					String runConfigID = runConfigIDMap.remove(run.getAlgorithmRunConfiguration());
						
					try {
						
						stmt.setString(1,run.getRunStatus().name());
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
				
				if(executePSUpdate(stmt)>0)
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
	private void logWorker(int runsToBatch, int delayBetweenRequests, String pool, int poolIdleTimeLimit, String version, int minRunsToBatch, int maxRunsToBatch, boolean autoAdjustBatchSize)
	{
		
		StringBuilder sb = new StringBuilder("INSERT ").append(TABLE_WORKERS).append(" (workerUUID, hostname, username, jobID, startTime, startWeekYear, originalEndTime, endTime_UPDATEABLE, runsToBatch_UPDATEABLE, delayBetweenRequests_UPDATEABLE, pool_UPDATEABLE, poolIdleTimeLimit_UPDATEABLE, concurrencyFactor_UPDATEABLE, workerIdleTime_UPDATEABLE,  upToDate, version, worstCaseNextUpdateWhenRunning, minRunsToBatch_UPDATEABLE,maxRunsToBatch_UPDATEABLE,autoAdjustBatchSize_UPDATEABLE)  VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,0,1,?,?,?,?,?)");
		
		
				
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
			stmt.setInt(13, this.concurrencyFactor.get());
			stmt.setString(14,version);
			stmt.setDate(15, new java.sql.Date(System.currentTimeMillis() + Math.max(600, 5*delayBetweenRequests)*1000));
			
			stmt.setInt(16, minRunsToBatch);
			stmt.setInt(17, maxRunsToBatch);
			stmt.setBoolean(18, autoAdjustBatchSize);
			
			try {
				stmt.execute();
			} catch(SQLException e)
			{
				log.error("Couldn't log worker, most likely because of missing columns. Maybe try executing: ALTER TABLE "+TABLE_WORKERS+" ADD COLUMN concurrencyFactor_UPDATEABLE int(11) NOT NULL DEFAULT 0;");
				throw new IllegalStateException("=========================================\n\n\n\nCouldn't create entry for worker in database, maybe try the following query:\n ALTER TABLE "+TABLE_WORKERS+" ADD COLUMN concurrencyFactor_UPDATEABLE int(11) NOT NULL DEFAULT 0;\n\n\n=========================================\n\n", e);
			}
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
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_WORKERS).append(" SET status='DONE', crashInfo=?, worstCaseNextUpdateWhenRunning=NOW() WHERE status='RUNNING' AND workerUUID=\""+workerUUID.toString()+"\" ");
		
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
	 * @return UpdatedWorkerParameters if updated (null otherwise)
	 */
	public UpdatedWorkerParameters getUpdatedParameters(int delayBetweenRequests, int runsToBatch) {
		StringBuilder sb = new StringBuilder("SELECT endTime_UPDATEABLE, runsToBatch_UPDATEABLE, delayBetweenRequests_UPDATEABLE,pool_UPDATEABLE,poolIdleTimeLimit_UPDATEABLE,concurrencyFactor_UPDATEABLE,minRunsToBatch_UPDATEABLE, maxRunsToBatch_UPDATEABLE,autoAdjustBatchSize_UPDATEABLE FROM ").append(TABLE_WORKERS).append(" WHERE upToDate=0 AND workerUUID=\""+workerUUID.toString()+"\" ");
		
		
		try {
			Connection conn = null;
			try {
				conn = getConnection();
			
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
		
				ResultSet rs = stmt.executeQuery();
				
				
				boolean newData;;
				if(!rs.next())
				{
					stmt.close();
					
					newData = false;
					//return null;
				} else
				{
					newData = true;
				}
				
				UpdatedWorkerParameters newParameters;

				
				if(newData)
				{
					if(this.blacklistedKeys.size() > 0)
					{
						log.debug("Flushing blacklist which previously had {} entries", this.blacklistedKeys.size());
						this.blacklistedKeys.clear();
					}
					
					long timeLimit = rs.getTimestamp(1).getTime()-startDateTime.getTime();
					
					runsToBatch = rs.getInt(2);
					newParameters = new UpdatedWorkerParameters(timeLimit, rs.getInt(2), rs.getInt(3), rs.getString(4), rs.getInt(5),rs.getInt(6),rs.getInt(7), rs.getInt(8), rs.getBoolean(9));
					
					this.concurrencyFactor.set(newParameters.getConcurrencyFactor());
					
					delayBetweenRequests = rs.getInt(3);
					stmt.close();
				} else
				{
					newParameters = null;
				}
				
				
				sb = new StringBuilder("UPDATE ").append(TABLE_WORKERS).append(" SET status='RUNNING',crashInfo='', runsToBatch_UPDATEABLE="+runsToBatch+", upToDate=1, worstCaseNextUpdateWhenRunning=DATE_ADD(NOW(),INTERVAL ").append(Math.max(worstCaseMultiplier*delayBetweenRequests,minWorstCaseTime)).append(" SECOND) WHERE workerUUID=\""+workerUUID.toString()+"\" ");
				
				stmt = conn.prepareStatement(sb.toString());
				int linesModified = executePSUpdate(stmt);
				
				if(linesModified == 0)
				{
					throw new IllegalStateException("No row updated in table, this suggests that the entry was deleted and that this worker should shutdown");
				}
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
	
	public void changeWorkerIdleStatus(int delayBetweenRequests, boolean idle)
	{
		try(Connection conn = getConnection())
		{
			StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_WORKERS).append(" SET status='RUNNING',crashInfo='', currentlyIdle="+ String.valueOf(idle).toUpperCase()+", worstCaseNextUpdateWhenRunning=DATE_ADD(NOW(),INTERVAL ").append(Math.max(worstCaseMultiplier*delayBetweenRequests,minWorstCaseTime)).append(" SECOND) WHERE workerUUID=\""+workerUUID.toString()+"\" ");
			
			try(PreparedStatement stmt = conn.prepareStatement(sb.toString()))
			{
				super.executePS(stmt);
				stmt.close();
			}
			
		} catch (SQLException e) {
			log.error("Couldn't update worker idle status", e);
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * Sums the idle times of all workers in the pool, launched within the last 3 weeks
	 * @return int representing the summed idle times
	 */
	public long sumIdleTimes() {
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
				
				long idleTime;
				if(!rs.next())
				{
					stmt.close();
					idleTime = -1;
				} else
				{
					idleTime = rs.getLong(1);
				}
				
				log.info("Sum of worker pool idle time with query {} was {} ", sb.toString(),  idleTime);
				return rs.getInt(1);
			} finally
			{
				if(conn != null) conn.close();
				
			}
			
		} catch(SQLException e)
		{
			log.error("Failed retrieving worker Information to database, something very bad is happening");
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
	 * @param extraRuns  List of AlgorithmExecutionConfiguration and RunConfig Pairs
	 */
	public void resetRunConfigs(List<AlgorithmRunConfiguration> extraRuns) {
		if(extraRuns.isEmpty())
			return;
		
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET  status='NEW', workerUUID=0 WHERE status=\"ASSIGNED\"  AND workerUUID=\""+ workerUUID.toString() +"\" AND runConfigID IN (" );

		for(AlgorithmRunConfiguration ent : extraRuns)
		{
			sb.append(this.runConfigIDMap.get(ent)+",");
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
	public boolean updateRunStatusAndCheckKillBit(AlgorithmRunResult run) {
		
		//This query is designed to update the database IF and only IF
		//The run hasn't been killed. If we get 0 runs back, then we know the run has been killed
		//This saves us another trip to the database
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET  runtime=?, runLength=?, worstCaseEndtime=?, walltime=? WHERE runConfigID=? AND workerUUID=\""+ workerUUID.toString() +"\" AND status=\"ASSIGNED\" AND killJob=0" );
		
		
		try {
			Connection conn = getConnection();
			Calendar worstCaseEndTime = Calendar.getInstance();
			if(run.getRuntime()==0)
				worstCaseEndTime.add(Calendar.SECOND, (int)(run.getAlgorithmRunConfiguration().getCutoffTime()*1.5-run.getWallclockExecutionTime()+120));
			else
				worstCaseEndTime.add(Calendar.SECOND, (int)(run.getAlgorithmRunConfiguration().getCutoffTime()*1.5-run.getRuntime()+120));
			try {
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
				stmt.setDouble(1, run.getRuntime());
				stmt.setDouble(2, run.getRunLength());
				stmt.setTimestamp(3, new java.sql.Timestamp(worstCaseEndTime.getTime().getTime()));
				stmt.setDouble(4, run.getWallclockExecutionTime());
				stmt.setString(5, this.runConfigIDMap.get(run.getAlgorithmRunConfiguration()));
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
		 * Updates the run in the database
		 * @param killableAlgorithmRun
		 * @return <code>true</code> if the job has been killed or otherwise is no longer updatedable by us
		 */
		public void updateOutstandingRunsLastUpdateTime(int delayBetweenRequests) {
			
			//This query is designed to update the database IF and only IF
			//The run hasn't been killed. If we get 0 runs back, then we know the run has been killed
			//This saves us another trip to the database
			
			
			StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET worstCaseNextUpdateWhenAssigned=DATE_ADD(NOW(),INTERVAL ").append(Math.max(this.worstCaseMultiplier*delayBetweenRequests,this.minWorstCaseTime)).append(" SECOND) WHERE runConfigID IN (");
			
			if(this.runConfigIDMap.size() > 0)
			{
				for(String runConfigID : new TreeSet<String>(this.runConfigIDMap.values()))
				{
					sb.append(runConfigID).append(",");
				}
					
				sb.setCharAt(sb.length()-1, ')');
					
				sb.append(" AND workerUUID=\""+ workerUUID.toString() +"\" AND status=\"ASSIGNED\" AND killJob=0" );
			
			
				
				try (Connection conn = getConnection())
				{
					//System.out.println(sb.toString());
					PreparedStatement stmt = conn.prepareStatement(sb.toString());
					executePS(stmt);
	
				}catch(SQLException e)
				{
					log.error("Failed writing run status to database, something very bad is happening, the query was: " + sb.toString());
					
					throw new IllegalStateException(e);
				}
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
				Thread.sleep( Math.max((long) (threadSleepTime * 1000) - 100,0));
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
			
		return successfulDBSleep;
		
	}

	
	/**
	 * 
	 * @return
	 */
	public boolean workersWaiting()
	{
	
		StringBuilder sb = new StringBuilder("SELECT workerUUID FROM ").append(TABLE_WORKERS).append(" WHERE status='RUNNING' AND `currentlyIdle`=TRUE AND worstCaseNextUpdateWhenRunning >= NOW() LIMIT 1");
		
		try (Connection conn = getConnection())
		{
			
			try(Statement stmt = conn.createStatement())
			{
				
				ResultSet rs = super.executeStatement(stmt, sb.toString());
				
				if(rs.next())
				{
					return true;
				} else
				{
					return false;
				}
				
			
			}
			
		} catch (SQLException e) {
			log.error("Couldn't check if workers were waiting: ", e);
			e.printStackTrace();
		}
				
		
		return false;
	}
	/**
	 * Return the number of new rows in the database
	 * @deprecated This is an expensive query against the runconfig table, if you just want to know if workers are waiting, use the workersWaiting() method.
	 * 
	 * @return number of new rows in the database.
	 * 
	 */
	@Deprecated
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
