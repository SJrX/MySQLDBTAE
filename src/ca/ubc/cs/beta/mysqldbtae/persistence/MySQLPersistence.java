package ca.ubc.cs.beta.mysqldbtae.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.ParameterException;
import com.mysql.jdbc.PacketTooBigException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLTransactionRollbackException;

import ca.ubc.cs.beta.aclib.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.ExistingAlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.RunResult;
import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration;
import ca.ubc.cs.beta.aclib.configspace.ParamConfigurationSpace;
import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration.StringFormat;
import ca.ubc.cs.beta.aclib.configspace.ParamFileHelper;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.misc.options.MySQLConfig;
import ca.ubc.cs.beta.aclib.misc.watch.AutoStartStopWatch;
import ca.ubc.cs.beta.aclib.misc.watch.StopWatch;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.mysqldbtae.util.ACLibHasher;
import ca.ubc.cs.beta.mysqldbtae.util.PathStripper;


public class MySQLPersistence {

	private final Connection conn;
	
	private final String TABLE_COMMAND;
	private  final String TABLE_EXECCONFIG;
	private  final String TABLE_RUNCONFIG;
	private  final String TABLE_ALGORITHMRUNS;
	
	/**
	 * Used to tie all the run requests with a specific execution
	 * Do not change after initialization
	 */
	private int commandID = -1;
	
	/**
	 * Used to tie all the runConfigs to a specific executionConfig
	 * Do not change after initilaziation
	 */
	private int execConfigID = -1;
	
	/**
	 * Stores a mapping of tokens to client, to the actual runConfigIDs in the database.
	 */
	private final Map<RunToken, List<String>> runToIntegerMap = new HashMap<RunToken, List<String>>();
	
	private final Map<RunToken, Set<String>> runTokenToIncompleteRunsSet = new HashMap<RunToken, Set<String>>();
	
	private final Map<RunToken, List<RunConfig>> runTokenToRunConfigMap = new HashMap<RunToken, List<RunConfig>>();
	
	private final Set<RunToken> completedRunTokens = new HashSet<RunToken>();
	
	
	private final Map<String, RunConfig> runConfigIDToRunConfig = new HashMap<String, RunConfig>();
	
	/**
	 * Stores a mapping of RunConfigs to the actual runConfigIDs in the database.
	 */
	private final Map<RunConfig, String> runConfigIDMap = new HashMap<RunConfig, String>();
	
	
	AtomicInteger runTokenKeys = new AtomicInteger();
	
	private static final ACLibHasher hasher = new ACLibHasher();

	private static final int QUERY_SIZE_LIMIT = 1000;

	
	
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Worker ID 
	 */
	private final UUID workerUUID = UUID.randomUUID();

	private AlgorithmExecutionConfig execConfig;
	
	
	private final PathStripper pathStrip;
	
	private final int batchInsertSize;

	
	
	public MySQLPersistence(MySQLConfig mysqlOptions, String pool, int batchInsertSize)
	{
		this(mysqlOptions.host, mysqlOptions.port,mysqlOptions.databaseName,mysqlOptions.username,mysqlOptions.password,pool, null, batchInsertSize);
	}
	
	public MySQLPersistence(MySQLConfig mysqlOptions, String pool, String pathStrip, int batchInsertSize)
	{
		this(mysqlOptions.host, mysqlOptions.port,mysqlOptions.databaseName,mysqlOptions.username,mysqlOptions.password,pool, pathStrip, batchInsertSize);
	}
	
	public MySQLPersistence(String host, String port, String databaseName, String username, String password, String pool, String pathStrip, int batchInsertSize)
	{
		this(host, Integer.valueOf(port), databaseName, username, password,pool,pathStrip, batchInsertSize);
	}
	
	public MySQLPersistence(String host, int port, String databaseName, String username, String password, String pool, String pathStrip, int batchInsertSize)
	{
		
		if(pool == null) throw new ParameterException("Must specify a pool name ");
		if(pool.length() > 15) throw new ParameterException("Pool name must be at most 15 characters");
		
		String url="jdbc:mysql://" + host + ":" + port + "/" + databaseName;
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url,username, password);
			
			
			
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
			sql = sql.replace("ACLIB_POOL_NAME", pool).trim();
			
			
			String[] chunks = sql.split(";");
			
			for(String sqlStatement : chunks)
			{
				if(sqlStatement.trim().length() == 0) continue;
				
				PreparedStatement stmt = conn.prepareStatement(sqlStatement);
				stmt.execute();
			}
			
			
			log.info("Pool Created");
			 TABLE_COMMAND = "commandTable_" + pool;
			 TABLE_EXECCONFIG = "execConfig_"+ pool;
			 TABLE_RUNCONFIG = "runConfigs_"+ pool;
			 TABLE_ALGORITHMRUNS = "algorithmRuns_"+ pool ;
			 
			 log.info("My Worker ID is " + workerUUID.toString());
			
			
			 this.pathStrip = new PathStripper(pathStrip);
			this.batchInsertSize = batchInsertSize;
			//conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED); 
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
			
		}
		
		
	}
	
	/**
	 * Sets the command line for this
	 * @param command
	 */
	public void setCommand(String command)
	{
		if(commandID != -1) throw new IllegalStateException("ID has already been set for this Command");
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("INSERT INTO ").append(TABLE_COMMAND).append(" (CommandString) VALUES (?)");
		
	
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, command);
			stmt.execute();
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			this.commandID = rs.getInt(1);
			
			
			
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		
	}
	

	
	
	public void setAlgorithmExecutionConfig(AlgorithmExecutionConfig execConfig)
	{
		if (commandID == -1) throw new IllegalStateException("Must CommandID not set");
		
		if (execConfigID != -1 ) throw new IllegalStateException("execConfigID is already set");
		
		this.execConfig = execConfig;
		
			File f = new File(execConfig.getParamFile().getParamFileName());
			
			if(!f.isAbsolute() || !f.exists())
			{
				throw new IllegalStateException("Param File must be created with an absolute file name not the following: " + execConfig.getParamFile().getParamFileName());
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO ").append(TABLE_EXECCONFIG).append(" (algorithmExecutable, algorithmExecutableDirectory, parameterFile, executeOnCluster, deterministicAlgorithm, cutoffTime, algorithmExecutionConfigHashCode) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE lastModified = NOW()");
			
			try {
				PreparedStatement stmt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
	
				stmt.setString(1, pathStrip.stripPath(execConfig.getAlgorithmExecutable()));
				stmt.setString(2, pathStrip.stripPath(execConfig.getAlgorithmExecutionDirectory()));
				stmt.setString(3, pathStrip.stripPath(execConfig.getParamFile().getParamFileName()));
				stmt.setBoolean(4, execConfig.isExecuteOnCluster());
				stmt.setBoolean(5, execConfig.isDeterministicAlgorithm());
				stmt.setDouble(6,execConfig.getAlgorithmCutoffTime());
				stmt.setString(7, hasher.getHash(execConfig,pathStrip));
				stmt.execute();
				ResultSet rs = stmt.getGeneratedKeys();
				rs.next();
				execConfigID = rs.getInt(1);
				
				rs.close();
			} catch (SQLException e) {
				log.error("Problem processing {}",sb.toString());
				throw new IllegalStateException(e);
			}
	
			

	}
	
	
	public RunToken enqueueRunConfigs(List<RunConfig> runConfigs)
	{
		
		AutoStartStopWatch completeInsertionTime = new AutoStartStopWatch();
		
		if(execConfigID == -1) throw new IllegalStateException("execConfigID must be set");
	
		if(runConfigs == null || runConfigs.size() == 0) throw new IllegalArgumentException("Must supply atleast one run");

		
		List<String> runKeys = new ArrayList<String>(runConfigs.size());
		
		RunToken runToken = new RunToken(runTokenKeys.incrementAndGet());
		
		

		boolean firstRun = true;
		long startTime = System.currentTimeMillis();
		
		
		for(int i=0; (i < Math.ceil((runConfigs.size()/(double) batchInsertSize)));i++)
		{
			
			if(!firstRun)
			{
				/*
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				*/
				
			}
			int listLowerBound = i*batchInsertSize;
			int listUpperBound = Math.min((i+1)*batchInsertSize,runConfigs.size());
			
			Object[] args2 =  { i, listLowerBound, listUpperBound, runConfigs.size()};
			log.trace("Lower and Upper Bound {}: ({}-{})  (size: {})",args2);
			
			
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT IGNORE INTO ").append(TABLE_RUNCONFIG).append(" ( execConfigID, problemInstance, seed, cutoffTime, paramConfiguration,paramConfigurationHash, cutoffLessThanMax, runConfigUUID) VALUES ");

			
			for(int j = listLowerBound; j < listUpperBound; j++ )
			{				
					 sb.append(" (?,?,?,?,?,?,?,?),");
			
			}
	
			sb.setCharAt(sb.length()-1, ' ');
			
			
			try {
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
				
				log.debug("SQL INSERT: {} ", sb.toString());
				
				log.trace("Preparing for insertion of {} rows into DB", listUpperBound-listLowerBound);

				int k=1;
				for(int j =listLowerBound; j < listUpperBound; j++ )
				{				
					RunConfig rc = runConfigs.get(j);
					
					String uuid = getHash(rc, execConfig);
					
					stmt.setInt(k++, execConfigID);
					stmt.setString(k++, pathStrip.stripPath(rc.getProblemInstanceSeedPair().getInstance().getInstanceName()));
					stmt.setLong(k++, rc.getProblemInstanceSeedPair().getSeed());
					stmt.setDouble(k++, rc.getCutoffTime());
					String configString = rc.getParamConfiguration().getFormattedParamString(StringFormat.ARRAY_STRING_SYNTAX);
					
					
					
					
					if(configString.length() > 2000)
					{
						log.warn("If you get an exception when inserting this row, it is probably because the configuration space string is too long for the table");
					}
					stmt.setString(k++, configString);
					
					
					stmt.setString(k++, hasher.getHash(rc.getParamConfiguration()));
					stmt.setBoolean(k++, rc.hasCutoffLessThanMax());
					stmt.setString(k++,uuid);
				
					runKeys.add(uuid);
					this.runConfigIDToRunConfig.put(uuid, rc);
				}
				
				
				StopWatch stopWatch = new AutoStartStopWatch();
				
				log.debug("Inserting Rows");
				
				boolean insertFailed = true;
				while(insertFailed)
				{
					try {
						stmt.execute();
						insertFailed = false;
					} catch(MySQLTransactionRollbackException e)
					{
						log.info("Deadlock");
					}
				}
					
				
				double timePerRow = ((listUpperBound - listLowerBound) / (stopWatch.stop() / 1000.0));
				
				Object[] args = { listUpperBound - listLowerBound, stopWatch.time()/1000.0, timePerRow};
				log.debug("Insertion of {} rows took {} seconds {} row / second", args);
				
				
				
				
				
				
			
				//return runToken;
			} catch(PacketTooBigException e)
			{
				throw new IllegalStateException("SQL Error Occured, Try lowering the Batch Size (probably MYSQL_BATCH_INSERT_SIZE or a CLI option)", e);
			} catch (SQLException e) {
				throw new IllegalStateException("SQL Error", e);
			}
		}
		
		this.runTokenToRunConfigMap.put(runToken, runConfigs);
		this.runToIntegerMap.put(runToken, runKeys);
		
		
		Set<String> unfinishedRunConfigs = new HashSet<String>();
		
		unfinishedRunConfigs.addAll(runKeys);
		this.runTokenToIncompleteRunsSet.put(runToken, unfinishedRunConfigs);
		
		
		Object[] args3 = { runConfigs.size(), completeInsertionTime.stop() / 1000.0, runConfigs.size() / (completeInsertionTime.stop() /1000.0)}; 
		log.info("Total time to insert {} rows was {} seconds, {} rows / second", args3);
		return runToken;
		
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
			
			PreparedStatement stmt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
			try {
				stmt.execute();
			} catch(MySQLTransactionRollbackException e)
			{
				log.info("Deadlock detected, trying again");
				return getRuns(n);
			}
			//if(true) return Collections.emptyMap();
			
			sb = new StringBuffer();
			//
			sb.append("SELECT runConfigUUID , execConfigID, problemInstance, seed, cutoffTime, paramConfiguration, cutoffLessThanMax FROM ").append(TABLE_RUNCONFIG);
			sb.append(" WHERE status=\"ASSIGNED\" AND workerUUID=\"" + workerUUID.toString() + "\"");
			
	
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
				long seed = rs.getLong(4);
				double cutoffTime = rs.getDouble(5);
				String paramConfiguration = rs.getString(6);
				boolean cutoffLessThanMax = rs.getBoolean(7);
								
				
				ProblemInstance pi = new ProblemInstance(problemInstance);
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
			
			return myMap;
			
			
			
			
			
		} catch (SQLException e) {
			throw new IllegalStateException("SQL Error", e);
		}
		
	}
	
	Map<Integer, AlgorithmExecutionConfig> execConfigMap = new HashMap<Integer, AlgorithmExecutionConfig>();
	
	private AlgorithmExecutionConfig getAlgorithmExecutionConfig(int execConfigID) throws SQLException {
		
		if(!execConfigMap.containsKey(execConfigID)) 
		{
		 StringBuilder sb = new StringBuilder();
		 sb.append("SELECT algorithmExecutable, algorithmExecutableDIrectory, parameterFile, executeOnCluster, deterministicAlgorithm, cutoffTime FROM ").append(TABLE_EXECCONFIG).append("  WHERE algorithmExecutionConfigID = " + execConfigID);
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
			
		rs.close();
		}
		
		
		return execConfigMap.get(execConfigID);
		
		
		
	}
	

	
	public void setRunResults(List<AlgorithmRun> runResult)
	{
	
		
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET runResult=?, runLength=?, quality=?, result_seed=?, result_line=?, runtime=?, additional_run_data=?, status='COMPLETE'  WHERE runConfigUUID=?");
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
			
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
			
			
		}catch(SQLException e)
		{
			throw new IllegalStateException("SQL Error", e);
		}
		
	}

	private void setAbortRun(String runConfigUUID)
	{
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET runResult='ABORT', status='COMPLETE'  WHERE runConfigUUID=?");
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
			stmt.setString(1, runConfigUUID);
			stmt.execute();
			
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
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
			stmt.execute();
			
		}catch(SQLException e)
		{
			log.error("Failed writing abort to database, something very bad is happening");
			
			throw new IllegalStateException(e);
		}
		

	}
	

	
	public List<AlgorithmRun> getRunResults(RunToken token) {
		
		if(token == null) { 
			throw new IllegalStateException("RunToken cannot be null"); 
		}
		
		
		if(completedRunTokens.contains(token))
		{
			throw new IllegalStateException("RunToken is no longer valid");
		}
		
		if(runToIntegerMap.get(token) == null)
		{
			throw new IllegalStateException("RunToken doesn't exist in mapping");
		}
	
		
		int runs = runToIntegerMap.get(token).size(); 
		
		
		
		
		Map<RunConfig, AlgorithmRun> userRuns = new HashMap<RunConfig, AlgorithmRun>();
		
		try {
		
		
		
			
			Set<String> incompleteRuns = runTokenToIncompleteRunsSet.get(token);
			
			while(userRuns.size() < runs)
			{
				StringBuilder sb = new StringBuilder();	
				sb.append("SELECT runConfigUUID, result_line,status FROM ").append(TABLE_RUNCONFIG).append(" WHERE runConfigUUID IN (");
				
				
				int querySize = 0;
				for(String key : incompleteRuns)
				{
					if(querySize >= QUERY_SIZE_LIMIT)
					{
						break;
					}
					sb.append("\""+key + "\",");
				}
				
				
				//Get rid of the last comma
				sb.setCharAt(sb.length()-1, ' ');
				
				sb.append(") AND status=\"COMPLETE\"");
				
				PreparedStatement stmt = conn.prepareStatement(sb.toString());
				//log.info("Query: {} ", sb);
				ResultSet rs = stmt.executeQuery();
				
				
		
				while(rs.next())
				{				
					String resultLine = rs.getString(2);
					RunConfig runConfig = this.runConfigIDToRunConfig.get(rs.getString(1));
					incompleteRuns.remove(rs.getString(1));
					/**
					 * AlgorithmExecutionConfig execConfig, RunConfig runConfig, String result, double wallClockTime
					 */
						
					AlgorithmRun run = new ExistingAlgorithmRun(execConfig, runConfig, resultLine, 0.0);
					
					if(run.getRunResult().equals(RunResult.ABORT))
					{
						Object[] args = {rs.getString(1), rs.getString(2), rs.getString(3) } ;
						log.info("ABORT DETECTED: {} : {} : {}",args );
					}
					userRuns.put(runConfig, run);
				}	
				
				
				Object args[] =  { token, userRuns.size(), runs };
				log.info("RunToken {} has {} out of {} runs complete ",args);
				Thread.sleep(1000);
				
				
				
			}
			
			
			
			
		} catch(SQLException e)
		{
			throw new IllegalStateException("SQL Error", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return Collections.emptyList();
		}
			
		
		List<AlgorithmRun> runResults = new ArrayList<AlgorithmRun>(this.runTokenToRunConfigMap.get(token).size());
		
		for(RunConfig rc : this.runTokenToRunConfigMap.get(token))
		{
			runResults.add(userRuns.get(rc));
		}
	

		completedRunTokens.add(token);
		runToIntegerMap.remove(token);
		runTokenToIncompleteRunsSet.remove(token);
		runTokenToRunConfigMap.remove(token);
		
		
		
		return runResults;
		
	}

	 
	
	public String getHash(RunConfig rc, AlgorithmExecutionConfig execConfig )
	{
		MessageDigest digest = DigestUtils.getSha1Digest();
		
		try {
			byte[] result = digest.digest( (rc.getProblemInstanceSeedPair().getInstance().getInstanceName() + rc.getProblemInstanceSeedPair().getSeed() +  rc.getCutoffTime() + rc.hasCutoffLessThanMax() + hasher.getHash(rc.getParamConfiguration()) + hasher.getHash(execConfig,pathStrip)).getBytes("UTF-8"));
			return new String(Hex.encodeHex(result));

		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Couldn't get Hash for RunConfig and ExecConfig");
		}
	}
	
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
	
	
}
