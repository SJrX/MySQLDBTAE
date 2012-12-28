package ca.ubc.cs.beta.mysqldbtae.version;

import org.mangosdk.spi.ProviderFor;

import ca.ubc.cs.beta.aclib.misc.version.AbstractVersionInfo;
import ca.ubc.cs.beta.aclib.misc.version.VersionInfo;

@ProviderFor(VersionInfo.class)
public class MySQLDBTAEVersionInfo extends AbstractVersionInfo {

	public MySQLDBTAEVersionInfo() {
		super("MySQL Database Target Algorithm Evaluator", "mysqldbtae-version.txt", true);
	}

}
