package ca.ubc.cs.beta.mysqldbtae.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration;
import ca.ubc.cs.beta.aclib.configspace.ParamConfiguration.StringFormat;
import ca.ubc.cs.beta.aclib.execconfig.AlgorithmExecutionConfig;

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
	
	public String getHash(AlgorithmExecutionConfig execConfig)
	{
		MessageDigest digest = DigestUtils.getSha1Digest();
		
		try {
			byte[] result = digest.digest( (execConfig.getAlgorithmExecutable() + execConfig.getAlgorithmExecutionDirectory() + execConfig.getAlgorithmCutoffTime() + execConfig.isDeterministicAlgorithm() + execConfig.isExecuteOnCluster() + execConfig.getParamFile().getParamFileName()).getBytes("UTF-8"));
			return new String(Hex.encodeHex(result));

		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("Could not encode Algorithm Execution Config " + execConfig);
		}
	}
}
