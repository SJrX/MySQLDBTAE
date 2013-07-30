package ca.ubc.cs.beta.mysqldbtae.worker;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterFile;
import com.beust.jcommander.ParametersDelegate;
import com.beust.jcommander.validators.PositiveInteger;

import ca.ubc.cs.beta.aclib.help.HelpOptions;
import ca.ubc.cs.beta.aclib.misc.file.HomeFileUtils;
import ca.ubc.cs.beta.aclib.misc.jcommander.converter.DurationConverter;
import ca.ubc.cs.beta.aclib.misc.options.UsageTextField;
import ca.ubc.cs.beta.aclib.options.AbstractOptions;
import ca.ubc.cs.beta.aclib.options.MySQLOptions;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorOptions;
import ca.ubc.cs.beta.aclib.misc.jcommander.converter.WritableDirectoryConverter;

@UsageTextField(title="MySQL TAE Worker Options", description="Options that describe and control the MySQL TAE Worker Process ")
public class MySQLTAEWorkerOptions extends AbstractOptions {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@UsageTextField(defaultValues="~/.aclib/mysqlworker.opt")
	@Parameter(names="--mysqlWorkerDefaultsFile", description="file that contains default settings for MySQL Workers")
	@ParameterFile(ignoreFileNotExists = true) 
	public File mysqlworkerDefaults = HomeFileUtils.getHomeFile(".aclib" + File.separator  + "mysqlworker.opt");
	
	@ParametersDelegate
	public MySQLOptions mysqlOptions = new MySQLOptions();
	
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
	
	@Parameter(names="--poolIdleTimeLimit", description="Amount of idle time allowed to accumulate in the pool before shutdown", converter=DurationConverter.class)
	public int poolIdleTimeLimit = 14400000;
	
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

	@Parameter(names="--createPoolTables", description="Create the tables for the pool in the database")
	public boolean createTables = true;
	
	@Parameter(names="--checkMinCutoff", description="Check if the minimum cutoff time of remaining jobs is greater than the remaining time.")
	public boolean checkMinCutoff = true;
	
	@Parameter(names="--minCutoffDeathTime", description="Amount of time to wait after discovering all jobs exceed the remaining time.")
	public int minCutoffDeathTime = 60;
	
	@UsageTextField(defaultValues="10 days")
	@Parameter(names="--idleLimit", description="Amount of time to not have a task before shutting down (by default this limit is set to 10 days)" , converter=DurationConverter.class)
	public int idleLimit = 86400*10;
	
	@ParametersDelegate
	public HelpOptions help = new HelpOptions();
	
}
