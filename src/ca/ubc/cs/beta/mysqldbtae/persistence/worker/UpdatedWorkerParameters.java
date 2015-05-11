package ca.ubc.cs.beta.mysqldbtae.persistence.worker;

public class UpdatedWorkerParameters {
	private final long timeLimit;
	private final int batchSize;
	private final int delayBetweenRequests;
	private final String pool;
	private final int poolIdleTimeLimit;
	private final int concurrencyFactor;
	private final int maxRunsToBatch;
	private final int minRunsToBatch;
	private final boolean autoTuneBatchSize;
	
	public UpdatedWorkerParameters(long timeLimit, int batchSize, int delayBetweenRequests, String pool, int poolIdleTimeLimit, int concurrencyFactor, int minRunsToBatch, int maxRunsToBatch, boolean autoTuneBatchSize)
	{
		this.timeLimit = timeLimit;
		this.batchSize = Math.max(1,batchSize);
		this.delayBetweenRequests = delayBetweenRequests;
		this.pool =pool;
		this.poolIdleTimeLimit = poolIdleTimeLimit;
		this.concurrencyFactor = concurrencyFactor;
		this.minRunsToBatch = Math.max(1, Math.min(batchSize, minRunsToBatch));
		this.maxRunsToBatch = Math.max(1, Math.max(batchSize, maxRunsToBatch));
		this.autoTuneBatchSize = autoTuneBatchSize;
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

	public int getConcurrencyFactor() {
		return concurrencyFactor;
	}

	public int getMaxRunsToBatch() {
		return maxRunsToBatch;
	}

	public int getMinRunsToBatch() {
		return minRunsToBatch;
	}

	public boolean getAutoTuneBatchSize() {
		return autoTuneBatchSize;
	}
	
	
}
