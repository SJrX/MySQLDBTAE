package ca.ubc.cs.beta.mysqldbtae.persistence.worker;

public class UpdatedWorkerParameters {
	private final int batchSize;
	private final int delayBetweenRequests;
	
	public UpdatedWorkerParameters(int batchSize, int delayBetweenRequests)
	{
		this.batchSize = batchSize;
		this.delayBetweenRequests = delayBetweenRequests;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public int getDelayBetweenRequests() {
		return delayBetweenRequests;
	}
	
	
}
