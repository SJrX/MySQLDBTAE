package ca.ubc.cs.beta.mysqldbtae.migration;

import ca.ubc.cs.beta.aeatk.misc.options.NoArgumentHandler;

public class MigrationUtilityNoArgumentHandler implements NoArgumentHandler{

	@Override
	public boolean handleNoArguments() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("mysqldbtae-repair is a utility that that upgrades older versions of the database if possible and subsequently repairs hashes.").append("\n\n");

		
		sb.append("  Basic Usage(*):\n\n");
		
		sb.append("\n");
		sb.append(" ./mysqldbtae-repair --pool <pool> --mysqldbtae-database <databaseName> \n\n");

		
		sb.append(" \n");
		sb.append(" ./mysqldbtae-repair --all-pools true --mysqldbtae-database <databaseName>\n\n");
		
		
		sb.append("  Resuming Hash Repair(*):\n");
		sb.append(" ./mysqldbtae-repair --pool <pool> --reset-all-run-hashes false \n\n");
		
	
		sb.append("  Details:\n");
		sb.append("\t<pool> The pool to upgrade.\n");
		sb.append("\t<database> The database the pool(s) exist in\n");
		sb.append("* Assuming you have the MySQL Options setup in the ~/.aeatk/mysql.opt file, otherwise you will need to specify other arguments like --mysqldbtae-host ,etc...");
		

		
			  
	
		System.out.println(sb);
		return true;
	}

}
