package ca.ubc.cs.beta.mysqldbtae.worker;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterFile;
import com.beust.jcommander.ParametersDelegate;
import com.beust.jcommander.validators.PositiveInteger;

import ca.ubc.cs.beta.aeatk.help.HelpOptions;
import ca.ubc.cs.beta.aeatk.logging.ExplicitLogFileLoggingOptions;
import ca.ubc.cs.beta.aeatk.logging.LoggingOptions;
import ca.ubc.cs.beta.aeatk.logging.SingleLogFileLoggingOptions;
import ca.ubc.cs.beta.aeatk.misc.file.HomeFileUtils;
import ca.ubc.cs.beta.aeatk.misc.jcommander.converter.DurationConverter;
import ca.ubc.cs.beta.aeatk.misc.options.OptionLevel;
import ca.ubc.cs.beta.aeatk.misc.options.UsageTextField;
import ca.ubc.cs.beta.aeatk.options.AbstractOptions;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorOptions;
import ca.ubc.cs.beta.aeatk.misc.jcommander.converter.WritableDirectoryConverter;
import ca.ubc.cs.beta.aeatk.misc.jcommander.validator.FixedPositiveInteger;

@UsageTextField(title="MySQL TAE Worker Options", description="Options that describe and control the MySQL TAE Worker Process ")
public class MySQLTAEWorkerOptions extends AbstractOptions {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@UsageTextField(defaultValues="~/.aeatk/mysqlworker.opt")
	@Parameter(names="--mysql-worker-defaults-file", description="file that contains default settings for MySQL Workers")
	@ParameterFile(ignoreFileNotExists = true) 
	public File mysqlworkerDefaults = HomeFileUtils.getHomeFile(".aeatk" + File.separator  + "mysqlworker.opt");
	
	@ParametersDelegate
	public MySQLOptions mysqlOptions = new MySQLOptions();
	
	@ParametersDelegate
	public TargetAlgorithmEvaluatorOptions taeOptions = new TargetAlgorithmEvaluatorOptions();

	@UsageTextField(level=OptionLevel.INTERMEDIATE)
	@Parameter(names={"--runs-to-batch","--runsToBatch"}, description="Number of runs to batch at a single time", validateWith=FixedPositiveInteger.class)
	public volatile int runsToBatch = 3;
	
	@UsageTextField(level=OptionLevel.INTERMEDIATE)
	@Parameter(names={"--min-runs-to-batch"}, description="Number of runs to batch at a single time", validateWith=FixedPositiveInteger.class)
	public volatile int minRunsToBatch = 1;
	
	@UsageTextField(level=OptionLevel.INTERMEDIATE)
	@Parameter(names={"--max-runs-to-batch"}, description="Number of runs to batch at a single time", validateWith=FixedPositiveInteger.class)
	public volatile int maxRunsToBatch = 100;
	
	@UsageTextField(level=OptionLevel.INTERMEDIATE, defaultValues="true if --runs-to-batch > 1, false otherwise.")
	@Parameter(names="--auto-adjust-batch-size", description="If set to true, we will automatically adjust the number of runs we take from the database as needed")
	public volatile Boolean autoAdjustRuns = null;

	@UsageTextField(level=OptionLevel.ADVANCED)
	@Parameter(names={"--num-uncaught-exceptions","--numUncaughtExceptions"}, description="Will retry the entire process until this many uncaught exceptions occur", validateWith=PositiveInteger.class)
	public int uncaughtExceptionLimit = 5;

	@UsageTextField(level=OptionLevel.INTERMEDIATE)
	@Parameter(names={"--delay-between-requests","--delay","--delayBetweenRequests"}, description="Minimum amount of time (in seconds) required between fetching runs from the MySQL DB", validateWith=PositiveInteger.class)
	//MySQL worker other thread may read this
	public volatile int delayBetweenRequests = 10;

	@UsageTextField(level=OptionLevel.BASIC)
	@Parameter(names="--pool", description="Pool to take tasks from", required = true)
	public String pool;
	
	@UsageTextField(level=OptionLevel.INTERMEDIATE)
	@Parameter(names={"--pool-idle-time-limit","--poolIdleTimeLimit"}, description="Amount of idle time allowed to accumulate in the pool before shutdown (we will shutdown if this limit is exceeded and there is no work)", converter=DurationConverter.class)
	public volatile int poolIdleTimeLimit = 14400000;
	
	@UsageTextField(level=OptionLevel.BASIC, defaultValues="no time limit")
	@Parameter(names={"--time-limit","--timeLimit"}, description="Amount of time to work for, you should set this to the time limit of the job, otherwise the database may have jobs that are stuck.", converter=DurationConverter.class)
	public volatile int timeLimit = Integer.MAX_VALUE;
	
	@UsageTextField(level=OptionLevel.DEVELOPER)
	@Parameter(names={"--shutdown-buffer","--shutdownBuffer"}, description="Amount of time to budget for shutdown tasks", converter=DurationConverter.class)
	public int shutdownBuffer = 60;
	
	@UsageTextField(level=OptionLevel.DEVELOPER)
	@Parameter(names={"--job-id","--jobID"}, description="Job Identifier for worker (logged to database), you should generally set this to some environment variable (i.e. --jobID $CLUSTER_JOB_ID)")
	public String jobID = "CLI";

	//@UsageTextField(level=OptionLevel.INTERMEDIATE)
	//@Parameter(names="--updateFrequency", description="How often to check DB for new parameters", converter=DurationConverter.class)
	//public volatile long updateFrequency = 30;
	
	@UsageTextField(level=OptionLevel.BASIC)
	@Parameter(names={"--log-output-dir","--logOutputDir"}, description="Log Output Directory", converter=WritableDirectoryConverter.class)
	public File logDirectory = new File(System.getProperty("user.home"));

	@UsageTextField(level=OptionLevel.ADVANCED)
	@Parameter(names={"--create-pool-tables","--createPoolTables"}, description="Create the tables for the pool in the database")
	public Boolean createTables = null;
	
	@Parameter(names={"--check-min-cutoff","--checkMinCutoff"}, description="Check if the minimum cutoff time of remaining jobs is greater than the remaining time. If the cutoff time is ludircriously high, we will assume it takes 1 hour, unless --check-min-cutoff-strict is true")
	public boolean checkMinCutoff = true;
	
	@Parameter(names={"--check-min-cutoff-strict"}, description="If false, any cutoff times greater than 10 years will be assumed to take 1 hour. The default is actually Double.MAX_VALUE and probably wasn't set correctly.")
	public boolean checkMinCutoffStrict = false;
	
	@Parameter(names={"--min-cutoff-death-time","--minCutoffDeathTime"}, description="Amount of time to wait after discovering all jobs exceed the remaining time.", converter=DurationConverter.class)
	public int minCutoffDeathTime = 600;
	
	@UsageTextField(defaultValues="10 days")
	@Parameter(names={"--idle-time-limit","--idle-limit","--idleLimit"}, description="Amount of time to not have a task before shutting down (by default this limit is set to 10 days)" , converter=DurationConverter.class)
	public int idleLimit = 86400*10;
	
	@Parameter(names="--pushback-threshold", description="Number of NEW jobs in the database")
	public int pushbackThreshold = 3;
	
	
	@Parameter(names="--concurrency-factor", description="How many workers should be allowed to grab jobs concurrently from the database, set to zero to disable.")
	public int concurrencyFactor = 4;
	
	@UsageTextField(level=OptionLevel.DEVELOPER)
	@Parameter(names="--min-worst-case-time", description="What is the minimum timeframe for the next worse case update", validateWith=PositiveInteger.class)
	public int minWorstCaseTime = 120;
	
	@UsageTextField(level=OptionLevel.DEVELOPER)
	@Parameter(names="--worst-case-multiplier", description="What is the minimum timeframe for the next worse case update",  validateWith=PositiveInteger.class)
	public int worstCaseMultiplier = 5;
	
	@UsageTextField(level=OptionLevel.DEVELOPER)
    @Parameter(names="--min-last-update-time", description="Minimum number of seconds to wait before a run should be updated",  validateWith=PositiveInteger.class)
    public int minLastUpdateTime = 120;
	
	@ParametersDelegate
	public HelpOptions help = new HelpOptions();
	
	@ParametersDelegate
	public ExplicitLogFileLoggingOptions logOptions = new ExplicitLogFileLoggingOptions();
	
	
}
