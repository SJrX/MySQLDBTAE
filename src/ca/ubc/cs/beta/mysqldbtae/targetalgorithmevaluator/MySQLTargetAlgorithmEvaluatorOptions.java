package ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import ca.ubc.cs.beta.aclib.misc.file.HomeFileUtils;
import ca.ubc.cs.beta.aclib.misc.jcommander.validator.FixedPositiveInteger;
import ca.ubc.cs.beta.aclib.misc.options.OptionLevel;
import ca.ubc.cs.beta.aclib.misc.options.UsageTextField;
import ca.ubc.cs.beta.aclib.options.AbstractOptions;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterFile;

@UsageTextField(title="MySQL Target Algorithm Evaluator Options", description="Options that control the MySQL Target Algorithm Evaluator", level=OptionLevel.INTERMEDIATE)
public class MySQLTargetAlgorithmEvaluatorOptions extends AbstractOptions{

	@UsageTextField(defaultValues="~/.aclib/mysqldbtae.opt")
	@Parameter(names="--mysqlTaeDefaultsFile", description="file that contains default settings for MySQL")
	@ParameterFile(ignoreFileNotExists = true) 
	public File smacDefaults = HomeFileUtils.getHomeFile(".aclib" + File.separator  + "mysqldbtae.opt");
	
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

	@Parameter(names="--mysqldbtae-priority", description="Priority of Jobs inserted into database")
	public JobPriority priority = JobPriority.NORMAL;
	
	//This is almost certain premature optimization
	@UsageTextField(level=OptionLevel.DEVELOPER)
	@Parameter(names={"--mysqldbtae-get-addl-run-data", "--mysqldbtae-get-additional-run-data", "--mysqldbtae-get-full-run"}, description="Get the Additional Run Data field from the database")
	public boolean additionalRunData = true;

	@UsageTextField(level=OptionLevel.ADVANCED)
	@Parameter(names={"--mysqldbtae-wake-up-on-submit"}, description="Wake the workers up when new jobs are submitted")
	public boolean wakeUpWorkersOnSubmit;

	@UsageTextField(defaultValues="Twice the number of available processors", level=OptionLevel.DEVELOPER)
	@Parameter(names={"--mysqldbtae-poll-threads"}, description="How many threads will be used to manage the outstanding requests")
	public int pollPoolSize = Runtime.getRuntime().availableProcessors() * 2;


	@UsageTextField(level=OptionLevel.DEVELOPER)
	@Parameter(names="--mysqldbtae-poll-delay", description="How often (in milliseconds) to poll the database for new results")
	public long delayBetweenPolls = 2000;

	
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
