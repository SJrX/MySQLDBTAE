package ca.ubc.cs.beta.mysqldbtae.persistence.client;

import java.io.File;
import java.io.UnsupportedEncodingException;
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
import java.util.Set;
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
import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration.StringFormat;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.misc.options.MySQLConfig;
import ca.ubc.cs.beta.aclib.misc.watch.AutoStartStopWatch;
import ca.ubc.cs.beta.aclib.misc.watch.StopWatch;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.mysqldbtae.persistence.MySQLPersistence;
import ca.ubc.cs.beta.mysqldbtae.util.ACLibHasher;
import ca.ubc.cs.beta.mysqldbtae.util.PathStripper;

public class MySQLPersistenceClient extends MySQLPersistence {

	
	/**
	 * Stores a mapping of tokens to client, to the actual runConfigUUIDs in the database.
	 */
	private final Map<RunToken, List<String>> runToIntegerMap = new HashMap<RunToken, List<String>>();
	
	/**
	 * Stores a mapping of tokens to the set of RunConfigUUIDs that are incomplete.
	 */
	private final Map<RunToken, Set<String>> runTokenToIncompleteRunsSet = new HashMap<RunToken, Set<String>>();
	
	/**
	 * Stores a mapping of tokens to RunConfigs that are associated with it.
	 */
	private final Map<RunToken, List<RunConfig>> runTokenToRunConfigMap = new HashMap<RunToken, List<RunConfig>>();
	
	/**
	 * Stores a set of completed RunTokens (they can no longer be queried)
	 */
	private final Set<RunToken> completedRunTokens = new HashSet<RunToken>();
	
	
	/**
	 * Stores a mapping from RunConfigUUID to RunConfig
	 */
	private final Map<String, RunConfig> runConfigIDToRunConfig = new HashMap<String, RunConfig>();
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	

	/**
	 * Number of RunConfigUUIDs to pool for status at any given time
	 */
	private static final int QUERY_SIZE_LIMIT = 1000;

	/**
	 * Execution Config associated with this Persistence Client
	 */
	private AlgorithmExecutionConfig execConfig;
	
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
	 * Object that will strip the path from a URL
	 */
	private final PathStripper pathStrip;
	
	/**
	 * Number of items to insert in batch at one time
	 */
	private final int batchInsertSize;
	
	/**
	 * RunToken Identifiers are drawn from this pool
	 */
	AtomicInteger runTokenKeys = new AtomicInteger();

	/**
	 * Computes hashes for ACLib Entities
	 */
	private static final ACLibHasher hasher = new ACLibHasher();

	
	public MySQLPersistenceClient(MySQLConfig mysqlOptions, String pool, int batchInsertSize)
	{
		this(mysqlOptions.host, mysqlOptions.port,mysqlOptions.databaseName,mysqlOptions.username,mysqlOptions.password,pool, null, batchInsertSize);
	}
	
	public MySQLPersistenceClient(MySQLConfig mysqlOptions, String pool, String pathStrip, int batchInsertSize)
	{
		this(mysqlOptions.host, mysqlOptions.port,mysqlOptions.databaseName,mysqlOptions.username,mysqlOptions.password,pool, pathStrip, batchInsertSize);
	}
	
	public MySQLPersistenceClient(String host, String port, String databaseName, String username, String password, String pool, String pathStrip, int batchInsertSize)
	{
		this(host, Integer.valueOf(port), databaseName, username, password,pool,pathStrip, batchInsertSize);
	}
	

	public MySQLPersistenceClient(String host, int port,
			String databaseName, String username, String password, String pool,
			String pathStrip, int batchInsertSize) {
		super(host, port, databaseName, username, password, pool);
		this.pathStrip = new PathStripper(pathStrip);
		this.batchInsertSize = batchInsertSize;
	
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
					
					PreparedStatement stmt = getConnection().prepareStatement(sb.toString());
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

	public RunToken enqueueRunConfigs(List<RunConfig> runConfigs)
	{
		
		AutoStartStopWatch completeInsertionTime = new AutoStartStopWatch();
		
		if(execConfigID == -1) throw new IllegalStateException("execConfigID must be set");
	
		if(runConfigs == null || runConfigs.size() == 0) throw new IllegalArgumentException("Must supply atleast one run");
	
		
		List<String> runKeys = new ArrayList<String>(runConfigs.size());
		
		RunToken runToken = new RunToken(runTokenKeys.incrementAndGet());
		
		
	
		boolean firstRun = true;
		
		
		
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
				PreparedStatement stmt = getConnection().prepareStatement(sb.toString());
				
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


	public void setAlgorithmExecutionConfig(AlgorithmExecutionConfig execConfig)
	{
		if (commandID == -1) throw new IllegalStateException("Must CommandID not set");
		
		if (execConfigID != -1 ) throw new IllegalStateException("execConfigID is already set");
		
		if(execConfig == null)
		{
			throw new IllegalArgumentException("execution configuration cannot be null");
		}
		this.execConfig = execConfig;
		
			File f = new File(execConfig.getParamFile().getParamFileName());
			
			if(!f.isAbsolute() || !f.exists())
			{
				throw new IllegalStateException("Param File must be created with an absolute file name not the following: " + execConfig.getParamFile().getParamFileName());
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO ").append(TABLE_EXECCONFIG).append(" (algorithmExecutable, algorithmExecutableDirectory, parameterFile, executeOnCluster, deterministicAlgorithm, cutoffTime, algorithmExecutionConfigHashCode) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE lastModified = NOW()");
			
			try {
				PreparedStatement stmt = getConnection().prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
	
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
			PreparedStatement stmt = getConnection().prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, command);
			stmt.execute();
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			this.commandID = rs.getInt(1);
			
			
			
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		
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
	
	Connection connSQL  = null;
	protected Connection getConnection()
	{
		if(connSQL == null)
		{
			connSQL = super.getConnection();
		}
		return connSQL;
	}
	

}
