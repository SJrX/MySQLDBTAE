package ca.ubc.cs.beta.mysqldbtae.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
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
	
	protected final String TABLE_COMMAND;
	protected final String TABLE_EXECCONFIG;
	protected final String TABLE_RUNCONFIG;
	
	protected final String TABLE_WORKERS;
	
	
	
	
	
	
	

	
	
	
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Worker ID 
	 */
	

	
	
	


	
	protected Connection getConnection()
	{
		try {
			return DriverManager.getConnection(url,username, password);
		} catch (SQLException e) {
			throw new IllegalStateException("Couldn't get connection");
		}
	}
	
	
	
	
	private final String url;
	private final String username;
	private final String password;
	
	public MySQLPersistence(String host, int port, String databaseName, String username, String password, String pool)
	{
		
		if(pool == null) throw new ParameterException("Must specify a pool name ");
		if(pool.length() > 15) throw new ParameterException("Pool name must be at most 15 characters");
		
		String url="jdbc:mysql://" + host + ":" + port + "/" + databaseName;
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			this.url = url; 
			this.username = username;
			this.password = password;
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
				stmt.close();
			}
			
			br.close();
			log.info("Pool Created");
			 TABLE_COMMAND = "commandTable_" + pool;
			 TABLE_EXECCONFIG = "execConfig_"+ pool;
			 TABLE_RUNCONFIG = "runConfigs_"+ pool;
			 TABLE_WORKERS = "workers_" + pool;
			 
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
			
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
