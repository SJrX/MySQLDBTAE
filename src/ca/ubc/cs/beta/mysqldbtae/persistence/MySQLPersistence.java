package ca.ubc.cs.beta.mysqldbtae.persistence;

import java.io.File;
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
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aclib.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration;
import ca.ubc.cs.beta.aclib.configspace.ParamConfigurationSpace;
import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration.StringFormat;
import ca.ubc.cs.beta.aclib.configspace.ParamFileHelper;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.misc.options.MySQLConfig;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.mysqldbtae.util.ACLibHasher;


public class MySQLPersistence {

	private final Connection conn;
	
	private static final String TABLE_COMMAND = "commandTable";
	private static final String TABLE_EXECCONFIG = "execConfig";
	private static final String TABLE_RUNCONFIG = "runConfigs";
	
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
	private final Map<RunToken, List<Integer>> runToIntegerMap = new HashMap<RunToken, List<Integer>>();
	
	
	/**
	 * Stores a mapping of RunConfigs to the actual runConfigIDs in the database.
	 */
	private final Map<RunConfig, Integer> runConfigIDMap = new HashMap<RunConfig, Integer>();
	
	
	AtomicInteger runTokenKeys = new AtomicInteger();
	
	private static final ACLibHasher hasher = new ACLibHasher();
	
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Worker ID 
	 */
	private final UUID workerUUID = UUID.randomUUID();
	
	public MySQLPersistence(MySQLConfig mysqlOptions)
	{
		this(mysqlOptions.host, mysqlOptions.port,mysqlOptions.databaseName,mysqlOptions.username,mysqlOptions.password);
	}
	
	public MySQLPersistence(String host, String port, String databaseName, String username, String password)
	{
		this(host, Integer.valueOf(port), databaseName, username, password);
	}
	
	public MySQLPersistence(String host, int port, String databaseName, String username, String password)
	{
		
		
		String url="jdbc:mysql://" + host + ":" + port + "/" + databaseName;
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url,username, password);
			
		} catch (Exception e) {
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
		
			
			File f = new File(execConfig.getParamFile().getParamFileName());
			
			if(!f.isAbsolute() || !f.exists())
			{
				throw new IllegalStateException("Param File must be created with an absolute file name not the following: " + execConfig.getParamFile().getParamFileName());
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO ").append(TABLE_EXECCONFIG).append(" (algorithmExecutable, algorithmExecutableDirectory, parameterFile, executeOnCluster, deterministicAlgorithm, cutoffTime, algorithmExecutionConfigHashCode) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE lastModified = NOW()");
			
			try {
				PreparedStatement stmt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
	
				stmt.setString(1, execConfig.getAlgorithmExecutable());
				stmt.setString(2, execConfig.getAlgorithmExecutionDirectory());
				stmt.setString(3, execConfig.getParamFile().getParamFileName());
				stmt.setBoolean(4, execConfig.isExecuteOnCluster());
				stmt.setBoolean(5, execConfig.isDeterministicAlgorithm());
				stmt.setDouble(6,execConfig.getAlgorithmCutoffTime());
				stmt.setString(7, this.hasher.getHash(execConfig));
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
		sb.append("INSERT INTO ").append(TABLE_RUNCONFIG).append(" ( execConfigID, problemInstance, seed, cutoffTime, paramConfiguration,paramConfigurationHash, cutoffLessThanMax) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE lastModified = NOW()");
		
	
		try {
			PreparedStatement stmt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
			List<Integer> runKeys = new ArrayList<Integer>(runConfigs.size());
			
			for(RunConfig rc : runConfigs)
			{
				
					
				
				stmt.setInt(1, execConfigID);
				stmt.setString(2, rc.getProblemInstanceSeedPair().getInstance().getInstanceName());
				stmt.setLong(3, rc.getProblemInstanceSeedPair().getSeed());
				stmt.setDouble(4, rc.getCutoffTime());
				stmt.setString(5, rc.getParamConfiguration().getFormattedParamString(StringFormat.NODB_SYNTAX));
				stmt.setString(6, hasher.getHash(rc.getParamConfiguration()));
				stmt.setBoolean(7, rc.hasCutoffLessThanMax());
				stmt.execute();
				
				ResultSet rs = stmt.getGeneratedKeys();
				rs.next();
				runKeys.add(rs.getInt(1));
				rs.close();
			}
			
			
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
			sb.append("UPDATE ").append(TABLE_RUNCONFIG).append(" SET status=\"ASSIGNED\", workerUUID=\"" + workerUUID.toString() +"\"  WHERE status=\"NEW\" ORDER BY priority DESC, runConfigID ASC LIMIT " + n);
			System.out.println(sb.toString());
			PreparedStatement stmt = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
			stmt.executeUpdate();
			
			
			sb = new StringBuffer();
			sb.append("SELECT runConfigID, execConfigID, problemInstance, seed, cutoffTime, paramConfiguration, cutoffLessThanMax FROM ").append(TABLE_RUNCONFIG);
			sb.append(" WHERE status=\"ASSIGNED\" AND workerUUID=\"" + workerUUID.toString() + "\"");
			
			
			stmt = conn.prepareStatement(sb.toString());
			ResultSet rs = stmt.executeQuery();
			
			Map<AlgorithmExecutionConfig, List<RunConfig>> myMap = new LinkedHashMap<AlgorithmExecutionConfig, List<RunConfig>>();
			
			
			while(rs.next())
			{
				int runConfigID = rs.getInt(1);
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
				runConfigIDMap.put(rc, runConfigID);
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
		 sb.append("SELECT algorithmExecutable, algorithmExecutableDIrectory, parameterFile, executeOnCluster, deterministicAlgorithm, cutoffTime FROM execConfig WHERE algorithmExecutionConfigID = " + execConfigID);
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
		StringBuilder sb = new StringBuilder("INSERT INTO algorithmRuns (runConfigID, runResult, runLength, quality, result_seed, result_line, runtime, additional_run_data) VALUES (?,?,?,?,?,?,?,?)");
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
			
			for(AlgorithmRun run : runResult)
			{	
				int runConfigID = runConfigIDMap.get(run.getRunConfig());
				try {
					
					
					stmt.setInt(1,runConfigID);
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
					setAbortRun(runConfigID);
				}
				setRunCompleted(runConfigID);
				
			} 
			
			
		}catch(SQLException e)
		{
			throw new IllegalStateException("SQL Error", e);
		}
		
	}

	private void setAbortRun(int runConfigID)
	{
		StringBuilder sb = new StringBuilder("INSERT INTO algorithmRuns (runConfigID) VALUES (" + runConfigID +")");
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sb.toString());
			stmt.execute();
			
		}catch(SQLException e)
		{
			log.error("Failed writing abort to database, something very bad is happening");
			throw new IllegalStateException(e);
		}
		
			
	}
	
	private void setRunCompleted(int runConfigID)
	{
		StringBuilder sb = new StringBuilder("UPDATE runConfigs SET status=\"COMPLETE\" WHERE runConfigID=" + runConfigID );
		
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
		sb.append("SELECT COUNT(*) FROM ").append(TABLE_RUNCONFIG).append(" WHERE runConfigID IN (");
		
		int runs = runToIntegerMap.get(token).size(); 
		
		for(Integer key : runToIntegerMap.get(token))
		{
		
			sb.append(key + ",");
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
			
		
		
		return null;
		
		
		
	}

	 
	
	
}
