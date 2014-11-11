package ca.ubc.cs.beta.mysqldbtae.example;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;

import ca.ubc.cs.beta.aeatk.help.HelpOptions;
import ca.ubc.cs.beta.aeatk.logging.ConsoleOnlyLoggingOptions;
import ca.ubc.cs.beta.aeatk.logging.LoggingOptions;
import ca.ubc.cs.beta.aeatk.misc.jcommander.validator.FixedPositiveInteger;
import ca.ubc.cs.beta.aeatk.misc.options.UsageTextField;
import ca.ubc.cs.beta.aeatk.options.AbstractOptions;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorOptions;

@UsageTextField
public class BraninSearchOptions extends AbstractOptions {
	
	
	@ParametersDelegate
	TargetAlgorithmEvaluatorOptions taeOpts = new TargetAlgorithmEvaluatorOptions();
	
	@ParametersDelegate
	HelpOptions help = new HelpOptions();
	
	@ParametersDelegate
	LoggingOptions logOpts = new ConsoleOnlyLoggingOptions();
		
	@Parameter(names={"--evaluations"}, description="Number of evaluations of the branin function to do" , validateWith=FixedPositiveInteger.class)
	public int evaluations = 100;
	
	@Parameter(names={"--seed"}, description="seed to use for generating configurations")
	public int seed = 0;
}
