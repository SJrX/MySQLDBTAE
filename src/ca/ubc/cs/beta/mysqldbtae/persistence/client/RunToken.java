package ca.ubc.cs.beta.mysqldbtae.persistence.client;

/**
 * Token class that is returned for a set of runs
 * Can be used to query the status or get the results later.
 * <br/>
 * NOTE: This token is suppose to have nothing but identity 
 * semantics for other users, there should be no publicly
 * accessible methods.
 * 
 * @author sjr
 *
 */
public final class RunToken {

	private final int token;

	RunToken(int token)
	{
		this.token = token;
	}


	int getToken()
	{
		return token;
	}
	
	public String toString()
	{
		return "Token:<"+token+">";
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof RunToken)
		{
			return (((RunToken) o).token == this.token);
		} 
		return false;
	}
	
	public int hashCode()
	{
		return this.token;
	}
}
