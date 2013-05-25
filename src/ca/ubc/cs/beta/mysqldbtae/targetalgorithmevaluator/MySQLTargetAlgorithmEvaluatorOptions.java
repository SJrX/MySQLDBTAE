package ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import ca.ubc.cs.beta.aclib.misc.jcommander.validator.FixedPositiveInteger;
import ca.ubc.cs.beta.aclib.misc.options.UsageTextField;
import ca.ubc.cs.beta.aclib.options.AbstractOptions;
import ca.ubc.cs.beta.mysqldbtae.JobPriority;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterFile;

@UsageTextField(title="MySQL Target Algorithm Evaluator Options", description="Options that control the MySQL Target Algorithm Evaluator")
public class MySQLTargetAlgorithmEvaluatorOptions extends AbstractOptions{

	@Parameter(names="--mysqldbtae-pool", description="Pool to take tasks from")
	public String pool;
	
	@Parameter(names="--mysqldbtae-create-pool", description="Attempt to create the tables for the database")
	public boolean createTables = true;
	
	@Parameter(names="--mysqldbtae-batch-insert-size", description="The number of runs to batch into a single insert", validateWith=FixedPositiveInteger.class)
	public int batchInsertSize = 500;
	
	@Parameter(names="--mysqldbtae-run-partition", description="Sets which partition the runs should exist in (existing runs in the database will only be used if the runPartition matches) ")
	public int runPartition = 0;
	
	@Parameter(names="--mysqldbtae-delete-partition-on-shutdown", description="If set to true, we will delete all data in the run partition (this requires that the partition be set to something greater than zero)")
	public boolean deletePartitionDataOnShutdown;
	
	@Parameter(names="--mysqldbtae-path-strip", description="If a trimmed path to be written to the database starts with the trimmed version of this string, we will remove this string from the start. This is useful if you mount the root of some remote file system that the workers will execute on in some directory. You should not put a / at the end of this string")
	public String pathStrip;
	

	@Parameter(names="--mysqldbtae-hostname", description="Hostname of database server" )
	public String host = "arrowdb.cs.ubc.ca";
	
	
	@Parameter(names="--mysqldbtae-password", description="Password of database server" )
	public String password = "";
	
	@Parameter(names="--mysqldbtae-database", description="Name of Database" )
	public String databaseName = "mysql_db_tae";
	
	
	@Parameter(names="--mysqldbtae-username", description="Username of the Database")
	public String username = "hutter";
	
	@Parameter(names="--mysqldbtae-port", description="Port of database server")
	public int port=4040;
	
	@Parameter(names="--mysqldbtae-options", description="MySQL Configuration Option File")
	@ParameterFile
	public File mysqlParamFile = null;

	@Parameter(names="--mysqldbtae-priority", description="Priority of Jobs inserted into database")
	public JobPriority priority = JobPriority.NORMAL;
	
	//This is almost certain premature optimization
	@Parameter(names={"--mysqldbtae-get-addl-run-data", "--mysqldbtae-get-additional-run-data", "--mysqldbtae-get-full-run"}, description="Get the Additional Run Data field from the database")
	public boolean additionalRunData = true;

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
