package ca.ubc.cs.beta.mysqldbtae.persistence.worker;

import java.sql.Date;

public class UpdatedWorkerParameters {
	private final long timeLimit;
	private final int batchSize;
	private final int delayBetweenRequests;
	private final String pool;
	
	public UpdatedWorkerParameters(long timeLimit, int batchSize, int delayBetweenRequests, String pool)
	{
		this.timeLimit = timeLimit;
		this.batchSize = batchSize;
		this.delayBetweenRequests = delayBetweenRequests;
		this.pool =pool;
	}

	public int getTimeLimit() {
		return (int)(timeLimit/1000);
	}
	
	public int getBatchSize() {
		return batchSize;
	}

	public int getDelayBetweenRequests() {
		return delayBetweenRequests;
	}

	public String getPool() {
		return pool;
	}
	
	
}
