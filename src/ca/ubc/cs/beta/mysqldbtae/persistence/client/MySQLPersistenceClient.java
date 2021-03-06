package ca.ubc.cs.beta.mysqldbtae.persistence.client;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.PacketTooBigException;

import ca.ubc.cs.beta.aeatk.algorithmexecutionconfiguration.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunconfiguration.AlgorithmRunConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.AlgorithmRunResult;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.ExistingAlgorithmRunResult;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.RunStatus;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.RunningAlgorithmRunResult;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.kill.KillHandler;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.kill.StatusVariableKillHandler;
import ca.ubc.cs.beta.aeatk.json.JSONHelper;
import ca.ubc.cs.beta.aeatk.misc.watch.AutoStartStopWatch;
import ca.ubc.cs.beta.aeatk.misc.watch.StopWatch;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfigurationSpace;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration.ParameterStringFormat;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorOptions;
import ca.ubc.cs.beta.mysqldbtae.util.ACLibHasher;
import ca.ubc.cs.beta.mysqldbtae.util.PathStripper;

/**
 * Handles dispatching runs to a MySQL Database to be retrieved later.
 * 
 * This class is presumed to be thread safe, but may not be currently,
 * if you encounter any thread safety bugs in this, you will need to fix them.
 * 
 * The developer should have done another pass on the code to make sure it is threadsafe.
 * 
 * The thread safety of this class is meant to work as follows.
 * 
 * When you create a run token, the only other operations you can do on it
 * are poll for results. This will involve synchronizing on the runtoken in question.
 * 
 * All shared datastructures, are meant to be thread safe, and the actual per-runtoken do not need to be 
 * because they will be synchronized on the run token.
 * 
 * 
 * 
 * @author sjr
 *
 */
public class MySQLPersistenceClient extends MySQLPersistence {

	
	/**
	 * Stores a mapping of tokens to client, to the actual runConfigUUIDs in the database.
	 */
	private final Map<RunToken, List<String>> runToIntegerMap = new ConcurrentHashMap<RunToken, List<String>>();
	
	/**
	 * Stores a mapping of tokens to the set of RunConfigUUIDs that are incomplete.
	 */
	private final Map<RunToken, Set<String>> runTokenToIncompleteRunsSet = new ConcurrentHashMap<RunToken, Set<String>>();
	
	/**
	 * Stores a mapping of tokens to RunConfigs that are associated with it.
	 */
	private final Map<RunToken, List<AlgorithmRunConfiguration>> runTokenToRunConfigMap = new ConcurrentHashMap<RunToken, List<AlgorithmRunConfiguration>>();
	
	/**
	 * Stores a set of completed RunTokens (they can no longer be queried)
	 */
	private final Set<RunToken> completedRunTokens = Collections.newSetFromMap(new ConcurrentHashMap<RunToken,Boolean>());
	
	/**
	 * Stores a mapping from RunConfigUUID to RunConfig
	 */
	private final Map<RunToken, Map<String, AlgorithmRunConfiguration>> runTokenToStringRCMap = new ConcurrentHashMap<RunToken, Map<String, AlgorithmRunConfiguration>>();
	
	
	/**
	 * Stores a mapping from RunConfigUUID to runConfigID
	 */
	private final Map<RunToken, Map<String, Integer>> runTokenToStringIDMap = new ConcurrentHashMap<RunToken, Map<String, Integer>>();
	
	
	/**
	 * Stores a mapping from RunConfig to RunConfigUUID
	 */
	private final Map<RunToken, Map<AlgorithmRunConfiguration, String>> runTokenToRCStringMap = new ConcurrentHashMap<RunToken, Map<AlgorithmRunConfiguration, String>>();
	
	
	/**
	 * Stores a mapping from runTokenToCompletedRuns 
	 */
	private final Map<RunToken, Map<AlgorithmRunConfiguration, AlgorithmRunResult>> runTokenToCompletedRuns = new ConcurrentHashMap<RunToken, Map<AlgorithmRunConfiguration, AlgorithmRunResult>>();
	
	private final Map<RunToken, Set<String>> runTokenToKilledJobs = new ConcurrentHashMap<RunToken, Set<String>>();
	
	/**
	 * Stores a mapping from the Run Token to the observer
	 */
	private final Map<RunToken, TargetAlgorithmEvaluatorRunObserver> runTokenToObserverMap = new ConcurrentHashMap<RunToken,TargetAlgorithmEvaluatorRunObserver>();
	
	/**
	 * Stores our current mapping of RunToken to OutstandingRuns
	 */
	private final Map<RunToken,  Map<AlgorithmRunConfiguration, AlgorithmRunResult>> runTokenToOutstandingRuns = new ConcurrentHashMap<RunToken,  Map<AlgorithmRunConfiguration, AlgorithmRunResult>>();
	
	private final Map<RunToken, Map<AlgorithmRunConfiguration, KillHandler>> runTokenToKillHandler = new ConcurrentHashMap<RunToken, Map<AlgorithmRunConfiguration, KillHandler>>();
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	

	/**
	 * Number of RunConfigUUIDs to pool for status at any given time
	 */
//	private final int QUERY_SIZE_LIMIT = 1000;
	
	/**
	 * Used to tie all the run requests with a specific execution
	 * Do not change after initialization
	 */
	private volatile int commandID = -1;
	
	
	/**
	 * Object that will strip the path from a URL
	 */
	private final PathStripper pathStrip;
	
	/**
	 * RunToken Identifiers are drawn from this pool
	 */
	AtomicInteger runTokenKeys = new AtomicInteger();

	/**
	 * Computes hashes for ACLib Entities
	 */
	private static final ACLibHasher hasher = new ACLibHasher();
	
	private final MySQLTargetAlgorithmEvaluatorOptions opts;
	

	private final String processName = (ManagementFactory.getRuntimeMXBean().getName().trim().length() > 0) ? ManagementFactory.getRuntimeMXBean().getName().trim() : "Unknown_JVM";

	private final boolean shutdownWorkersOnCompletion;
	
	private final AtomicBoolean logWarningOnInstanceBeingTooLong = new AtomicBoolean(false);

	
	private static final AtomicBoolean logHowToTurnOffInsertionLogging = new AtomicBoolean(false);
	
	public MySQLPersistenceClient(MySQLTargetAlgorithmEvaluatorOptions opts)
	{

		super(opts.host, opts.port, opts.databaseName, opts.username, opts.password, opts.pool, opts.createTables);
		
		if(opts.priority == null)
		{
			throw new IllegalArgumentException("Priority cannot be null"); 
		}
		
		this.shutdownWorkersOnCompletion = opts.shutdownWorkersOnCompletion;
		
		if(opts.deletePartitionDataOnShutdown && opts.runPartition < 0)
		{
			throw new IllegalArgumentException("You cannot delete partition data with a negative partition value, this is a safety mechanism. You could run: DELETE FROM " + TABLE_RUNCONFIG + " WHERE runPartition="+opts.runPartition);
		}
		
		this.opts= opts;
		
		String pathStrip = opts.pathStrip;
		
		if(pathStrip != null)
		{
			pathStrip = pathStrip.trim();
		}
		

		if(pathStrip != null && pathStrip.trim().endsWith("/"))
		{
			log.warn("Path strip variable has a / at the end this may behave unexpectedly" );
			try {
				Thread.sleep(2048);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		
		this.pathStrip = new PathStripper(pathStrip);
		
		if(opts.batchInsertSize == 0)
		{
			throw new IllegalArgumentException("Batch insert size must be greater than zero");
		}
		

		//this(mysqlOptions.host, mysqlOptions.port,mysqlOptions.databaseName,mysqlOptions.username,mysqlOptions.password,pool, null, batchInsertSize, createTables, runPartition, deletePartitionDataOnShutdown, priority, false, false);

	}



	/**
	 * Returns the runs if completed, null otherwise 
	 * @param token - previously issued Run Token
	 * @return <code>null</code> if the results aren't available yet, the in order sequence of runs if they are
	 */
	public  List<AlgorithmRunResult> pollRunResults(RunToken token) {
			
			if(token == null) { 
				throw new IllegalStateException("RunToken cannot be null"); 
			}
			
			//=== Lock on the run token
			synchronized(token)
			{
				AutoStartStopWatch watch = new AutoStartStopWatch();
				
				if(completedRunTokens.contains(token))
				{
					throw new IllegalStateException("RunToken is no longer valid");
				}
				
				if(runToIntegerMap.get(token) == null)
				{
					throw new IllegalStateException("RunToken doesn't exist in mapping");
				}
			
				
				
				int runs = runToIntegerMap.get(token).size(); 
				
				Connection conn = null;
				try {
					
					conn = getConnection();
				
					Map<AlgorithmRunConfiguration, AlgorithmRunResult> userRuns = runTokenToCompletedRuns.get(token);
					Map<AlgorithmRunConfiguration, AlgorithmRunResult> outstandingRuns = runTokenToOutstandingRuns.get(token); 
					Map<AlgorithmRunConfiguration, KillHandler> killHandlers = runTokenToKillHandler.get(token);
					Map<AlgorithmRunConfiguration, String> runConfigToString = runTokenToRCStringMap.get(token);
					Map<String,AlgorithmRunConfiguration> stringToRunConfig  = runTokenToStringRCMap.get(token);
					Set<String> killedJobs = runTokenToKilledJobs.get(token);
					
					try {
					
						Set<String> incompleteRuns = runTokenToIncompleteRunsSet.get(token);
						
						while(userRuns.size() < runs)
						{
							StringBuilder sb = new StringBuilder();	
							
							String addlRunData = (this.opts.additionalRunData) ? ", result_additionalRunData" : "";
							sb.append("SELECT runHashCode, status, result_status, result_runtime, result_runLength, result_quality, result_seed, result_walltime"+ addlRunData + "  FROM ").append(TABLE_RUNCONFIG).append(" WHERE runHashCode IN (");
							
						
							int querySize = 0;
							for(String key : incompleteRuns)
							{
								if(querySize >= opts.queryBatchSize)
								{
									break;
								}
								querySize++;
								sb.append("\""+key + "\",");
							}
							
							
							//Get rid of the last comma
							sb.setCharAt(sb.length()-1, ' ');
							
							sb.append(") AND (status=\"COMPLETE\" OR status=\"ASSIGNED\")");
							log.trace("Query was {} ", sb);
							PreparedStatement stmt = null;
							int returnedResults = 0;
							try 
							{
								stmt = conn.prepareStatement(sb.toString());
							
								ResultSet rs = executePSQuery(stmt);
								
								while(rs.next())
								{	
									if(rs.getString(2).equals("COMPLETE"))
									{
										
										
										String addlRunDataStr = (this.opts.additionalRunData) ? rs.getString(9) : "";
										
										
										RunStatus result = RunStatus.valueOf(rs.getString(3));
										double runtime = rs.getDouble(4);
										double runlength = rs.getDouble(5);
										double quality = rs.getDouble(6);
										long seed = rs.getLong(7);
										
										double walltime = rs.getDouble(8);
										
										AlgorithmRunConfiguration runConfig =  stringToRunConfig.get(rs.getString(1));
										incompleteRuns.remove(rs.getString(1));
										outstandingRuns.remove(runConfig);
										/**
										 * AlgorithmExecutionConfiguration execConfig, RunConfig runConfig, String result, double wallClockTime
										 */
											
										AlgorithmRunResult run = new ExistingAlgorithmRunResult( runConfig, result, runtime, runlength, quality, seed, addlRunDataStr, walltime);
										
										if(run.getRunStatus().equals(RunStatus.ABORT))
										{
											Object[] args = {rs.getString(1), rs.getString(2), rs.getString(3) } ;
											log.debug("ABORT DETECTED: {} : {} : {}",args );
										}
										userRuns.put(runConfig, run);
										returnedResults++;
										
									} else if(rs.getString(2).equals("ASSIGNED"))
									{
										AlgorithmRunConfiguration runConfig =  stringToRunConfig.get(rs.getString(1));
										
										double runtime = rs.getDouble(4);
									
										long seed = rs.getLong(7);
										
										double walltime = rs.getDouble(8);
										
										
										outstandingRuns.put(runConfig, new RunningAlgorithmRunResult(runConfig, runtime,0,0,seed, walltime , killHandlers.get(runConfig)));
									} else
									{	
										throw new IllegalStateException("Must have some new status that we don't know what do with in the database");
									}
								}	
							} finally
							{
								if(stmt != null) stmt.close();
							}
							
					
							//Thread.sleep(1000);
							log.trace("Queried for {} got {} results back", querySize, returnedResults);
							Object args[] =  { token, userRuns.size(), runs };
							
							
							if(returnedResults > 0)
							{
								log.debug("RunToken {} has {} out of {} runs complete ",args);
							}
							
							
							
							
							List<String> runsToKill = new ArrayList<String>();
							
							for(Entry<AlgorithmRunConfiguration, KillHandler> ent : killHandlers.entrySet())
							{
								String runToKill = runConfigToString.get(ent.getKey());
								if(!ent.getValue().isKilled() || (killedJobs.contains(runToKill)))
								{
									continue;
								}
								
								runsToKill.add(runToKill);
								
								
							}
							
							
							if(runsToKill.size() > 0)
							{
								
								//=== Kill all the NEW jobs by marking them as complete
								sb = new StringBuilder();
								sb.append("UPDATE ").append(TABLE_RUNCONFIG).append(" SET killJob=1, result_seed=seed, result_additionalRunData=CONCAT(\"Killed By Client (\",?,\" ) While Status \",status), status=\"COMPLETE\",result_status=\"KILLED\" WHERE (status=\"NEW\" OR status=\"ASSIGNED\") AND runHashCode IN (");
								
								for(int i=0; i < Math.min(opts.queryBatchSize,runsToKill.size()); i++)
								{
									//Only jobs upto the QUERY_SIZE_LIMIT will be killed, other jobs will wait until next poll
									if(i!=0)
									{
										sb.append(",");
									}
									sb.append("\""+runsToKill.get(i)+"\"");
									killedJobs.add(runsToKill.get(i));
								}
								
								sb.append(")");
								
								try {
									stmt = conn.prepareStatement(sb.toString());
									stmt.setString(1, this.processName);
									executePS(stmt);
								
								} finally
								{
									if(stmt != null) stmt.close();
								}
								
							}
							
							
							List<AlgorithmRunConfiguration> rcs = runTokenToRunConfigMap.get(token);
							
							
							List<AlgorithmRunResult> runsInProgress = new ArrayList<AlgorithmRunResult>(rcs.size());
							
							for(int i=0; i < rcs.size(); i++)
							{
								//Bleh populate the array
								runsInProgress.add(outstandingRuns.get(rcs.get(0)));
							}
							for(int i=0; i < rcs.size(); i++)
							{
								if(outstandingRuns.containsKey(rcs.get(i)))
								{
									runsInProgress.set(i, outstandingRuns.get(rcs.get(i)));
								} else
								{
									runsInProgress.set(i,userRuns.get(rcs.get(i)));
								}
								
								
								log.trace("Updated information {}", outstandingRuns.get(rcs.get(i)));
							}
							

							runTokenToObserverMap.get(token).currentStatus(runsInProgress);
							
							
							
							if(querySize > returnedResults)
							{
								return null;
							}
						}
						
						
						
						
					} catch(SQLException e)
					{
						throw new IllegalStateException("SQL Error", e);
					}
					
					List<AlgorithmRunResult> runResults = new ArrayList<AlgorithmRunResult>(this.runTokenToRunConfigMap.get(token).size());
					
					for(AlgorithmRunConfiguration rc : this.runTokenToRunConfigMap.get(token))
					{
						runResults.add(userRuns.get(rc));
					}
					
					//Clean up everything
			
					completedRunTokens.add(token);
					runTokenToCompletedRuns.remove(token);
					runToIntegerMap.remove(token);
					runTokenToIncompleteRunsSet.remove(token);
					runTokenToRunConfigMap.remove(token);
					runTokenToObserverMap.remove(token);
					runTokenToOutstandingRuns.remove(token);
					runTokenToKillHandler.remove(token);
					runTokenToStringRCMap.remove(token);
					runTokenToRCStringMap.remove(token);
					runTokenToStringIDMap.remove(token);

					
					
					log.info("Total time to poll database was {} seconds", watch.stop() / 1000.0);
					return runResults;
				} finally
				{
					if(conn != null)
					{
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
				
		}

	public RunToken enqueueRunConfigs(List<AlgorithmRunConfiguration> runConfigs, TargetAlgorithmEvaluatorRunObserver obs)
	{

		AutoStartStopWatch completeInsertionTime = new AutoStartStopWatch();
	
		if(runConfigs == null || runConfigs.size() == 0) 
		{
			throw new IllegalArgumentException("Must supply atleast one run " + runConfigs);
		}
	
		List<String> runKeys = new ArrayList<String>(runConfigs.size());
		
		RunToken runToken = new RunToken(runTokenKeys.incrementAndGet());
		
		Connection conn = null;
		try {
			
			if(obs == null)
			{
				obs = new TargetAlgorithmEvaluatorRunObserver()
				{

					@Override
					public void currentStatus(List<? extends AlgorithmRunResult> runs) {
						//NOOP
					}
					
				};
				
			}
		
			conn = getConnection();
			runTokenToOutstandingRuns.put(runToken, new HashMap<AlgorithmRunConfiguration,AlgorithmRunResult>());
			runTokenToObserverMap.put(runToken, obs);
			runTokenToKillHandler.put(runToken, new HashMap<AlgorithmRunConfiguration, KillHandler>());
			runTokenToStringRCMap.put(runToken,new HashMap<String, AlgorithmRunConfiguration>());
			runTokenToRCStringMap.put(runToken,new HashMap<AlgorithmRunConfiguration,String>());
			runTokenToKilledJobs.put(runToken, new HashSet<String>());
			
			Map<AlgorithmRunConfiguration, AlgorithmRunResult> outstandingRuns = runTokenToOutstandingRuns.get(runToken);
			Map<AlgorithmRunConfiguration, KillHandler> killHandlers = runTokenToKillHandler.get(runToken);
			Map<AlgorithmRunConfiguration, String> runConfigToString = runTokenToRCStringMap.get(runToken);
			Map<String,AlgorithmRunConfiguration> stringToRunConfig  = runTokenToStringRCMap.get(runToken);
			Map<String, Integer> runConfigToIDMap = new HashMap<String, Integer>();
			
			
			
			boolean shouldLogProgress = ( opts.logIncrementalProgressOfLargeInserts && runConfigs.size() >= opts.incrementProgressLargeInsertSize) ? true : false;
			
			if(shouldLogProgress && logHowToTurnOffInsertionLogging.compareAndSet(false, true))
			{
				log.info("MySQL Target Algorithm Evaluator is set to log large requests, you can adjust this setting with --mysqldbtae-log-large-inserts-progress");
			}
			Double[] timesToLog = {Math.pow(10, -6),0.001,0.1,0.25, 0.33, 0.5, 0.66, 0.75,0.9,0.99, 1.1 /**DO NOT REMOVE THIS ELEMENT, IT ENSURES THAT WE CAN NEVER GET A NPE WHEN PEEKING **/};
			
			for(int i=0 ; i < timesToLog.length; i++)
			{
				timesToLog[i] = runConfigs.size()*timesToLog[i];
			}
			
			Queue<Double> thresholdsToLog = new LinkedList<>(Arrays.asList(timesToLog)); 
			
			int totalRunsInserted = 0;
			for(int i=0; (i < Math.ceil((runConfigs.size()/(double) opts.batchInsertSize)));i++)
			{
				
			
				int listLowerBound = i*opts.batchInsertSize;
				int listUpperBound = Math.min((i+1)*opts.batchInsertSize,runConfigs.size());
				
				Object[] args2 =  { i, listLowerBound, listUpperBound, runConfigs.size()};
				log.trace("Lower and Upper Bound {}: ({}-{})  (size: {})",args2);
				
				
				StringBuilder sb = new StringBuilder();
				sb.append("INSERT INTO ").append(TABLE_RUNCONFIG).append(" ( algorithmExecutionConfigID, problemInstance, instanceSpecificInformation, seed, cutoffTime, paramConfiguration, cutoffLessThanMax, runHashCode, runPartition, priority, result_additionalRunData, lastInsertionTime) VALUES ");
		
				
				for(int j = listLowerBound; j < listUpperBound; j++ )
				{				
						 sb.append(" (?,?,?,?,?,?,?,?,?,?,'',NOW()),");
				
				}
		
				sb.setCharAt(sb.length()-1, ' ');
				
				sb.append(" ON DUPLICATE KEY UPDATE priority=\"" +opts.priority+ "\",retryAttempts=0, result_runtime=IF(killJob=1 OR (status=\"COMPLETE\" AND result_status=\"ABORT\"),0,result_runtime), status=IF(killJob = 1 OR (status=\"COMPLETE\" AND result_status=\"ABORT\"),\"NEW\",status), lastInsertionTime=IF(killJob=1 OR (status=\"COMPLETE\" AND result_status=\"ABORT\"),NOW(),lastInsertionTime), killJob=0");
				

				try {
					PreparedStatement stmt = null;
					StopWatch stopWatch = new StopWatch();
					try {
						stmt = conn.prepareStatement(sb.toString());
					
					
						log.trace("SQL INSERT: {} ", sb);
						
						log.trace("Preparing for insertion of {} rows into DB", listUpperBound-listLowerBound);
			
						int k=1;
						List<String> uuids = new ArrayList<String>(listUpperBound - listLowerBound + 1);
						
						for(int j =listLowerBound; j < listUpperBound; j++ )
						{				
							AlgorithmRunConfiguration rc = runConfigs.get(j);
							
							
							KillHandler kh = new StatusVariableKillHandler();
							
							
							killHandlers.put(rc, kh);
							outstandingRuns.put(rc, new RunningAlgorithmRunResult(rc, 0,0,0,rc.getProblemInstanceSeedPair().getSeed() ,0, kh));
							String uuid = getHash(rc, opts.runPartition);
							uuids.add(uuid);
							Integer execConfigID = this.getAlgorithmExecutionConfigurationID(rc.getAlgorithmExecutionConfiguration());
							
							stmt.setInt(k++, execConfigID);
							stmt.setString(k++, pathStrip.stripPath(rc.getProblemInstanceSeedPair().getProblemInstance().getInstanceName()));
							
							/* Changed to TEXT
							if(rc.getProblemInstanceSeedPair().getProblemInstance().getInstanceSpecificInformation().length() > 8172)
							{
								log.warn("If you get an exception when inserting this row, it's probably because the instance specific information is too long, the maximum length in this version by default is 8172.  You can try changing the column to a TEXT field but be warned that if you do any further queries referencing this column the performance will be very poor.");
								//throw new UnsupportedOperationException("MySQL DB Only supports Instance Specific Information of 4K or less in this version, I'm sorry");
							}
							*/
							if(rc.getProblemInstanceSeedPair().getProblemInstance().getInstanceName().length() > 8172)
							{
								if(this.logWarningOnInstanceBeingTooLong.compareAndSet(false, true))
								{
									log.warn("If you get an exception when inserting this row, it's probably because the instance name is too long, the maximum length in this version by default is 8172. You can try changing the column to a TEXT field but be warned that if you do any further queries referencing this column the performance will be very poor.");
								}
								//throw new UnsupportedOperationException("MySQL DB Only supports Instance Specific Information of 4K or less in this version, I'm sorry");
							}
							
							stmt.setString(k++, rc.getProblemInstanceSeedPair().getProblemInstance().getInstanceSpecificInformation());
							stmt.setLong(k++, rc.getProblemInstanceSeedPair().getSeed());
							stmt.setDouble(k++, rc.getCutoffTime());
							String configString = rc.getParameterConfiguration().getFormattedParameterString(ParameterStringFormat.ARRAY_STRING_SYNTAX);
							
							
							
							
							/*
							if(configString.length() > 2000)
							{
								log.warn("If you get an exception when inserting this row, it is probably because the configuration space string is too long for the table");
							}*/
							stmt.setString(k++, configString);
							
							stmt.setBoolean(k++, rc.hasCutoffLessThanMax());
							stmt.setString(k++,uuid);
							stmt.setInt(k++, opts.runPartition);
							stmt.setString(k++, opts.priority.name());
							runKeys.add(uuid);
							
							stringToRunConfig.put(uuid, rc);
							runConfigToString.put(rc,uuid);
						}
					
						log.trace("Inserting Rows");
						stopWatch.start();
		
						executePS(stmt);

					} finally 
					{
						if(stmt != null) stmt.close();
					}
					
					totalRunsInserted += (listUpperBound - listLowerBound);
					
					
					
					
					double timePerRow = ((listUpperBound - listLowerBound) / (stopWatch.stop() / 1000.0));
					
					Object[] args = { listUpperBound - listLowerBound, stopWatch.time()/1000.0, timePerRow};
					log.trace("Insertion of {} rows took {} seconds {} row / second", args);
					
				
					if(shouldLogProgress)
					{
						boolean shouldLog = false;
						while(totalRunsInserted > thresholdsToLog.peek())
						{
							shouldLog = true;
							thresholdsToLog.poll();
						}
						
						if(shouldLog)
						{
							log.info("Insertion progress {} of {} total rows, most recent insertion of {} rows took: {} seconds (@ {} rows / second) , cumulative time: {} seconds (@ {} rows / second).", totalRunsInserted, runConfigs.size(), listUpperBound - listLowerBound, stopWatch.time() / 1000.0, timePerRow, completeInsertionTime.time() / 1000.0 ,  totalRunsInserted / (completeInsertionTime.time() / 1000.0));
						}
					}
				} catch(PacketTooBigException e)
				{
					throw new IllegalStateException("SQL Error Occured, Try lowering the Batch Size", e);
				} catch (SQLException e) {
					throw new IllegalStateException("SQL Error", e);
				}
			}
			
			this.runTokenToRunConfigMap.put(runToken, runConfigs);
			this.runToIntegerMap.put(runToken, runKeys);
			this.runTokenToObserverMap.put(runToken, obs);
			Set<String> unfinishedRunConfigs = new HashSet<String>();
			
			
			
			unfinishedRunConfigs.addAll(runKeys);
			this.runTokenToIncompleteRunsSet.put(runToken, unfinishedRunConfigs);
			
			this.runTokenToCompletedRuns.put(runToken, new HashMap<AlgorithmRunConfiguration, AlgorithmRunResult>());
			this.runTokenToStringIDMap.put(runToken, runConfigToIDMap);
			
			
			Object[] args3 = { runConfigs.size(), completeInsertionTime.stop() / 1000.0, runConfigs.size() / (completeInsertionTime.stop() /1000.0)};
			if(!shouldLogProgress)
			{
				log.debug("Total time to insert {} rows was {} seconds, {} rows / second", args3);
			}  else
			{
				log.info("Total time to insert {} rows was {} seconds, {} rows / second", args3);
			}
			synchronized(runToken)
			{
				return runToken;
			}
		} finally
		{
			if(conn != null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}

	
	private final ConcurrentHashMap<AlgorithmExecutionConfiguration, Integer> execConfigToIDMap = new ConcurrentHashMap<AlgorithmExecutionConfiguration, Integer>();
	
	/**
	 * Retrieves the ID for this execution configuration object
	 * 
	 * @param execConfig
	 * @return
	 */
	private int getAlgorithmExecutionConfigurationID(AlgorithmExecutionConfiguration execConfig)
	{
		
		/**
		 * There is a benign race condition here, the database will take care of this however.
		 */
		Integer id = execConfigToIDMap.get(execConfig);
		
		if(id != null)
		{
			return id;
		}
				
		
		Connection conn = null; 
		try {
			conn = getConnection();
			
			File f = new File(execConfig.getParameterConfigurationSpace().getParamFileName());

			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO ").append(TABLE_EXECCONFIG).append(" (algorithmExecutable, algorithmExecutableDirectory, parameterFile, deterministicAlgorithm, cutoffTime, algorithmExecutionConfigHashCode, algorithmExecutionConfigurationJSON) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE algorithmExecutionConfigID=LAST_INSERT_ID(algorithmExecutionConfigID), lastModified = NOW()");
			
			try {
				PreparedStatement stmt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
	
				stmt.setString(1, pathStrip.stripPath(execConfig.getAlgorithmExecutable()));
				stmt.setString(2, pathStrip.stripPath(execConfig.getAlgorithmExecutionDirectory()));
				stmt.setString(3, pathStrip.stripPath(execConfig.getParameterConfigurationSpace().getParamFileName()));
				stmt.setBoolean(4, execConfig.isDeterministicAlgorithm());
				stmt.setDouble(5,execConfig.getAlgorithmMaximumCutoffTime());
				stmt.setString(6, hasher.getHash(execConfig,pathStrip));
				
				stmt.setString(7, JSONHelper.getJSON(execConfig));
				/*
				System.out.println(pathStrip.stripPath(execConfig.getAlgorithmExecutable()));
				System.out.println(pathStrip.stripPath(execConfig.getAlgorithmExecutionDirectory()));
				System.out.println(pathStrip.stripPath(execConfig.getParameterConfigurationSpace().getParamFileName()));
				
				System.out.println(execConfig.isDeterministicAlgorithm());
				System.out.println(execConfig.getAlgorithmMaximumCutoffTime());
				System.out.println(hasher.getHash(execConfig,pathStrip));
				*/
				executePS(stmt);
				ResultSet rs = stmt.getGeneratedKeys();
				//System.out.println(stmt.toString());
				rs.next();
				id = rs.getInt(1);
				
				execConfigToIDMap.put(execConfig,id );
				rs.close();
				stmt.close();
				return id;
			} catch (SQLException e) {
				log.error("Problem processing {}",sb.toString());
				throw new IllegalStateException(e);
			}
		} finally
		{
			if(conn != null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	
			
	
	}
	
	/**
	 * Sets the command line for this
	 * @param command
	 */
	public void setCommand(String command)
	{
		Connection conn = null;
		
		try {
			conn = getConnection();
			if(commandID != -1) throw new IllegalStateException("ID has already been set for this Command");
			
			StringBuffer sb = new StringBuffer();
			
			sb.append("INSERT INTO ").append(TABLE_COMMAND).append(" (CommandString) VALUES (?)");
			
			try {
				PreparedStatement stmt = null;
				
				try {
					stmt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
				
					stmt.setString(1, command);
					executePS(stmt);
					ResultSet rs = stmt.getGeneratedKeys();
					rs.next();
					this.commandID = rs.getInt(1);
				} finally
				{
					if(stmt != null) stmt.close();
				}
				
				
				
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}  finally
		{
			if(conn != null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	private String getHash(AlgorithmRunConfiguration rc, int runPartition)
	{
		return MySQLPersistenceClient.getHash(rc, runPartition, pathStrip);
	}
	
	public static String getHash(AlgorithmRunConfiguration rc, int runPartition, PathStripper pathStrip )
	{
		MessageDigest digest = DigestUtils.getSha1Digest();
		
		try {
			byte[] result = digest.digest( (rc.getProblemInstanceSeedPair().getProblemInstance().getInstanceName() + rc.getProblemInstanceSeedPair().getSeed() +  rc.getCutoffTime() + rc.hasCutoffLessThanMax() + hasher.getHash(rc.getParameterConfiguration()) + hasher.getHash(rc.getAlgorithmExecutionConfiguration(),pathStrip) + runPartition).getBytes("UTF-8"));
			return new String(Hex.encodeHex(result));
	
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Couldn't get Hash for RunConfig and ExecConfig");
		}
	}

	private final AtomicInteger sqlErrorsWhileKillingQueries  = new AtomicInteger(0); 
	
	/**
	 * Wakes up workers that are presently sleeping waiting for tasks
	 * 
	 * Note: There is a race condition implicitly in how this works and we rely on timing to make this work properly :S. You will want to look at how the sleep method works at the same time
	 * as modifying this method: {@link ca.ubc.cs.beta.mysqldbtae.persistence.worker.MySQLPersistenceWorker#sleep(double)}.
	 * 
	 * In short in a loop, we pull all sleeping workers from the database and keep issuing KILL QUERY until we kill enough, or until a second has passed, then we update the list.
	 * 
	 * @param workersToWake  the number of workers to wake up
	 * 
	 */
	public void wakeWorkers(int workersToWake)
	{
		
		Connection conn = null;
		try
		{
			int workersWokenUp = 0;

			try 
			{
				conn = getConnection();
outerloop:
				while(true)
				{
					
					StringBuilder sb = new StringBuilder("SELECT ID FROM INFORMATION_SCHEMA.PROCESSLIST WHERE DB=? AND STATE=\"User Sleep\" AND INFO LIKE ? LIMIT 100");
					
				
						
					
						PreparedStatement stmt = conn.prepareStatement(sb.toString());
				
						stmt.setString(1, this.DATABASE);
						stmt.setString(2,"%"+ this.SLEEP_COMMENT_TEXT + "%");
						long startTime = System.currentTimeMillis();
						
						ResultSet rs = stmt.executeQuery();
						int rowCount = 0;
						while(rs.next())
						{							
							
							int id = rs.getInt(1);
							rowCount++;
							try {
								conn.createStatement().executeQuery("KILL QUERY  " + id);
								workersWokenUp++;
							} catch(SQLException e)
							{
								//Query may be gone
								int errors = sqlErrorsWhileKillingQueries.incrementAndGet();
								workersWokenUp--;
								if(errors == 1)
								{
									log.debug("Exception occurred while killing queries, if this is an Unknown Thread Id issue you can ignore it ", e);
								}
								
							}
							
							if(workersWokenUp >= workersToWake)
							{
								break outerloop;
							}
							
							//After processing for half the cutoff window time, we will actually rechek the database;
							if((System.currentTimeMillis() - startTime) / 1000.0 > (this.MYSQL_SLEEP_CUTOFF_WINDOW/ 2))
							{
								log.info("Too much time has elapsed since we got results, rechecking... {} workers woken up so far.", workersWokenUp );
								break;
							}

						}
						
						if(rowCount == 0)
						{
							break;
						} 
					
						
					
					}	
			} finally
			{
				if(conn != null) conn.close();
				if(workersWokenUp == 0)
				{
					log.trace("Workers woken up {} ",workersWokenUp);
				} else
				{
					log.debug("Workers woken up {} ",workersWokenUp);
				}
			}
		} catch(SQLException e)
		{
			log.error("Failed writing worker Information to database, something very bad is happening");
			throw new IllegalStateException(e);
		}
		
	}
	/**
	 * Invoke this method prior to shutting down the client. 
	 */
	public void shutdown()
	{
		

		if(this.shutdownWorkersOnCompletion)
		{
			log.debug("Notifying workers that they should shutdown immediately");
			
			String query = "UPDATE " + TABLE_WORKERS + " SET upToDate=0,endTime_UPDATEABLE=NOW() WHERE status=\"RUNNING\"";
			
			try {
				PreparedStatement stmt = getConnection().prepareStatement(query);
				int updated = executePSUpdate(stmt);
				
			
				this.wakeWorkers(updated);
				log.info("A total of {} workers were told to shutdown immediately, workers woken up", updated);
				
				
				
			} catch(SQLException e)
			{
				log.error("Couldn't notify workers to shutdown, error while executing", query, e);
				throw new IllegalStateException(e);
			}
		} else
		{
			log.trace("Workers will be left remaining");
		}
		
		

		if(opts.deletePartitionDataOnShutdown)

		{
			log.debug("Deleting all data in {} with runPartition {} ", TABLE_RUNCONFIG, opts.runPartition);
			String query = "DELETE FROM " + TABLE_RUNCONFIG + " WHERE runPartition=" + opts.runPartition;
			try {
				 
				 PreparedStatement stmt = getConnection().prepareStatement(query);
				 executePS(stmt);
						 
				 
				 
			} catch(SQLException e)
			{
				log.error("Couldn't delete data in database, error while executing" + query,e );
				throw new IllegalStateException(e);
			}			
		} else
		{
			log.debug("Data in database will be left alone");
		}
		
		super.shutdown();
		
		
		
		
	}



	
}
