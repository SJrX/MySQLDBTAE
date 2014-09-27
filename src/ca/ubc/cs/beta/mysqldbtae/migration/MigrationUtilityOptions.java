package ca.ubc.cs.beta.mysqldbtae.migration;

import ca.ubc.cs.beta.aeatk.example.statemerge.StateMergeNoArgumentHandler;
import ca.ubc.cs.beta.aeatk.help.HelpOptions;
import ca.ubc.cs.beta.aeatk.logging.ConsoleOnlyLoggingOptions;
import ca.ubc.cs.beta.aeatk.logging.LoggingOptions;
import ca.ubc.cs.beta.aeatk.misc.options.OptionLevel;
import ca.ubc.cs.beta.aeatk.misc.options.UsageTextField;
import ca.ubc.cs.beta.aeatk.options.AbstractOptions;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;

@UsageTextField(title="MySQL Target Algorithm Evaluator Utility", description="Tries to restore old databases to the new format and correct hashes", noarg=MigrationUtilityNoArgumentHandler.class)

public class MigrationUtilityOptions extends AbstractOptions{

	@ParametersDelegate
	public LoggingOptions logOpts = new ConsoleOnlyLoggingOptions();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@ParametersDelegate
	public MySQLOptions mysqlOptions = new MySQLOptions();
	
	@UsageTextField(level=OptionLevel.BASIC)
	@Parameter(names="--pool", description="Pool to take tasks from")
	public String pool;
	
	@UsageTextField(level=OptionLevel.BASIC)
	@Parameter(names="--all-pools", description="If true then all pools in the database will be upgraded")
	public boolean allPools = false;
	
	@Parameter(names={"--skip-warning","--skip-warning-pause"}, description="If true the start up warning will be skipped")
	public boolean skipWarningPause = false;
	
	
	@ParametersDelegate
	public HelpOptions help = new HelpOptions();


}
