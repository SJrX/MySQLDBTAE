package ca.ubc.cs.beta.mysqldbtae;

public enum JobPriority {
	LOW,
	NORMAL,
	HIGH,
	UBER;
	
	public int getReverseOrderIndex()
	{
		return (JobPriority.values().length - 1) - this.ordinal();
	}
}
