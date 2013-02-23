package ca.ubc.cs.beta.mysqldbtae.persistence.worker;

public class UpdatedWorkerParameters {
	private final int batchSize;
	private final int delayBetweenRequests;
	private final String pool;
	
	public UpdatedWorkerParameters(int batchSize, int delayBetweenRequests, String pool)
	{
		this.batchSize = batchSize;
		this.delayBetweenRequests = delayBetweenRequests;
		this.pool =pool;
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
