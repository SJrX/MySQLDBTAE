package ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.TreeMap;

import ca.ubc.cs.beta.aeatk.misc.file.HomeFileUtils;
import ca.ubc.cs.beta.aeatk.misc.jcommander.validator.FixedPositiveInteger;
import ca.ubc.cs.beta.aeatk.misc.options.OptionLevel;
import ca.ubc.cs.beta.aeatk.misc.options.UsageTextField;
import ca.ubc.cs.beta.aeatk.options.AbstractOptions;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterFile;

@UsageTextField(title="MySQL Target Algorithm Evaluator Options", description="Options that control the MySQL Target Algorithm Evaluator", level=OptionLevel.INTERMEDIATE)
public class MySQLTargetAlgorithmEvaluatorOptions extends AbstractOptions{

	@UsageTextField(defaultValues="~/.aeatk/mysqldbtae.opt")
	@Parameter(names="--mysqlTaeDefaultsFile", description="file that contains default settings for MySQL")
	@ParameterFile(ignoreFileNotExists = true) 
	public File smacDefaults = HomeFileUtils.getHomeFile(".aeatk" + File.separator  + "mysqldbtae.opt");
	
	@Parameter(names="--mysqldbtae-pool", description="Pool to take tasks from")
	public String pool;
	
	@UsageTextField(defaultValues="Will attempt to automatically create it if it isn't there")
	@Parameter(names="--mysqldbtae-create-pool", description="Attempt to create the tables for the database")
	public Boolean createTables = null;
	
	@UsageTextField(level=OptionLevel.ADVANCED)
	@Parameter(names="--mysqldbtae-batch-insert-size", description="The number of runs to batch into a single insert", validateWith=FixedPositiveInteger.class)
	public int batchInsertSize = 500;
	
	@Parameter(names="--mysqldbtae-run-partition", description="Sets which partition the runs should exist in (existing runs in the database will only be used if the runPartition matches) ")
	public int runPartition = 0;
	
	@Parameter(names="--mysqldbtae-delete-partition-on-shutdown", description="If set to true, we will delete all data in the run partition (this requires that the partition be set to something greater than zero)")
	public boolean deletePartitionDataOnShutdown;
	
	@UsageTextField(level=OptionLevel.DEVELOPER)
	@Parameter(names="--mysqldbtae-path-strip", description="If a trimmed path to be written to the database starts with the trimmed version of this string, we will remove this string from the start. This is useful if you mount the root of some remote file system that the workers will execute on in some directory. You should not put a / at the end of this string")
	public String pathStrip;
	
	@UsageTextField(level=OptionLevel.ADVANCED)
	@DynamicParameter(names="-M", description="Name and path script to execute. The script will be invoked with arguments as follows <username> <password> <host> <port> <databasename> <pool>. Supplying this script will not cause it to be executed, use the --mysqldbtae-exec option passing the key in. For example -MMYCLUSTER=/path/to/workersubmit.sh ---mysqldbtae-exec MYCLUSTER. Note that we block until this job returns successfully.")
	public Map<String, String> execProfiles = new TreeMap<String, String>();
	
	@UsageTextField(level=OptionLevel.ADVANCED, defaultValues="No profile will be executed")
	@Parameter(names="--mysqldbtae-exec", description="Profile to execute")
	public String execProfile = null;
	
	@UsageTextField(level=OptionLevel.ADVANCED)
	@Parameter(names="--mysqldbtae-exec-abort-on-error", description="If true, if the process invoked throws an error we will throw an exception")
	public boolean execAbortOnErrorCode = true;
	
	
	@Parameter(names="--mysqldbtae-hostname", description="Hostname of database server" )
	public String host;
	
	@Parameter(names="--mysqldbtae-password", description="Password of database server" )
	public String password;
	
	@Parameter(names="--mysqldbtae-database", description="Name of Database" )
	public String databaseName = System.getProperty("user.name") + "_mysqldbtae";
	
	
	@Parameter(names="--mysqldbtae-username", description="Username of the Database")
	public String username;
	
	@Parameter(names="--mysqldbtae-port", description="Port of database server")
	public int port = 3306;
	
	@Parameter(names="--mysqldbtae-options", description="MySQL Configuration Option File")
	@ParameterFile
	public File mysqlParamFile = null;

	@Parameter(names="--mysqldbtae-shutdown-workers-on-complete", description="Shutdown the workers when complete")
	public boolean shutdownWorkersOnCompletion = false;
	
	@Parameter(names="--mysqldbtae-priority", description="Priority of Jobs inserted into database")
	public JobPriority priority = JobPriority.NORMAL;
	
	//This is almost certain premature optimization
	@UsageTextField(level=OptionLevel.DEVELOPER)
	@Parameter(names={"--mysqldbtae-get-addl-run-data", "--mysqldbtae-get-additional-run-data", "--mysqldbtae-get-full-run"}, description="Get the Additional Run Data field from the database")
	public boolean additionalRunData = true;

	@UsageTextField(level=OptionLevel.ADVANCED)
	@Parameter(names={"--mysqldbtae-wake-up-on-submit"}, description="Wake the workers up when new jobs are submitted")
	public boolean wakeUpWorkersOnSubmit = true;

	@UsageTextField(defaultValues="Twice the number of available processors", level=OptionLevel.DEVELOPER)
	@Parameter(names={"--mysqldbtae-poll-threads"}, description="How many threads will be used to manage the outstanding requests", validateWith=FixedPositiveInteger.class)
	public int pollPoolSize = Runtime.getRuntime().availableProcessors() * 2;


	@UsageTextField(level=OptionLevel.DEVELOPER)
	@Parameter(names="--mysqldbtae-poll-delay", description="How often (in milliseconds) to poll the database for new results", validateWith=FixedPositiveInteger.class)
	public long delayBetweenPolls = 2000;

	@UsageTextField(level=OptionLevel.ADVANCED)
	@Parameter(names="--dead-job-check-frequency", description="How often (in seconds) to check for stale or dead jobs", validateWith=FixedPositiveInteger.class)
	public int deadJobCheckFrequency = 120;

	
	
	

	
	public Connection getConnection()
	{
		String url="jdbc:mysql://" + host + ":" + port + "/" + databaseName;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			return DriverManager.getConnection(url,username, password);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
			
		}
		
		
	}
	
}
