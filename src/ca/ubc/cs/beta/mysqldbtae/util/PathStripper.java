package ca.ubc.cs.beta.mysqldbtae.util;

public class PathStripper {

	public final String pathStrip;
	
	public PathStripper(String s)
	{
		this.pathStrip = s;
		
	}
	
	public String stripPath(String s)
	{
		if(pathStrip == null)
		{
			return s;
		} else if(s.startsWith(pathStrip))
		{
			 return s.substring(pathStrip.length());
			
		} else
		{
			return s;
		}
		
	
	}

}
