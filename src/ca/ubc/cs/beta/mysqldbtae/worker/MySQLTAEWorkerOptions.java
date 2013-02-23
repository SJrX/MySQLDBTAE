package ca.ubc.cs.beta.mysqldbtae.worker;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;
import com.beust.jcommander.validators.PositiveInteger;

import ca.ubc.cs.beta.aclib.misc.jcommander.converter.DurationConverter;
import ca.ubc.cs.beta.aclib.misc.jcommander.validator.FixedPositiveInteger;
import ca.ubc.cs.beta.aclib.misc.jcommander.validator.NonNegativeInteger;
import ca.ubc.cs.beta.aclib.misc.options.MySQLConfig;
import ca.ubc.cs.beta.aclib.misc.options.UsageTextField;
import ca.ubc.cs.beta.aclib.options.AbstractOptions;
import ca.ubc.cs.beta.aclib.options.TargetAlgorithmEvaluatorOptions;
import ca.ubc.cs.beta.aclib.misc.jcommander.converter.WritableDirectoryConverter;

@UsageTextField(title="MySQL TAE Worker Options", description="Options that describe and control the MySQL TAE Worker Process ")
public class MySQLTAEWorkerOptions extends AbstractOptions {


	@ParametersDelegate
	public MySQLConfig mysqlOptions = new MySQLConfig();
	
	@ParametersDelegate
	public TargetAlgorithmEvaluatorOptions taeOptions = new TargetAlgorithmEvaluatorOptions();

	@Parameter(names={"--runsToBatch"}, description="Number of runs to batch at a single time", validateWith=PositiveInteger.class)
	public int runsToBatch = 1;

	@Parameter(names={"--numUncaughtExceptions"}, description="Will retry the entire process until this many uncaught exceptions occur", validateWith=PositiveInteger.class)
	public int uncaughtExceptionLimit = 5;

	@Parameter(names="--delayBetweenRequests", description="Minimum amount of time (in seconds) required between fetching requests from the MySQL DB", validateWith=PositiveInteger.class)
	//MySQL worker other thread may read this
	public volatile int delayBetweenRequests = 10;

	@Parameter(names="--pool", description="Pool to take tasks from", required = true)
	public String pool;
	
	@Parameter(names="--timeLimit", description="Amount of time to work for", required = true, converter=DurationConverter.class)
	public int timeLimit;
	
	@Parameter(names="--shutdownBuffer", description="Amount of time to budget for shutdown tasks", converter=DurationConverter.class)
	public int shutdownBuffer = 60;
	
	@Parameter(names="--jobID", description="Job Identifier for worker (logged to database)")
	public String jobID = "CLI";

	@Parameter(names="--updateFrequency", description="How often to check DB for new parameters")
	public long updateFrequency = 60;
	
	@Parameter(names="--logOutputDir", description="Log Output Directory", converter=WritableDirectoryConverter.class)
	public File logDirectory = new File(System.getProperty("user.home"));
	/*
	@Parameter(names="--numberOfInsertsForTest")
	public int numberOfInserts = 1000;
	*/

	@Parameter(names="--createPoolTables", description="Create the tables for the pool in the database")
	public boolean createTables = true;
}
