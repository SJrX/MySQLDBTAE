package ca.ubc.cs.beta.mysqldbtae.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.ParameterException;

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
	
	private final Map<String, RunConfig> runConfigIDToRunConfig = new HashMap<String, RunConfig>();
	
	/**
	 * Stores a mapping of RunConfigs to the actual runConfigIDs in the database.
	 */
	private final Map<RunConfig, String> runConfigIDMap = new HashMap<RunConfig, String>();
	
	
	AtomicInteger runTokenKeys = new AtomicInteger();
	
	private static final ACLibHasher hasher = new ACLibHasher();

	
	
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Worker ID 
	 */
	private final UUID workerUUID = UUID.randomUUID();

	private AlgorithmExecutionConfig execConfig;
	
	
	private final PathStripper pathStrip;
	public MySQLPersistence(MySQLConfig mysqlOptions, String pool)
	{
		this(mysqlOptions.host, mysqlOptions.port,mysqlOptions.databaseName,mysqlOptions.username,mysqlOptions.password,pool, null);
	}
	
	public MySQLPersistence(MySQLConfig mysqlOptions, String pool, String pathStrip)
	{
		this(mysqlOptions.host, mysqlOptions.port,mysqlOptions.databaseName,mysqlOptions.username,mysqlOptions.password,pool, pathStrip);
	}
	
	public MySQLPersistence(String host, String port, String databaseName, String username, String password, String pool, String pathStrip)
	{
		this(host, Integer.valueOf(port), databaseName, username, password,pool,pathStrip);
	}
	
	public MySQLPersistence(String host, int port, String databaseName, String username, String password, String pool, String pathStrip)
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
		
		if(execConfigID == -1) throw new IllegalStateException("execConfigID must be set");
	
		if(runConfigs == null || runConfigs.size() == 0) throw new IllegalArgumentException("Must supply atleast one run");
		
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT IGNORE INTO ").append(TABLE_RUNCONFIG).append(" ( execConfigID, problemInstance, seed, cutoffTime, paramConfiguration,paramConfigurationHash, cutoffLessThanMax, runConfigUUID) VALUES ");
		
		
	
		/**
		 * 	
		*StringBuilder sb = new StringBuilder();*/
		//sb.append("INSERT IGNORE INTO ").append(TABLE_RUNCONFIG).append(" ( execConfigID, problemInstance, seed, cutoffTime, paramConfiguration,paramConfigurationHash, cutoffLessThanMax, runConfigUUID) VALUES ");
		
		/*
		for(RunConfig rc : runConfigs)
		{
			 sb.append(" (?,?,?,?,?,?,?,?),");
		}
		*/
		//sb.setCharAt(sb.length()-1, ' ');
		
     
		
	
		try {
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
			List<String> runKeys = new ArrayList<String>(runConfigs.size());
			
			log.info("Preparing for insertion of {} rows into DB", runConfigs.size());
			int rowsInserted = 0;
			
			
			
			
			
			for(RunConfig rc : runConfigs)
			{
				int i=1;
					
				String uuid = getHash(rc, execConfig);
				
				stmt.setInt(i++, execConfigID);
				stmt.setString(i++, pathStrip.stripPath(rc.getProblemInstanceSeedPair().getInstance().getInstanceName()));
				stmt.setLong(i++, rc.getProblemInstanceSeedPair().getSeed());
				stmt.setDouble(i++, rc.getCutoffTime());
				stmt.setString(i++, rc.getParamConfiguration().getFormattedParamString(StringFormat.NODB_SYNTAX));
				stmt.setString(i++, hasher.getHash(rc.getParamConfiguration()));
				stmt.setBoolean(i++, rc.hasCutoffLessThanMax());
				stmt.setString(i++,uuid);
				stmt.addBatch();
				
				runKeys.add(uuid);
				this.runConfigIDToRunConfig.put(uuid, rc);
			}
			
			StopWatch stopWatch = new AutoStartStopWatch();
			
			log.info("Inserting Rows");
			stmt.executeBatch();
				//stmt.execute();
			
				double timePerRow = (runConfigs.size() / (stopWatch.stop() / 1000.0));
				Object[] args = { runConfigs.size(), stopWatch.time()/1000.0, timePerRow};
				log.info("Insertion of {} rows took {} seconds {} seconds / row", args);
				RunToken runToken = new RunToken(runTokenKeys.incrementAndGet());
				this.runToIntegerMap.put(runToken, runKeys);
				return runToken;
		} catch (SQLException e) {
			throw new IllegalStateException("SQL Error", e);
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
			sb.append("UPDATE ").append(TABLE_RUNCONFIG).append( " A JOIN ( SELECT runConfigUUID FROM ").append(TABLE_RUNCONFIG).append(" WHERE status=\"NEW\" AND (workerUUID IS NULL OR workerUUID <> \""+ workerUUID.toString() +"\") ORDER BY priority DESC, runConfigUUID DESC LIMIT " + n +  "  FOR UPDATE) B ON B.runConfigUUID=A.runConfigUUID SET status=\"ASSIGNED\", workerUUID=\"" + workerUUID.toString() + "\"");
					
					
			System.out.println(sb.toString());
			
			PreparedStatement stmt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
			stmt.executeUpdate();
		
			
			sb = new StringBuffer();
			sb.append("SELECT runConfigUUID, execConfigID, problemInstance, seed, cutoffTime, paramConfiguration, cutoffLessThanMax FROM ").append(TABLE_RUNCONFIG);
			sb.append(" WHERE status=\"ASSIGNED\" AND workerUUID=\"" + workerUUID.toString() + "\"");
			
	
			stmt = conn.prepareStatement(sb.toString());
			ResultSet rs = stmt.executeQuery();
			
			Map<AlgorithmExecutionConfig, List<RunConfig>> myMap = new LinkedHashMap<AlgorithmExecutionConfig, List<RunConfig>>();
			
		
			while(rs.next())
			{
				
				String uuid = rs.getString(1);
				log.debug("Assigned Run {} ", uuid);
				
				AlgorithmExecutionConfig execConfig = getAlgorithmExecutionConfig(rs.getInt(2));
				String problemInstance = rs.getString(3);
				long seed = rs.getLong(4);
				double cutoffTime = rs.getDouble(5);
				String paramConfiguration = rs.getString(6);
				boolean cutoffLessThanMax = rs.getBoolean(7);
				
				
				ProblemInstance pi = new ProblemInstance(problemInstance);
				ProblemInstanceSeedPair pisp = new ProblemInstanceSeedPair(pi,seed);
				ParamConfiguration config = execConfig.getParamFile().getConfigurationFromString(paramConfiguration, StringFormat.NODB_SYNTAX);
				
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
		StringBuilder sb = new StringBuilder("INSERT INTO ").append(TABLE_ALGORITHMRUNS).append(" ( runConfigUUID, runResult, runLength, quality, result_seed, result_line, runtime, additional_run_data) VALUES (?,?,?,?,?,?,?,?)");
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
			
			for(AlgorithmRun run : runResult)
			{	
				String runConfigUUID = runConfigIDMap.get(run.getRunConfig());
				try {
					
					
					stmt.setString(1,runConfigUUID);
					stmt.setString(2,run.getRunResult().name());
					stmt.setDouble(3, run.getRunLength());
					stmt.setDouble(4, run.getQuality());
					stmt.setLong(5,run.getResultSeed());
					stmt.setString(6, run.getResultLine());
					stmt.setDouble(7, run.getRuntime());
					stmt.setString(8, run.getAdditionalRunData());
					
					stmt.execute();
				} catch(SQLException e)
				{
					log.error("SQL Exception while saving run {}", run);
					log.error("Error occured", e);
					log.error("Saving ABORT Manually");
					setAbortRun(runConfigUUID);
				}
				setRunCompleted(runConfigUUID);
				
			} 
			
			
		}catch(SQLException e)
		{
			throw new IllegalStateException("SQL Error", e);
		}
		
	}

	private void setAbortRun(String runConfigUUID)
	{
		StringBuilder sb = new StringBuilder("INSERT INTO ").append(TABLE_ALGORITHMRUNS).append(" (runConfigUUID) VALUES (\"" + runConfigUUID +"\")");
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
			stmt.execute();
			
		}catch(SQLException e)
		{
			log.error("Failed writing abort to database, something very bad is happening");
			throw new IllegalStateException(e);
		}
		
			
	}
	
	private void setRunCompleted(String runConfigUUID)
	{
		StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_RUNCONFIG).append(" SET status=\"COMPLETE\" WHERE runConfigUUID=\"" + runConfigUUID + "\"");
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
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
		
		StringBuilder sb = new StringBuilder();
		
		
		sb.append("SELECT COUNT(*) FROM ").append(TABLE_RUNCONFIG).append(" WHERE runConfigUUID IN (");
		
		int runs = runToIntegerMap.get(token).size(); 
		
		
		
		
		for(String key : runToIntegerMap.get(token))
		{
		
			sb.append("\""+key + "\",");
		}
		
		//Get rid of the last comma
		sb.setCharAt(sb.length()-1, ' ');
		
		sb.append(") AND status=\"COMPLETE\"");
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
			
			boolean done=false;
			while(!done)
			{
				ResultSet rs = stmt.executeQuery();
				
				
				rs.next();
				int completedRuns = rs.getInt(1);
				
				if(completedRuns == runs) 
				{
					log.info("All runs completed for RunToken {}", token);
					done = true;
					
				} else
				{
					Object[] args = { token, completedRuns, runs };
					log.info("RunToken {} currently has {} out of {} runs completed ",args);
					Thread.sleep(2500);
				}
			}
			
			
			
			
		} catch(SQLException e)
		{
			throw new IllegalStateException("SQL Error", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return Collections.emptyList();
		}
			
		
		sb = new StringBuilder();
		sb.append("SELECT runConfigUUID, result_line FROM ").append(TABLE_ALGORITHMRUNS).append(" WHERE runConfigUUID IN (");
		
		runs = runToIntegerMap.get(token).size(); 
		
		for(String key : runToIntegerMap.get(token))
		{
		
			sb.append("\"" + key + "\",");
		}
		
		//Get rid of the last comma
		sb.setCharAt(sb.length()-1, ' ');
		//Ensures that the order is correct
		sb.append(") ORDER BY runConfigUUID");
		List<AlgorithmRun> userRuns = new ArrayList<AlgorithmRun>(runs);
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
			ResultSet rs = stmt.executeQuery();
			
			
			while(rs.next())
			{				
				String resultLine = rs.getString(2);
				RunConfig runConfig = this.runConfigIDToRunConfig.get(rs.getString(1));
				/**
				 * AlgorithmExecutionConfig execConfig, RunConfig runConfig, String result, double wallClockTime
				 */
				
				AlgorithmRun run = new ExistingAlgorithmRun(execConfig, runConfig, resultLine, 0.0);
				userRuns.add(run);
			}
		 
		 
		
		
			
			
		} catch(SQLException e)
		{
			throw new IllegalStateException("SQL Error",e);
		}
		
		return userRuns;
		
		
		
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
	
	
	
}
