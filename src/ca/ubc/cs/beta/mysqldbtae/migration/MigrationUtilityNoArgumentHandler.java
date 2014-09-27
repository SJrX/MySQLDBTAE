package ca.ubc.cs.beta.mysqldbtae.migration;

import ca.ubc.cs.beta.aeatk.misc.options.NoArgumentHandler;

public class MigrationUtilityNoArgumentHandler implements NoArgumentHandler{

	@Override
	public boolean handleNoArguments() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("mysqldbtae-migrate is a utility that that upgrades older versions of the database if possible and subsequently repairs hashes.").append("\n\n");

		sb.append("  Basic Usage:\n\n");
		
		sb.append(" * Assuming you have the MySQL Options setup in the ~/.aeatk/mysql.opt file\n");
		sb.append(" ./mysqldbtae-migrate --pool <pool>\n\n");
		
		sb.append("  Details:\n");
		sb.append("\t<pool> The pool to upgrade.\n");
		
		
			  
	
		System.out.println(sb);
		return true;
	}

}
