package ca.ubc.cs.beta.mysqldbtae.example;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aeatk.algorithmexecutionconfiguration.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunconfiguration.AlgorithmRunConfiguration;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.AlgorithmRunResult;
import ca.ubc.cs.beta.aeatk.algorithmrunresult.RunStatus;
import ca.ubc.cs.beta.aeatk.example.tae.TargetAlgorithmEvaluatorRunner;
import ca.ubc.cs.beta.aeatk.example.tae.TargetAlgorithmEvaluatorRunnerOptions;
import ca.ubc.cs.beta.aeatk.misc.jcommander.JCommanderHelper;
import ca.ubc.cs.beta.aeatk.misc.version.VersionTracker;
import ca.ubc.cs.beta.aeatk.misc.watch.AutoStartStopWatch;
import ca.ubc.cs.beta.aeatk.misc.watch.StopWatch;
import ca.ubc.cs.beta.aeatk.options.AbstractOptions;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParamFileHelper;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfigurationSpace;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration.ParameterStringFormat;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import ec.util.MersenneTwister;

public class BraninSearch {

	
	
	//SLF4J Logger object (not-initialized on start up in case command line options want to change it)
	private static Logger log;
	
	public static void main(String[] args)
	{
		 
		BraninSearchOptions mainOptions = new BraninSearchOptions();
		
		//Map object that for each available TargetAlgorithmEvaluator gives it's associated options object
		Map<String,AbstractOptions> taeOptions = mainOptions.taeOpts.getAvailableTargetAlgorithmEvaluators();

		try {
			
			//Parses the options given in the args array and sets the values
			JCommander jcom;
			try {
				//This will check for help and version arguments 
				jcom = JCommanderHelper.parseCheckingForHelpAndVersion(args, mainOptions,taeOptions);
				
				//Does any setup work necessary to setup logger.
				mainOptions.logOpts.initializeLogging();
			} finally
			{
				//Initialize the logger *AFTER* the JCommander objects have been parsed
				//So that options that take effect
				log = LoggerFactory.getLogger(TargetAlgorithmEvaluatorRunner.class);
			}
		
			ParameterConfigurationSpace configSpace = ParamFileHelper.getParamFileFromString("x1 [-5,10] [2.5]\nx2 [0,15] [7.5]\n");
			
			
			File f = findBraninExecutable();
			
			AlgorithmExecutionConfiguration execConfig = new AlgorithmExecutionConfiguration(f.getAbsolutePath(), "./", configSpace, false, true, 5);
			//Logs the options (since mainOptions implements AbstractOptions a 'nice-ish' printout is created).
			log.debug("==== Configuration====\n {} ", mainOptions);
			
			
			
			TargetAlgorithmEvaluator tae = null;
			try {
		
				tae = mainOptions.taeOpts.getTargetAlgorithmEvaluator( taeOptions);
				
			
				ProblemInstance	pi = new ProblemInstance("null");
				//A problem instance seed pair object (IMMUTABLE)
				ProblemInstanceSeedPair pisp = new ProblemInstanceSeedPair(pi, -1);
			
				//If we are asked to supply a random a configuration, we need to pass a Random object
				Random configSpacePRNG = new MersenneTwister(mainOptions.seed);
				
				
				List<AlgorithmRunConfiguration> runsToDo = new ArrayList<AlgorithmRunConfiguration>();
				
				//A RunConfig object stores the information needed to actually request (compare the objects here to the information passed to the wrapper as listed in the Manual)
				//It is also IMMUTABLE
				for(int i=0; i < mainOptions.evaluations; i++)
				{
					AlgorithmRunConfiguration runConfig = new AlgorithmRunConfiguration(pisp, execConfig.getAlgorithmMaximumCutoffTime(), configSpace.getRandomParameterConfiguration(configSpacePRNG),execConfig);
					runsToDo.add(runConfig);
				}

				
				
				
				TargetAlgorithmEvaluatorRunObserver taeObs = new TargetAlgorithmEvaluatorRunObserver()
				{

					
					private final ArrayList<Double> treshholds = new ArrayList<Double>(Arrays.asList(0.0,0.25,0.5,0.75,1.0));
					
					@Override
					public synchronized void currentStatus(List<? extends AlgorithmRunResult> runs) {
						
						int complete = 0;
						for(AlgorithmRunResult run : runs)
						{
							if(run.isRunCompleted())
							{
								complete++;
							}
						}
					
						double ratio = complete / (double) runs.size();
						
						boolean shouldLog = false;
						while(ratio > treshholds.get(0))
						{
							treshholds.remove(0);
							shouldLog = true;
						}
						
						if(shouldLog)
						{
							log.info("Branin Search has completed {} runs out of {}", complete, runs.size());
						}
						
					}
					
				};
				StopWatch watch = new AutoStartStopWatch();
				log.info("Starting Random Search for minimum of Branin Function");
				List<AlgorithmRunResult> runs = tae.evaluateRun(runsToDo, taeObs);
				
				double lowestValue = Double.MAX_VALUE;
				ParameterConfiguration lowestConfiguration = null;
				
				double highestValue = Double.MIN_VALUE;
				ParameterConfiguration highestConfiguration = null;
				
				for(AlgorithmRunResult run : runs)
				{
					if(run.getQuality() < lowestValue)
					{
						lowestConfiguration = run.getParameterConfiguration();
						lowestValue = run.getQuality();
					}
					
					if(run.getQuality() > highestValue)
					{
						highestValue  = run.getQuality();
						highestConfiguration = run.getParameterConfiguration();
					}
				}
				
				//log.info("Highest found value was {} and occurred at {}", highestValue, highestConfiguration.getFormattedParameterString(ParameterStringFormat.NODB_SYNTAX));
				
				log.info("Best value for the Brainin Function found was: {} and occurred at {}. Search took {} seconds", lowestValue, lowestConfiguration.getFormattedParameterString(ParameterStringFormat.NODB_SYNTAX),watch.stop()/1000.0);
				
			} finally
			{
				//We need to tell the TAE we are shutting down
				//Otherwise the program may not exit 
				if(tae != null)
				{
					tae.notifyShutdown();
				}
			}
		} catch(ParameterException e)
		{	
			log.error(e.getMessage());
			if(log.isDebugEnabled())
			{
				log.error("Stack trace:",e);
			}
		} catch(Exception e)
		{
			
			e.printStackTrace();
		}
	}
	

	
	
	private static File findBraninExecutable() {
		String[] locations = { "./", "./deployables/","./deployables/example","../"};
		
		
		for(String loc : locations)
		{
			File f = new File(loc + "/branin-sleep.py").getAbsoluteFile();
			
			if(f.exists())
			{
				if(!f.canExecute())
				{
					log.warn("Found branin executable but it is not marked executable: {}", f);
				} else
				{
					return f;
				}
			}
		}
		
		throw new ParameterException("Could not find branin-sleep.py please make sure that it is in the current working directory.");
	}	
}
