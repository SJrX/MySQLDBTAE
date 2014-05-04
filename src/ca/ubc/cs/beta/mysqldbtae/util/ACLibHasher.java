package ca.ubc.cs.beta.mysqldbtae.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import ca.ubc.cs.beta.aeatk.algorithmexecutionconfiguration.AlgorithmExecutionConfiguration;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration;
import ca.ubc.cs.beta.aeatk.parameterconfigurationspace.ParameterConfiguration.ParameterStringFormat;

public class ACLibHasher {

	public String getHash(ParameterConfiguration config)
	{
		MessageDigest digest = DigestUtils.getSha1Digest();
		
		try {
			byte[] result = digest.digest(config.getFormattedParameterString(ParameterStringFormat.NODB_SYNTAX).getBytes("UTF-8"));
			return new String(Hex.encodeHex(result));

		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Could not encode Param Configuration : " + config.getFormattedParameterString(ParameterStringFormat.NODB_SYNTAX), e);
		}
	}
	
	public String getHash(AlgorithmExecutionConfiguration execConfig, PathStripper pathStrip)
	{
		try {
			MessageDigest digest = DigestUtils.getSha1Digest();
		
		
		try {
			byte[] result = digest.digest( (pathStrip.stripPath(execConfig.getAlgorithmExecutable()) +
											pathStrip.stripPath(execConfig.getAlgorithmExecutionDirectory()) + 
											execConfig.getAlgorithmMaximumCutoffTime() + 
											execConfig.isDeterministicAlgorithm() + 
											false +
											pathStrip.stripPath(execConfig.getParameterConfigurationSpace().getParamFileName()) +
											((execConfig.getTargetAlgorithmExecutionContext().size() > 0) ? execConfig.getTargetAlgorithmExecutionContext().toString() : "")).getBytes("UTF-8")); //Want to keep hashes backwards compatible with old versions
			return new String(Hex.encodeHex(result));

		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Could not encode Algorithm Execution Config " + execConfig);
		}
		
		} catch(NoSuchMethodError e)
		{
			System.err.println(this.getClass().getClassLoader().getResource("org/apache/commons/codec/digest/DigestUtils.class").getPath());
			throw e;
		}
	}
}
