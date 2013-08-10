package ca.ubc.cs.beta.mysqldbtae.persistence.worker;

public class UpdatedWorkerParameters {
	private final long timeLimit;
	private final int batchSize;
	private final int delayBetweenRequests;
	private final String pool;
	private final int poolIdleTimeLimit;
	
	public UpdatedWorkerParameters(long timeLimit, int batchSize, int delayBetweenRequests, String pool, int poolIdleTimeLimit)
	{
		this.timeLimit = timeLimit;
		this.batchSize = batchSize;
		this.delayBetweenRequests = delayBetweenRequests;
		this.pool =pool;
		this.poolIdleTimeLimit = poolIdleTimeLimit;
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
	
	public int getPoolIdleTimeLimit() {
		return poolIdleTimeLimit;
	}
}
