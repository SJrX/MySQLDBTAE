package ca.ubc.cs.beta.mysqldbtae.migrate;

import java.sql.SQLException;
import java.util.Map;


import ca.ubc.cs.beta.aeatk.misc.jcommander.JCommanderHelper;
import ca.ubc.cs.beta.aeatk.options.AbstractOptions;
import ca.ubc.cs.beta.aeatk.options.docgen.OptionsToUsage;
import ca.ubc.cs.beta.aeatk.options.docgen.UsageSectionGenerator;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.init.TargetAlgorithmEvaluatorLoader;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;


public class MigrationProcessor {
//private static Logger log = LoggerFactory.getLogger(MigrationProcessor.class);
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
	

		MigrationOptions mitOpts = new MigrationOptions();
		
		Map<String, AbstractOptions> taeOpts = TargetAlgorithmEvaluatorLoader.getAvailableTargetAlgorithmEvaluators();
		mitOpts.taeOptions.targetAlgorithmEvaluator = "MYSQLDB";
		JCommander jcom = JCommanderHelper.getJCommander(mitOpts, taeOpts);
		
		
		try {

			
			jcom.parse(args);
			
			if(!mitOpts.taeOptions.targetAlgorithmEvaluator.equals("MYSQLDB"))
			{
				throw new ParameterException("You can only use the MySQLDB TAE for this");
			}
			
			
			
			
			
			
			
			
			
		} catch(ParameterException e)
		{
			OptionsToUsage.usage(UsageSectionGenerator.getUsageSections(mitOpts, taeOpts));
		}
		
	}

	
	
	public void sourceView(String source) throws SQLException
	{
		/*
		 * Connection conn = null;
		 *
		
		PreparedStatement stmt = conn.prepareStatement("SELECT " +
									  "algorithmExecutable," +
									  "algorithmExecutableDirectory," +
									  "parameterFile," +
									  "executeOnCluster," +
									  "deterministicAlgorithm," +
									  "execConfigCutoffTime," +
									  "problemInstance," +
									  "instanceSpecificInformation," +
									  "seed," +
									  "cutoffTime," +
									  "paramConfiguration," +
									  "cutoffLessThanMax," +
									  "status," +
									  "priority," +
									  "workerUUID," +
									  "lastModified," +
									  "killJob," +
									  "retryAttempts," +
									  "runPartition," +
									  "runResult," +
									  "runLength," +
									  "quality," +
									  "result_seed," +
									  "runtime," +
									  "additional_run_data " +
									  " FROM " + source + " WHERE status=\"COMPLETE\"");
		
		ResultSet rs =  stmt.executeQuery();
		
		while(rs.next())
		{
			ParamConfigurationSpace configSpace = ParamFileHelper.getParamFileParser(rs.getString(3));
			AlgorithmExecutionConfiguration execConfig = new AlgorithmExecutionConfiguration(rs.getString(1), rs.getString(2), configSpace, rs.getBoolean(4), rs.getBoolean(5), rs.getDouble(6));
			
			ParamConfiguration config;
			
			try {
				config = configSpace.getConfigurationFromString(rs.getString(11), StringFormat.ARRAY_STRING_SYNTAX);
			} catch(RuntimeException e)
			{
				log.error("Could not parse configuration from string {} ", rs.getString(11));
				continue;
			}
			
			RunConfig rc = new RunConfig(new ProblemInstanceSeedPair(new ProblemInstance(rs.getString(7)),rs.getLong(8)), rs.getDouble(9), config, rs.getBoolean(10));
			
			
			
			
			
			
			
			
			
		}
		
					*/		
				
				
				
				
				
	}
}
