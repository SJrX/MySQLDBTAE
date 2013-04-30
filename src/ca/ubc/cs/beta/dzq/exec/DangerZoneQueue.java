package ca.ubc.cs.beta.dzq.exec;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import org.mangosdk.spi.ProviderFor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import ca.ubc.cs.beta.aclib.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aclib.algorithmrun.RunResult;
import ca.ubc.cs.beta.aclib.algorithmrun.kill.KillableAlgorithmRun;
import ca.ubc.cs.beta.aclib.configspace.ParamConfigurationSpace;
import ca.ubc.cs.beta.aclib.configspace.ParamFileHelper;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;
import ca.ubc.cs.beta.aclib.misc.jcommander.JCommanderHelper;
import ca.ubc.cs.beta.aclib.misc.version.VersionTracker;
import ca.ubc.cs.beta.aclib.options.AbstractOptions;
import ca.ubc.cs.beta.aclib.options.ConfigToLaTeX;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aclib.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aclib.runconfig.RunConfig;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.TargetAlgorithmEvaluatorBuilder;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.cli.CommandLineTargetAlgorithmEvaluatorOptions;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.currentstatus.CurrentRunStatusObserver;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.decorators.BoundedTargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.deferred.TAECallback;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.deferred.WaitableTAECallback;
import ca.ubc.cs.beta.aclib.targetalgorithmevaluator.loader.TargetAlgorithmEvaluatorLoader;
import ca.ubc.cs.beta.dzq.options.DangerZoneQueueOptions;

public class DangerZoneQueue {

	private static Logger log = LoggerFactory.getLogger(DangerZoneQueue.class);
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
	

		DangerZoneQueueOptions dzOpts = new DangerZoneQueueOptions();
		
		Map<String, AbstractOptions> taeOpts = TargetAlgorithmEvaluatorLoader.getAvailableTargetAlgorithmEvaluators();
		
		JCommander jcom = JCommanderHelper.getJCommander(dzOpts, taeOpts);

		try {
				
			jcom.parse(args);

			VersionTracker.setClassLoader(TargetAlgorithmEvaluatorLoader.getClassLoader());
			VersionTracker.logVersions();
		
			
			log.debug("Abort on Crash and abort on First Run Crash are disabled, as is verifySAT");
			dzOpts.taeOptions.abortOnCrash = false;
			dzOpts.taeOptions.abortOnFirstRunCrash = false;
			dzOpts.taeOptions.verifySAT = false;
			
			for(String name : taeOpts.keySet())
			{
				log.info("Target Algorithm Evaluator Available {} ", name);
			}
			
	
			ParamConfigurationSpace configSpace = ParamConfigurationSpace.getSingletonConfigurationSpace();
			
			
			AlgorithmExecutionConfig execConfig = new AlgorithmExecutionConfig(getExecutionString(dzOpts.wrapper), "/", configSpace, true, true, dzOpts.runtimeLimit);
			
			TargetAlgorithmEvaluator tae = TargetAlgorithmEvaluatorBuilder.getTargetAlgorithmEvaluator(dzOpts.taeOptions, execConfig, false, dzOpts.ignoreTAEBounds, taeOpts, null);
			try {
				List<String> commands = getCommands(dzOpts);
				
				List<RunConfig> runConfigs = new ArrayList<RunConfig>();
				
				
				StringBuilder sb = new StringBuilder();
				for(Object o : jcom.getObjects())
				{
					sb.append(o.toString()).append("\n");
				}
					
				log.info("==========Configuration Options==========\n{}", sb.toString());
				
	
				
				for(String cmd : commands)
				{
					for(int i=0; i < dzOpts.execTimes; i++)
					{
						
						String cmd2 = cmd.replaceAll("\\%ID\\%", String.valueOf(i+dzOpts.idOffset));
						ProblemInstance pi = new ProblemInstance("\"DIR=>" + dzOpts.dir + ";CMD=>" + cmd2+";ENFORCETIME=>" + dzOpts.enforceTimeLimit + ";SHOWOUTPUT=>" + dzOpts.showOutput  + "\"", "DZQFTW");
						ProblemInstanceSeedPair pisp = new ProblemInstanceSeedPair(pi, i);
						
						runConfigs.add(new RunConfig(pisp,dzOpts.runtimeLimit, configSpace.getDefaultConfiguration()));
					}
				}
				
				
				TAECallback callback = new TAECallback()
				{
	
					@Override
					public void onSuccess(List<AlgorithmRun> runs) {
						//log.info("Done");
						for(AlgorithmRun run : runs)
						{
							Object[] args = { run.getRunConfig().getProblemInstanceSeedPair().getInstance().getInstanceName(), run.getRunResult(), run.getRuntime(), run.getQuality(), run.getAdditionalRunData() };
							log.info("Run Completed Successfully: Command: {} => (Result: {}, Runtime: {}, Exit Code: {} , Addl: {} ) ", args);
						}
						
					}
	
					@Override
					public void onFailure(RuntimeException t) {
						log.error("An error has occured", t);
					}
					
				};
				
				WaitableTAECallback wait = new WaitableTAECallback(callback);
				
				final double frequency = dzOpts.statusFrequency;
				
				final Semaphore observerFired = new Semaphore(0);
				
				final boolean showDetailedStatus = dzOpts.showDetailed;
				final boolean showStatusOverview = dzOpts.showOverview;
				
				CurrentRunStatusObserver obs = new CurrentRunStatusObserver()
				{
	
					long lastUpdate = -1;
					@Override
					public void currentStatus(
							List<? extends KillableAlgorithmRun> runs) {
						
						
							if(!showDetailedStatus && !showStatusOverview)
							{
								return;
							}
							Map<String, Integer> counts = new HashMap<String, Integer>();
							Map<String, Double> runtimes = new HashMap<String, Double>();
							
							
							for(RunResult r : RunResult.values())
							{
								counts.put(r.toString(), 0);
								runtimes.put(r.toString(),0.0);
								
							}
	
							
							counts.put("NOT-STARTED", 0);
							runtimes.put("NOT-STARTED",0.0);
							
							if( ((System.currentTimeMillis() - lastUpdate) / 1000.0) < frequency)
							{
								return;
							} 
							
							log.info("***** Run Status Update *****");
							for(AlgorithmRun run : runs)
							{
								Object[] args = { run.getRunConfig().getProblemInstanceSeedPair().getInstance().getInstanceName(), run.getRunResult(), run.getRuntime(), run.getQuality(), run.getAdditionalRunData() };
								
								if(showDetailedStatus)
								{
									log.info("Current Run Status: Command: {} => (Result: {}, Runtime: {}, Exit Code: {} , Addl: {} ) ", args);
								}
								
								if(run.getRunResult().equals(RunResult.RUNNING) && run.getRuntime() <= 0.0)
								{
									counts.put("NOT-STARTED", counts.get("NOT-STARTED")+1);
								} else
								{
									counts.put(run.getRunResult().toString(), counts.get(run.getRunResult().toString())+1);
									runtimes.put(run.getRunResult().toString(), runtimes.get(run.getRunResult().toString())+run.getRuntime());
								}
								
								
							}
							
							
							if(showStatusOverview)
							{
								for(Entry<String, Integer> countEntry : counts.entrySet())
								{
									Object[] args = { countEntry.getKey(), countEntry.getValue(), runtimes.get(countEntry.getKey())};
									log.info("Status Breakdown: {} => Count: {}  Total Runtime: {}", args);
								}
							}
							
	
							lastUpdate = System.currentTimeMillis();
							
							observerFired.release();
						
					}
					
				};
				
				
				tae.evaluateRunsAsync(runConfigs,  wait, obs);
				
				try {
					observerFired.acquire();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
				
				
				if(!tae.areRunsPersisted() || dzOpts.waitForRuns)
				{
					log.info("Waiting until completion");
					
					wait.waitForCompletion();
				}
				
			} finally
			{
				tae.notifyShutdown();
			}
			
		} catch(ParameterException t)
		{
			
			ConfigToLaTeX.usage(ConfigToLaTeX.getParameters(dzOpts,taeOpts));
			log.error(t.getMessage());
		}
		

	}

	
	private static List<String> getCommands(DangerZoneQueueOptions dzOpts) 
	{
		
		int commandOptions = 0;

		List<String> commands = null;
		if(dzOpts.exec != null)
		{
			commandOptions++;
			commands = Collections.singletonList(dzOpts.exec);
		}
	
		if(dzOpts.execFile != null)
		{
			throw new UnsupportedOperationException("Not Implemented Currently");
		}
		
		
		if(commandOptions != 1)
		{
			throw new ParameterException("You must supply one and only one of --exec and --execFile");
		}
	
		return commands;
		
	}


	private static String getExecutionString(String wrapper) {
		if(wrapper.equals("BUILTIN"))
		{
			
			String fullClass = DangerZoneWrapper.class.getClassLoader().getResource(DangerZoneWrapper.class.getCanonicalName().replace(".", File.separator)  + ".class").getFile();
			String root;
			
			
			if(!fullClass.contains("jar!"))
			{
				String wrapperRelativePath = DangerZoneWrapper.class.getCanonicalName().replace(".", File.separator) + ".class";
				root = fullClass.replace(wrapperRelativePath, "");
			} else
			{
				root = fullClass.split("!")[0];
			}
			
			
			log.debug("Built In Wrapper parsed to location {} ", root);
		
			
			return "java -cp " + root + " " + DangerZoneWrapper.class.getCanonicalName();
			//System.out.println(DangerZoneWrapper.class.getClassLoader().getResource(ProviderFor.class.getCanonicalName().replace(".", File.separator)  + ".class").getFile());
			
			//return wrapper;
		} else
		{
			throw new IllegalStateException("What?");
			//return wrapper;
		}
	}

}
