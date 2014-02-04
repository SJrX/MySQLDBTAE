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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.PacketTooBigException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException;

import ca.ubc.cs.beta.aclib.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.ExistingAlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.RunResult;
import ca.ubc.cs.beta.aclib.algorithmrun.RunningAlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.kill.KillHandler;
import ca.ubc.cs.beta.aclib.algorithmrun.kill.KillableAlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.kill.KillableWrappedAlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.kill.StatusVariableKillHandler;
import ca.ubc.cs.beta.aclib.configspace.ParamConfigurationSpace;
import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration.StringFormat;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.misc.watch.AutoStartStopWatch;
import ca.ubc.cs.beta.aclib.misc.watch.StopWatch;
import ca.ubc.cs.beta.aclib.options.MySQLOptions;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;
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
	private final Map<RunToken, List<RunConfig>> runTokenToRunConfigMap = new ConcurrentHashMap<RunToken, List<RunConfig>>();
	
	/**
	 * Stores a set of completed RunTokens (they can no longer be queried)
	 */
	private final Set<RunToken> completedRunTokens = Collections.newSetFromMap(new ConcurrentHashMap<RunToken,Boolean>());
	
	/**
	 * Stores a mapping from RunConfigUUID to RunConfig
	 */
	private final Map<RunToken, Map<String, RunConfig>> runTokenToStringRCMap = new ConcurrentHashMap<RunToken, Map<String, RunConfig>>();
	
	
	/**
	 * Stores a mapping from RunConfigUUID to runConfigID
	 */
	private final Map<RunToken, Map<String, Integer>> runTokenToStringIDMap = new ConcurrentHashMap<RunToken, Map<String, Integer>>();
	
	
	/**
	 * Stores a mapping from RunConfig to RunConfigUUID
	 */
	private final Map<RunToken, Map<RunConfig, String>> runTokenToRCStringMap = new ConcurrentHashMap<RunToken, Map<RunConfig, String>>();
	
	
	/**
	 * Stores a mapping from runTokenToCompletedRuns 
	 */
	private final Map<RunToken, Map<RunConfig, AlgorithmRun>> runTokenToCompletedRuns = new ConcurrentHashMap<RunToken, Map<RunConfig, AlgorithmRun>>();
	
	private final Map<RunToken, Set<String>> runTokenToKilledJobs = new ConcurrentHashMap<RunToken, Set<String>>();
	
	/**
	 * Stores a mapping from the Run Token to the observer
	 */
	private final Map<RunToken, TargetAlgorithmEvaluatorRunObserver> runTokenToObserverMap = new ConcurrentHashMap<RunToken,TargetAlgorithmEvaluatorRunObserver>();
	
	/**
	 * Stores our current mapping of RunToken to OutstandingRuns
	 */
	private final Map<RunToken,  Map<RunConfig, KillableAlgorithmRun>> runTokenToOutstandingRuns = new ConcurrentHashMap<RunToken,  Map<RunConfig, KillableAlgorithmRun>>();
	
	private final Map<RunToken, Map<RunConfig, KillHandler>> runTokenToKillHandler = new ConcurrentHashMap<RunToken, Map<RunConfig, KillHandler>>();
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	

	/**
	 * Number of RunConfigUUIDs to pool for status at any given time
	 */
	private static final int QUERY_SIZE_LIMIT = 1000;
	
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
		
		

		//this(mysqlOptions.host, mysqlOptions.port,mysqlOptions.databaseName,mysqlOptions.username,mysqlOptions.password,pool, null, batchInsertSize, createTables, runPartition, deletePartitionDataOnShutdown, priority, false, false);

	}



	/**
	 * Returns the runs if completed, null otherwise 
	 * @param token - previously issued Run Token
	 * @return <code>null</code> if the results aren't available yet, the in order sequence of runs if they are
	 */
	public  List<AlgorithmRun> pollRunResults(RunToken token) {
			
			if(token == null) { 
				throw new IllegalStateException("RunToken cannot be null"); 
			}
			
			//=== Lock on the run token
			synchronized(token)
			{

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
				
					Map<RunConfig, AlgorithmRun> userRuns = runTokenToCompletedRuns.get(token);
					Map<RunConfig, KillableAlgorithmRun> outstandingRuns = runTokenToOutstandingRuns.get(token); 
					Map<RunConfig, KillHandler> killHandlers = runTokenToKillHandler.get(token);
					Map<RunConfig, String> runConfigToString = runTokenToRCStringMap.get(token);
					Map<String,RunConfig> stringToRunConfig  = runTokenToStringRCMap.get(token);
					Set<String> killedJobs = runTokenToKilledJobs.get(token);
					
					try {
					
						Set<String> incompleteRuns = runTokenToIncompleteRunsSet.get(token);
						
						while(userRuns.size() < runs)
						{
							StringBuilder sb = new StringBuilder();	
							
							String addlRunData = (this.opts.additionalRunData) ? ", additionalRunData" : "";
							sb.append("SELECT runConfigUUID, status, runResult, runtime, runLength, quality, resultSeed, walltime"+ addlRunData + "  FROM ").append(TABLE_RUNCONFIG).append(" WHERE runConfigUUID IN (");
							
						
							int querySize = 0;
							for(String key : incompleteRuns)
							{
								if(querySize >= QUERY_SIZE_LIMIT)
								{
									break;
								}
								querySize++;
								sb.append("\""+key + "\",");
							}
							
							
							//Get rid of the last comma
							sb.setCharAt(sb.length()-1, ' ');
							
							sb.append(") AND (status=\"COMPLETE\" OR status=\"ASSIGNED\")");
							log.debug("Query was {} ", sb);
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
										
										
										RunResult result = RunResult.valueOf(rs.getString(3));
										double runtime = rs.getDouble(4);
										double runlength = rs.getDouble(5);
										double quality = rs.getDouble(6);
										long seed = rs.getLong(7);
										
										double walltime = rs.getDouble(8);
										
										RunConfig runConfig =  stringToRunConfig.get(rs.getString(1));
										incompleteRuns.remove(rs.getString(1));
										outstandingRuns.remove(runConfig);
										/**
										 * AlgorithmExecutionConfig execConfig, RunConfig runConfig, String result, double wallClockTime
										 */
											
										AlgorithmRun run = new ExistingAlgorithmRun( runConfig, result, runtime, runlength, quality, seed, addlRunDataStr, walltime);
										
										if(run.getRunResult().equals(RunResult.ABORT))
										{
											Object[] args = {rs.getString(1), rs.getString(2), rs.getString(3) } ;
											log.debug("ABORT DETECTED: {} : {} : {}",args );
										}
										userRuns.put(runConfig, run);
										returnedResults++;
										
									} else if(rs.getString(2).equals("ASSIGNED"))
									{
										RunConfig runConfig =  stringToRunConfig.get(rs.getString(1));
										
										double runtime = rs.getDouble(4);
									
										long seed = rs.getLong(7);
										
										double walltime = rs.getDouble(8);
										
										
										outstandingRuns.put(runConfig, new RunningAlgorithmRun(runConfig, runtime,0,0,seed, walltime , killHandlers.get(runConfig)));
									} else
									{	
										throw new IllegalStateException("Must have some new status that we don't know what do with in the database");
									}
								}	
							} finally
							{
								if(stmt != null) stmt.close();
							}
							
							Object args[] =  { token, userRuns.size(), runs };
							log.debug("RunToken {} has {} out of {} runs complete ",args);
							
					
							//Thread.sleep(1000);
							log.trace("Queried for {} got {} results back", querySize, returnedResults);
							
							
							List<String> runsToKill = new ArrayList<String>();
							
							for(Entry<RunConfig, KillHandler> ent : killHandlers.entrySet())
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
								sb.append("UPDATE ").append(TABLE_RUNCONFIG).append(" SET killJob=1, resultSeed=seed, additionalRunData=CONCAT(\"Killed By Client (\",?,\" ) While Status \",status), status=\"COMPLETE\",runResult=\"KILLED\" WHERE (status=\"NEW\" OR status=\"ASSIGNED\") AND runConfigUUID IN (");
								
								for(int i=0; i < Math.min(QUERY_SIZE_LIMIT,runsToKill.size()); i++)
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
							
							
							List<RunConfig> rcs = runTokenToRunConfigMap.get(token);
							
							
							List<KillableAlgorithmRun> runsInProgress = new ArrayList<KillableAlgorithmRun>(rcs.size());
							
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
									runsInProgress.set(i, new KillableWrappedAlgorithmRun(userRuns.get(rcs.get(i))));
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
					
					List<AlgorithmRun> runResults = new ArrayList<AlgorithmRun>(this.runTokenToRunConfigMap.get(token).size());
					
					for(RunConfig rc : this.runTokenToRunConfigMap.get(token))
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

	public RunToken enqueueRunConfigs(List<RunConfig> runConfigs, TargetAlgorithmEvaluatorRunObserver obs)
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
					public void currentStatus(List<? extends KillableAlgorithmRun> runs) {
						//NOOP
					}
					
				};
				
			}
		
			conn = getConnection();
			runTokenToOutstandingRuns.put(runToken, new HashMap<RunConfig,KillableAlgorithmRun>());
			runTokenToObserverMap.put(runToken, obs);
			runTokenToKillHandler.put(runToken, new HashMap<RunConfig, KillHandler>());
			runTokenToStringRCMap.put(runToken,new HashMap<String, RunConfig>());
			runTokenToRCStringMap.put(runToken,new HashMap<RunConfig,String>());
			runTokenToKilledJobs.put(runToken, new HashSet<String>());
			
			Map<RunConfig, KillableAlgorithmRun> outstandingRuns = runTokenToOutstandingRuns.get(runToken);
			Map<RunConfig, KillHandler> killHandlers = runTokenToKillHandler.get(runToken);
			Map<RunConfig, String> runConfigToString = runTokenToRCStringMap.get(runToken);
			Map<String,RunConfig> stringToRunConfig  = runTokenToStringRCMap.get(runToken);
			Map<String, Integer> runConfigToIDMap = new HashMap<String, Integer>();
			
			for(int i=0; (i < Math.ceil((runConfigs.size()/(double) opts.batchInsertSize)));i++)
			{
				
			
				int listLowerBound = i*opts.batchInsertSize;
				int listUpperBound = Math.min((i+1)*opts.batchInsertSize,runConfigs.size());
				
				Object[] args2 =  { i, listLowerBound, listUpperBound, runConfigs.size()};
				log.trace("Lower and Upper Bound {}: ({}-{})  (size: {})",args2);
				
				
				StringBuilder sb = new StringBuilder();
				sb.append("INSERT INTO ").append(TABLE_RUNCONFIG).append(" ( execConfigID, problemInstance, instanceSpecificInformation, seed, cutoffTime, paramConfiguration, cutoffLessThanMax, runConfigUUID, runPartition, priority, additionalRunData) VALUES ");
		
				
				for(int j = listLowerBound; j < listUpperBound; j++ )
				{				
						 sb.append(" (?,?,?,?,?,?,?,?,?,?,''),");
				
				}
		
				sb.setCharAt(sb.length()-1, ' ');
				
				sb.append(" ON DUPLICATE KEY UPDATE priority=\"" +opts.priority+ "\",retryAttempts=0, runtime=IF(killJob=1 OR (status=\"COMPLETE\" AND runResult=\"ABORT\"),0,runtime), status=IF(killJob = 1 OR (status=\"COMPLETE\" AND runResult=\"ABORT\"),\"NEW\",status), killJob=0");

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
							RunConfig rc = runConfigs.get(j);
							
							
							KillHandler kh = new StatusVariableKillHandler();
							
							
							killHandlers.put(rc, kh);
							outstandingRuns.put(rc, new RunningAlgorithmRun(rc, 0,0,0,rc.getProblemInstanceSeedPair().getSeed() ,0, kh));
							String uuid = getHash(rc, opts.runPartition);
							uuids.add(uuid);
							Integer execConfigID = this.getAlgorithmExecutionConfigID(rc.getAlgorithmExecutionConfig());
							stmt.setInt(k++, execConfigID);
							stmt.setString(k++, pathStrip.stripPath(rc.getProblemInstanceSeedPair().getInstance().getInstanceName()));
							if(rc.getProblemInstanceSeedPair().getInstance().getInstanceSpecificInformation().length() > 4000)
							{
								throw new UnsupportedOperationException("MySQL DB Only supports Instance Specific Information of 4K or less in this version, I'm sorry");
							}
							
							stmt.setString(k++, rc.getProblemInstanceSeedPair().getInstance().getInstanceSpecificInformation());
							stmt.setLong(k++, rc.getProblemInstanceSeedPair().getSeed());
							stmt.setDouble(k++, rc.getCutoffTime());
							String configString = rc.getParamConfiguration().getFormattedParamString(StringFormat.ARRAY_STRING_SYNTAX);
							
							
							
							
							if(configString.length() > 2000)
							{
								log.warn("If you get an exception when inserting this row, it is probably because the configuration space string is too long for the table");
							}
							stmt.setString(k++, configString);
							
							stmt.setBoolean(k++, rc.hasCutoffLessThanMax());
							stmt.setString(k++,uuid);
							stmt.setInt(k++, opts.runPartition);
							stmt.setString(k++, opts.priority.name());
							runKeys.add(uuid);
							
							stringToRunConfig.put(uuid, rc);
							runConfigToString.put(rc,uuid);
						}
					
						log.debug("Inserting Rows");
						stopWatch.start();
		
						executePS(stmt);

					} finally 
					{
						if(stmt != null) stmt.close();
					}
					
					double timePerRow = ((listUpperBound - listLowerBound) / (stopWatch.stop() / 1000.0));
					
					Object[] args = { listUpperBound - listLowerBound, stopWatch.time()/1000.0, timePerRow};
					log.debug("Insertion of {} rows took {} seconds {} row / second", args);
					
				
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
			
			this.runTokenToCompletedRuns.put(runToken, new HashMap<RunConfig, AlgorithmRun>());
			this.runTokenToStringIDMap.put(runToken, runConfigToIDMap);
			
			Object[] args3 = { runConfigs.size(), completeInsertionTime.stop() / 1000.0, runConfigs.size() / (completeInsertionTime.stop() /1000.0)}; 
			log.info("Total time to insert {} rows was {} seconds, {} rows / second", args3);
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

	
	private final ConcurrentHashMap<AlgorithmExecutionConfig, Integer> execConfigToIDMap = new ConcurrentHashMap<AlgorithmExecutionConfig, Integer>();
	
	/**
	 * Retrieves the ID for this execution configuration object
	 * 
	 * @param execConfig
	 * @return
	 */
	private int getAlgorithmExecutionConfigID(AlgorithmExecutionConfig execConfig)
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
			
			File f = new File(execConfig.getParamFile().getParamFileName());

			if(!execConfig.getParamFile().equals(ParamConfigurationSpace.getSingletonConfigurationSpace()))
			{
				if(!f.isAbsolute() || !f.exists())
				{
					throw new IllegalStateException("Param File must be created with an absolute file name not the following: " + execConfig.getParamFile().getParamFileName());
				}
			}
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO ").append(TABLE_EXECCONFIG).append(" (algorithmExecutable, algorithmExecutableDirectory, parameterFile, executeOnCluster, deterministicAlgorithm, cutoffTime, algorithmExecutionConfigHashCode) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE algorithmExecutionConfigID=LAST_INSERT_ID(algorithmExecutionConfigID), lastModified = NOW()");
			
			try {
				PreparedStatement stmt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
	
				stmt.setString(1, pathStrip.stripPath(execConfig.getAlgorithmExecutable()));
				stmt.setString(2, pathStrip.stripPath(execConfig.getAlgorithmExecutionDirectory()));
				stmt.setString(3, pathStrip.stripPath(execConfig.getParamFile().getParamFileName()));
				stmt.setBoolean(4, execConfig.isExecuteOnCluster());
				stmt.setBoolean(5, execConfig.isDeterministicAlgorithm());
				stmt.setDouble(6,execConfig.getAlgorithmCutoffTime());
				stmt.setString(7, hasher.getHash(execConfig,pathStrip));
				
				System.out.println(pathStrip.stripPath(execConfig.getAlgorithmExecutable()));
				System.out.println(pathStrip.stripPath(execConfig.getAlgorithmExecutionDirectory()));
				System.out.println(pathStrip.stripPath(execConfig.getParamFile().getParamFileName()));
				System.out.println(execConfig.isExecuteOnCluster());
				System.out.println(execConfig.isDeterministicAlgorithm());
				System.out.println(execConfig.getAlgorithmCutoffTime());
				System.out.println(hasher.getHash(execConfig,pathStrip));
				executePS(stmt);
				ResultSet rs = stmt.getGeneratedKeys();
				System.out.println(stmt.toString());
				rs.next();
				id = rs.getInt(1);
				
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
	
	public String getHash(RunConfig rc, int runPartition )
	{
		MessageDigest digest = DigestUtils.getSha1Digest();
		
		try {
			byte[] result = digest.digest( (rc.getProblemInstanceSeedPair().getInstance().getInstanceName() + rc.getProblemInstanceSeedPair().getSeed() +  rc.getCutoffTime() + rc.hasCutoffLessThanMax() + hasher.getHash(rc.getParamConfiguration()) + hasher.getHash(rc.getAlgorithmExecutionConfig(),pathStrip) + runPartition).getBytes("UTF-8"));
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
				log.info("Workers woken up {} ",workersWokenUp);
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
		

		
		
		
		
	}
}
