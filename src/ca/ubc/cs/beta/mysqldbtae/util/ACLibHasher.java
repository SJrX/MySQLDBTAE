package ca.ubc.cs.beta.mysqldbtae.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import ca.ubc.cs.beta.aeatk.configspace.ParamConfiguration;
import ca.ubc.cs.beta.aeatk.configspace.ParamConfiguration.StringFormat;
import ca.ubc.cs.beta.aeatk.execconfig.AlgorithmExecutionConfiguration;

public class ACLibHasher {

	public String getHash(ParamConfiguration config)
	{
		MessageDigest digest = DigestUtils.getSha1Digest();
		
		try {
			byte[] result = digest.digest(config.getFormattedParamString(StringFormat.NODB_SYNTAX).getBytes("UTF-8"));
			return new String(Hex.encodeHex(result));

		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Could not encode Param Configuration : " + config.getFormattedParamString(StringFormat.NODB_SYNTAX), e);
		}
	}
	
	public String getHash(AlgorithmExecutionConfiguration execConfig, PathStripper pathStrip)
	{
		try {
			MessageDigest digest = DigestUtils.getSha1Digest();
		
		
		try {
			byte[] result = digest.digest( (pathStrip.stripPath(execConfig.getAlgorithmExecutable()) + pathStrip.stripPath(execConfig.getAlgorithmExecutionDirectory()) + execConfig.getAlgorithmMaximumCutoffTime() + execConfig.isDeterministicAlgorithm() + false + pathStrip.stripPath(execConfig.getParameterConfigurationSpace().getParamFileName())).getBytes("UTF-8"));
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
