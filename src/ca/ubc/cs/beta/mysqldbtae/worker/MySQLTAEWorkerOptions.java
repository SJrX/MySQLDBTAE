package ca.ubc.cs.beta.mysqldbtae.worker;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterFile;
import com.beust.jcommander.ParametersDelegate;
import com.beust.jcommander.validators.PositiveInteger;

import ca.ubc.cs.beta.aeatk.help.HelpOptions;
import ca.ubc.cs.beta.aeatk.misc.file.HomeFileUtils;
import ca.ubc.cs.beta.aeatk.misc.jcommander.converter.DurationConverter;
import ca.ubc.cs.beta.aeatk.misc.options.OptionLevel;
import ca.ubc.cs.beta.aeatk.misc.options.UsageTextField;
import ca.ubc.cs.beta.aeatk.options.AbstractOptions;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorOptions;
import ca.ubc.cs.beta.aeatk.misc.jcommander.converter.WritableDirectoryConverter;

@UsageTextField(title="MySQL TAE Worker Options", description="Options that describe and control the MySQL TAE Worker Process ")
public class MySQLTAEWorkerOptions extends AbstractOptions {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@UsageTextField(defaultValues="~/.aeatk/mysqlworker.opt")
	@Parameter(names="--mysqlWorkerDefaultsFile", description="file that contains default settings for MySQL Workers")
	@ParameterFile(ignoreFileNotExists = true) 
	public File mysqlworkerDefaults = HomeFileUtils.getHomeFile(".aeatk" + File.separator  + "mysqlworker.opt");
	
	@ParametersDelegate
	public MySQLOptions mysqlOptions = new MySQLOptions();
	
	@ParametersDelegate
	public TargetAlgorithmEvaluatorOptions taeOptions = new TargetAlgorithmEvaluatorOptions();

	@UsageTextField(level=OptionLevel.INTERMEDIATE)
	@Parameter(names={"--runsToBatch"}, description="Number of runs to batch at a single time", validateWith=PositiveInteger.class)
	public volatile int runsToBatch = 3;

	@UsageTextField(level=OptionLevel.ADVANCED)
	@Parameter(names={"--numUncaughtExceptions"}, description="Will retry the entire process until this many uncaught exceptions occur", validateWith=PositiveInteger.class)
	public int uncaughtExceptionLimit = 5;

	@UsageTextField(level=OptionLevel.INTERMEDIATE)
	@Parameter(names="--delayBetweenRequests", description="Minimum amount of time (in seconds) required between fetching requests from the MySQL DB", validateWith=PositiveInteger.class)
	//MySQL worker other thread may read this
	public volatile int delayBetweenRequests = 10;

	@UsageTextField(level=OptionLevel.BASIC)
	@Parameter(names="--pool", description="Pool to take tasks from", required = true)
	public String pool;
	
	@UsageTextField(level=OptionLevel.INTERMEDIATE)
	@Parameter(names="--poolIdleTimeLimit", description="Amount of idle time allowed to accumulate in the pool before shutdown", converter=DurationConverter.class)
	public volatile int poolIdleTimeLimit = 14400000;
	
	@UsageTextField(level=OptionLevel.BASIC, defaultValues="no time limit")
	@Parameter(names="--timeLimit", description="Amount of time to work for, you should set this to the time limit of the job, otherwise the database may have jobs that are stuck.", converter=DurationConverter.class)
	public volatile int timeLimit = Integer.MAX_VALUE;
	
	@UsageTextField(level=OptionLevel.DEVELOPER)
	@Parameter(names="--shutdownBuffer", description="Amount of time to budget for shutdown tasks", converter=DurationConverter.class)
	public int shutdownBuffer = 60;
	
	@UsageTextField(level=OptionLevel.DEVELOPER)
	@Parameter(names="--jobID", description="Job Identifier for worker (logged to database), you should generally set this to some environment variable (i.e. --jobID $CLUSTER_JOB_ID)")
	public String jobID = "CLI";

	//@UsageTextField(level=OptionLevel.INTERMEDIATE)
	//@Parameter(names="--updateFrequency", description="How often to check DB for new parameters", converter=DurationConverter.class)
	//public volatile long updateFrequency = 30;
	
	@UsageTextField(level=OptionLevel.BASIC)
	@Parameter(names="--logOutputDir", description="Log Output Directory", converter=WritableDirectoryConverter.class)
	public File logDirectory = new File(System.getProperty("user.home"));

	@UsageTextField(level=OptionLevel.ADVANCED)
	@Parameter(names="--createPoolTables", description="Create the tables for the pool in the database")
	public Boolean createTables = null;
	
	@Parameter(names="--checkMinCutoff", description="Check if the minimum cutoff time of remaining jobs is greater than the remaining time.")
	public boolean checkMinCutoff = true;
	
	@Parameter(names="--minCutoffDeathTime", description="Amount of time to wait after discovering all jobs exceed the remaining time.", converter=DurationConverter.class)
	public int minCutoffDeathTime = 600;
	
	@UsageTextField(defaultValues="10 days")
	@Parameter(names="--idleLimit", description="Amount of time to not have a task before shutting down (by default this limit is set to 10 days)" , converter=DurationConverter.class)
	public int idleLimit = 86400*10;
	
	@Parameter(names="--pushback-threshold", description="Number of NEW jobs in the database")
	public int pushbackThreshold = 3;
	
	
	@Parameter(names="--concurrency-factor", description="How many workers should be allowed to grab jobs concurrently from the database, set to zero to disable.")
	public int concurrencyFactor = 4;
	
	@ParametersDelegate
	public HelpOptions help = new HelpOptions();

	
	
}
