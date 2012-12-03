package ca.ubc.cs.beta.mysqldbtae.version;

import org.mangosdk.spi.ProviderFor;

import ca.ubc.cs.beta.aclib.misc.version.VersionInfo;

@ProviderFor(VersionInfo.class)
public class MySQLDBTAEVersionInfo implements VersionInfo{

	@Override
	public String getProductName() {		
		return "MySQL Database Target Algorithm Evaluator";
	}

	@Override
	public String getVersion() {	
		return "v0.10b";
	}


}
