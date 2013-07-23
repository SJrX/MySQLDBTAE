package ca.ubc.cs.beta.dzq.options;

import java.io.File;

import ca.ubc.cs.beta.aclib.help.HelpOptions;
import ca.ubc.cs.beta.aclib.misc.file.HomeFileUtils;
import ca.ubc.cs.beta.aclib.misc.jcommander.converter.DurationConverter;
import ca.ubc.cs.beta.aclib.misc.options.UsageTextField;
import ca.ubc.cs.beta.aclib.options.AbstractOptions;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorOptions;


import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterFile;
import com.beust.jcommander.ParametersDelegate;

import ca.ubc.cs.beta.aclib.misc.jcommander.validator.*;


@UsageTextField(title="Danger Zone Queue Options", description="Options that control the execution of the queue")
public class DangerZoneQueueOptions extends AbstractOptions {

	@UsageTextField(defaultValues="~/.aclib/dzq.opt")
	@Parameter(names="--dzqDefaultsFile", description="file that contains default settings for DZQ")
	@ParameterFile(ignoreFileNotExists = true) 
	public File dzqDefaults = HomeFileUtils.getHomeFile(".aclib" + File.separator  + "dzq.opt");
	
	@ParametersDelegate
	public HelpOptions help = new HelpOptions();
	
	@ParametersDelegate
	public TargetAlgorithmEvaluatorOptions taeOptions = new TargetAlgorithmEvaluatorOptions();
	

	@ParameterFile
	@Parameter(names="--dz-options-file", description="Option file for dz")
	public File defaultOptions;
	
	@Parameter(names={"--runtime","--runtimeLimit"}, description="How long the job should be allowed to run for", required=true, converter=DurationConverter.class)
	public int runtimeLimit; 

	@UsageTextField(defaultValues="Current Working Directory")
	@Parameter(names={"--directory", "--dir"}, description="Directory to execute the command(s) in")
	public String dir = System.getProperty("user.dir") + File.separator + "";
	
	//Wrapper
	@UsageTextField(defaultValues="Use the built in wrapper")
	@Parameter(names="--wrapper", description="[ADVANCED] Which wrapper should we use")
	public String wrapper = "BUILTIN";
	
	@Parameter(names="--count", description="Number of times to submit the command", validateWith=FixedPositiveInteger.class)
	public int execTimes = 1;
	
	@Parameter(names="--id-offset", description="Offset of ids to submit", validateWith=NonNegativeInteger.class)
	public int idOffset = 0;
	
	@Parameter(names="--dz-bound-tae", description="[ADVANCED] We will ignore the TAE bounds if this is set to true")
	public boolean ignoreTAEBounds = true;

	
	@Parameter(names="--execFile", description="File with commands one per line to execute (lines starting with a # will be ignored). \"%ID%\" will be replaced with the index of the job in the array and a space", converter=ReadableFileConverter.class)
	public File execFile;
	
	@Parameter(names="--exec", description="Command to execute. \"%ID \" will be replaced with the index of the job in the array and a space")
	public String exec = null;

	
	
	@Parameter(names={"--wait-for-completion","--wait-for-runs"}, description="[ADVANCED] Wait for the runs to complete before returning (we will always wait if the TAE is not Persistent)")
	public boolean waitForRuns;
	
	@Parameter(names="--wrapper-enforce-timelimit", description="[ADVANCED] Enforce the timelimit in the wrapper")
	public boolean enforceTimeLimit = true;
	
	@Parameter(names="--wrapper-show-output", description="[ADVANCED] Pass all wrapper output to the callee")
	public boolean showOutput = false;

	@Parameter(names={"--wrapper-output-pils-line","--wrapper-pils-line"}, description="[ADVANCED] Whether to display a \"Results for ParamILS:\" line (if something else will do this then it's best to use that). You will need to use --wrapper-show-output true as well")
	public boolean wrapperPils = false;
	
	@Parameter(names="--update-frequency", description="[ADVANCED] How often to show status updates (in seconds)")
	public double statusFrequency = 10;

	@Parameter(names="--show-status-detailed", description="[ADVANCED] Show detailed status ")
	public boolean showDetailed = false;

	@Parameter(names="--show-status-overview", description="Whether to show the status overview")
	public boolean showOverview = true;
	
	
	
	
}
