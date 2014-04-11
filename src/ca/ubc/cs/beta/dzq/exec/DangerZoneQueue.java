package ca.ubc.cs.beta.dzq.exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import ca.ubc.cs.beta.aeatk.algorithmrun.AlgorithmRun;
import ca.ubc.cs.beta.aeatk.algorithmrun.RunResult;
import ca.ubc.cs.beta.aeatk.configspace.ParamConfigurationSpace;
import ca.ubc.cs.beta.aeatk.execconfig.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.misc.jcommander.JCommanderHelper;
import ca.ubc.cs.beta.aeatk.misc.spi.SPIClassLoaderHelper;
import ca.ubc.cs.beta.aeatk.misc.version.VersionTracker;
import ca.ubc.cs.beta.aeatk.options.AbstractOptions;

import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstance;
import ca.ubc.cs.beta.aeatk.probleminstance.ProblemInstanceSeedPair;
import ca.ubc.cs.beta.aeatk.runconfig.RunConfig;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorRunObserver;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluatorCallback;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.TargetAlgorithmEvaluator;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.WaitableTAECallback;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.init.TargetAlgorithmEvaluatorBuilder;
import ca.ubc.cs.beta.aeatk.targetalgorithmevaluator.init.TargetAlgorithmEvaluatorLoader;
import ca.ubc.cs.beta.dzq.options.DangerZoneQueueOptions;
import ca.ubc.cs.beta.mysqldbtae.targetalgorithmevaluator.MySQLTargetAlgorithmEvaluatorOptions;
import ca.ubc.cs.beta.mysqldbtae.util.PathStripper;

public class DangerZoneQueue {

	private static Logger log = LoggerFactory.getLogger(DangerZoneQueue.class);
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
	

		DangerZoneQueueOptions dzOpts = new DangerZoneQueueOptions();
		
		Map<String, AbstractOptions> taeOpts = TargetAlgorithmEvaluatorLoader.getAvailableTargetAlgorithmEvaluators();
		
		JCommander jcom = JCommanderHelper.parseCheckingForHelpAndVersion(args, dzOpts, taeOpts);

		try {
				
			

			VersionTracker.setClassLoader(SPIClassLoaderHelper.getClassLoader());
			VersionTracker.logVersions();
		
			for(String name : jcom.getParameterFilesToRead())
			{
				log.info("Parsing (default) options from file: {} ", name);
			}
			
			log.debug("Abort on Crash and abort on First Run Crash are disabled, as is verifySAT");
			dzOpts.taeOptions.abortOnCrash = false;
			dzOpts.taeOptions.abortOnFirstRunCrash = false;
			dzOpts.taeOptions.verifySAT = false;
			
			for(String name : taeOpts.keySet())
			{
				log.info("Target Algorithm Evaluator Available {} ", name);
			}
			
	
			ParamConfigurationSpace configSpace = ParamConfigurationSpace.getSingletonConfigurationSpace();
			
			
			
			PathStripper ps  = new PathStripper("!!!!!");
			if(dzOpts.taeOptions.targetAlgorithmEvaluator.equals("MYSQLDB"))
			{
				 ps = new PathStripper(((MySQLTargetAlgorithmEvaluatorOptions) taeOpts.get("MYSQLDB")).pathStrip);
			}
			
			AlgorithmExecutionConfiguration execConfig = new AlgorithmExecutionConfiguration(getExecutionString(dzOpts.wrapper, dzOpts.wrapperMemLimit), "/", configSpace, true, true, dzOpts.runtimeLimit);
			
			TargetAlgorithmEvaluator tae = TargetAlgorithmEvaluatorBuilder.getTargetAlgorithmEvaluator(dzOpts.taeOptions, false, dzOpts.ignoreTAEBounds, taeOpts, null);
			try {
				
				
				List<RunConfig> runConfigs = new ArrayList<RunConfig>();
				
				
				StringBuilder sb = new StringBuilder();
				for(Object o : jcom.getObjects())
				{
					sb.append(o.toString()).append("\n");
				}
					
				log.info("==========Configuration Options==========\n{}", sb.toString());
				
	
				List<String> commands = getCommands(dzOpts);
				for(String cmd : commands)
				{
					for(int i=0; i < dzOpts.execTimes; i++)
					{
						
						String cmd2 = cmd.replaceAll("\\%ID\\%", String.valueOf(i+dzOpts.idOffset));
						ProblemInstance pi = new ProblemInstance("DIR=>" + ps.stripPath(dzOpts.dir) + ";CMD=>" + ps.stripPath(cmd2)+";ENFORCETIME=>" + dzOpts.enforceTimeLimit + ";SHOWOUTPUT=>" + dzOpts.showOutput  + ";PILSLINE=>" + dzOpts.wrapperPils, "DZQFTW");
						ProblemInstanceSeedPair pisp = new ProblemInstanceSeedPair(pi, i);
						
						runConfigs.add(new RunConfig(pisp,dzOpts.runtimeLimit, configSpace.getDefaultConfiguration(), execConfig));
					}
				}
				
				
				TargetAlgorithmEvaluatorCallback callback = new TargetAlgorithmEvaluatorCallback()
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
				
				TargetAlgorithmEvaluatorRunObserver obs = new TargetAlgorithmEvaluatorRunObserver()
				{
	
					long lastUpdate = -1;
					@Override
					public void currentStatus(
							List<? extends AlgorithmRun> runs) {
						
						
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
			commandOptions++;
			
			commands = new ArrayList<String>();
			log.info("Reading commands from file {} ", dzOpts.execFile.getAbsolutePath());
			
			try {
				BufferedReader reader = new BufferedReader(new FileReader(dzOpts.execFile));
				String line;
				while( (line = reader.readLine()) != null)
				{
					
					if(line.trim().length() == 0)
					{
						continue;
					}
					if(line.trim().startsWith("#"))
					{
						continue;
					} else
					{ 
						log.debug("Parsed command line {}", line);
						commands.add(line);
					}
				}
				reader.close();
				
				
			} catch (IOException e) {
				throw new IllegalStateException("Unexpected IO Expection", e);
			}
		}
		
		
		if(commandOptions != 1)
		{
			throw new ParameterException("You must supply one and only one of --exec and --execFile");
		}
	
		return commands;
		
	}


	public static String getExecutionString(String wrapper, int wrapperMemLimit) {
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
			
				
				return AlgorithmExecutionConfiguration.MAGIC_VALUE_ALGORITHM_EXECUTABLE_PREFIX + "java -Xmx"+wrapperMemLimit+"m -cp " + root + " " + DangerZoneWrapper.class.getCanonicalName();
			
			//System.out.println(DangerZoneWrapper.class.getClassLoader().getResource(ProviderFor.class.getCanonicalName().replace(".", File.separator)  + ".class").getFile());
			
			//return wrapper;
		} else
		{
			throw new IllegalStateException("What?");
			//return wrapper;
		}
	}

}
