package ca.ubc.cs.beta.mysqldbtae.migration;



import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.cs.beta.aeatk.concurrent.threadfactory.SequentiallyNamedThreadFactory;
import ca.ubc.cs.beta.aeatk.misc.jcommander.JCommanderHelper;
import ca.ubc.cs.beta.aeatk.misc.returnvalues.AEATKReturnValues;
import ca.ubc.cs.beta.aeatk.misc.version.VersionTracker;
import ca.ubc.cs.beta.aeatk.misc.watch.AutoStartStopWatch;
import ca.ubc.cs.beta.aeatk.misc.watch.StopWatch;
import ca.ubc.cs.beta.mysqldbtae.persistence.migration.MySQLPersistenceMigration;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class MigrationUtilityExecutor {

	private static Logger log;
	
	public static void main(String[] args) throws InterruptedException
	{
		
		final MigrationUtilityOptions muo = new MigrationUtilityOptions();

		try 
		{
		
			try {
			 JCommanderHelper.parseCheckingForHelpAndVersion(args, muo);
			} finally
			{
				muo.logOpts.initializeLogging();
				log = LoggerFactory.getLogger(MigrationUtilityExecutor.class);
			}
			
			log.warn("\n*******************************\n"
					+ "\tThis utility will begin modify the database directly please ensure that no workers or clients are presently using the database.\n"
					+ "\tFailure to do so may make the database recovery require manual intervention\n"
					+ "\n"
					+ "\n--==[ LIMITATIONS ]==--\n"
					+ "\t1) You should be aware that runs that are NEW or ASSIGNED presently will be reset to NEW and will lose their present runResult value.\n"
					+ "\t2) Runs that are COMPLETE and ABORT will be set as NEW afterwards\n"
					+ "\t3) You will lose information about which worker did which run.\n"
					+ "\n"
					+ "\t*This utility has paused for 60 seconds to allow you to cancel via CTRL+C or CTRL+BREAK\n"
					+ "\tTo skip this warning pause use --skip-warning-pause true"
					+ "\n*******************************");
				
			
			if(!muo.skipWarningPause)
			{
				
				try {
					Thread.sleep(60*1000);
				} catch (InterruptedException e) {
					return;
				}
			}
			
			log.info("Starting MySQL DB Target Algorithm Evaluator Database Migration Utility");
			
			StopWatch watch = new AutoStartStopWatch();
			
			MySQLPersistenceMigration mysqlPM = new MySQLPersistenceMigration(muo.mysqlOptions.host, muo.mysqlOptions.port, muo.mysqlOptions.databaseName, muo.mysqlOptions.username, muo.mysqlOptions.password, muo.pool);
			
			
			
			ExecutorService execService = Executors.newCachedThreadPool(new SequentiallyNamedThreadFactory("Migration Runners", true));
			
			try {
				mysqlPM.preMigrate();
				
				
				Runnable run = new Runnable()
				{

					@Override
					public void run() {
						MySQLPersistenceMigration mysqlPM = new MySQLPersistenceMigration(muo.mysqlOptions.host, muo.mysqlOptions.port, muo.mysqlOptions.databaseName, muo.mysqlOptions.username, muo.mysqlOptions.password, muo.pool);
						try {
							mysqlPM.fixAlgorithmRunsTable(250);
						} catch (SQLException e) {
							log.error("Unfortunately an error occurred and we couldn't continue, see the details below:", e);
						}
						
					}
					
				};
				
				for(int i=0; i < Math.min(Runtime.getRuntime().availableProcessors(),4); i++)
				{
					execService.execute(run);
				}
				execService.shutdown();
				execService.awaitTermination(24, TimeUnit.DAYS);
				
				
			} catch (SQLException e) {
				log.error("Unfortunately an error occurred and we couldn't continue, see the details below:", e);
			}
			
			
			
			
			
			
			
			log.info("MySQL DB Target Algorithm Evaluator Database Migration Utility has completed, after {} seconds", watch.time() / 1000.0 );
			
		} catch(ParameterException e)
		{
			
			log.info("Error {}", e.getMessage());
			log.trace("Exception ", e);
			System.exit(AEATKReturnValues.PARAMETER_EXCEPTION);
		} catch(RuntimeException e)
		{
			log.error("Unknown Runtime Exception ",e);
			System.exit(AEATKReturnValues.OTHER_EXCEPTION);
		} 
		
		System.exit(AEATKReturnValues.SUCCESS);
}

}
