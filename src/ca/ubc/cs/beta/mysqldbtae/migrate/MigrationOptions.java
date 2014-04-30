package ca.ubc.cs.beta.mysqldbtae.migrate;

import ca.ubc.cs.beta.aeatk.options.AbstractOptions;
import ca.ubc.cs.beta.aeatk.options.MySQLOptions;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorOptions;


import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParametersDelegate;

public class MigrationOptions extends AbstractOptions{

	@ParametersDelegate
	public TargetAlgorithmEvaluatorOptions taeOptions = new TargetAlgorithmEvaluatorOptions();
	
	@ParametersDelegate
	public MySQLOptions mysqlConfig = new MySQLOptions();

	
	@Parameter(names="--sourceTable")
	public String sourceView;
}
