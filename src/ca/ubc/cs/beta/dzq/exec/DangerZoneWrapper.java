package ca.ubc.cs.beta.dzq.exec;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/***
 * A basic wrapper class
 * 
 * NOTE: This class cannot have any dependencies to anything outside of the JRE
 * as it will be invoked without anything on it's classpath
 * 
 * @author Steve Ramage <seramage@cs.ubc.ca>
 */
public class DangerZoneWrapper {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String seed = "0";
		try 
		{
			
			//Get arguments
			System.out.println(Arrays.toString(args));
			String instanceName = args[0];
			//String instanceSpecificInfo = args[1];
			final double cutoffTime = Double.valueOf(args[2]);
			//String cutoffLength = args[3];
			seed = args[4];
			
			
			StringBuilder sb = new StringBuilder();
			for(int i=5; i < args.length; i++)
			{
				sb.append(args[i]).append(" ");
			}
			
			String[] pathExec = instanceName.split(";");
			
			
			//Get Map encoded arguments
			Map<String, String> argString = new HashMap<String, String>();
			for(String keyVal : pathExec)
			{
				
				String[] vals = keyVal.split("=>");
				if(vals.length != 2)
				{
					throw new IllegalStateException("Corrupted encoding in command arguments");
				}
				argString.put(vals[0].trim(), vals[1]);
				
			}
			
			
			
			
			final long startTime = System.currentTimeMillis();
			final Process p = Runtime.getRuntime().exec(DangerZoneWrapper.splitQuotedString(argString.get("CMD").replaceAll("%ID", seed)),null, new File(argString.get("DIR")));
			
			
			/**********
			 * DO NOT INCLUDE ANY OTHER CLASSES IN THIS FILE FROM ACLIB, ETC WE HAVE NO IDEA WHERE IT IS
			 * AND THIS WILL BREAK SILENTLY
			 * 
			 */
			//new SequentiallyNamedThreadFactory("Danger Zone Queue Thread")
			ExecutorService execService = Executors.newCachedThreadPool();
			
			
			String output = argString.get("SHOWOUTPUT").trim();
			String prefix = "OUT>";
			
			boolean pilsLine = true;
			if((argString.get("PILSLINE") != null) && !Boolean.valueOf(argString.get("PILSLINE")))
			{
				prefix ="";
				pilsLine = false;
			}
			final String finalPrefix = prefix;
			
			LineHandler lh = new LineHandler()
			{

				@Override
				public void processLine(String line) {

					
				}
				
			};
			
			if(output != null && Boolean.valueOf(output))
			{
				lh = new LineHandler()
				{

					@Override
					public void processLine(String line) {
						System.out.println(finalPrefix + line);
					}
					
				};
				
			} 
			
			
		
			
			InputReader r = new InputReader(p.getInputStream(), lh);
			InputReader r2 = new InputReader(p.getErrorStream(), lh);
			
			
			execService.submit(r);
			execService.submit(r2);
			
			final AtomicReference<Boolean> timeoutReference = new AtomicReference<Boolean>();
			
			
			if((argString.get("ENFORCETIME") != null) && Boolean.valueOf(argString.get("ENFORCETIME")))
			{
				execService.submit(new Runnable() 
				{

					@Override
					public void run() {
						while(true)
						{
							try {
								Thread.sleep(1000);
								if ((System.currentTimeMillis() / 1000.0  - startTime / 1000.0) > Double.valueOf(cutoffTime) + 1)
								{
									if(timeoutReference.compareAndSet(null, true));
									{
										p.destroy();
										p.waitFor();
									}
									
									//If we get here then we mine as well return no matter what since we only ever set a value 
									
									return;
								}
								
								
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
								return;
							}
							
							
						}
						
					}
					
					
					
				});
				
				
			}
			
			
			
			p.waitFor();
			timeoutReference.compareAndSet(null, false);
			
			execService.shutdownNow();
			
			long endTime = System.currentTimeMillis();
			
			double runtime = (endTime - startTime)/1000.0;
			
			
			long runlength =0;
			long quality = p.exitValue();;
			long resultSeed = Long.valueOf(seed);

			String runResult;
			
			if(timeoutReference.get())
			{
				runResult = "TIMEOUT";
			} else{
				if(quality == 0)
				{
					runResult = "SAT";
				} else
				{
					runResult = "CRASHED";
				}
			}
			
			if(pilsLine)
			{
				System.out.println("Result for ParamILS: "+runResult+"," + runtime + "," + runlength + "," + quality + "," + resultSeed + "\n");
			}
			
		} catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Result for ParamILS: CRASHED, 0.000 ,0 ,0 ," + seed + ",  " + e.getClass().getCanonicalName() + "\n");
		}
		
		
	}
	
	
	/** 
     * Splits a string based on spaces, grouping atoms if they are inside non escaped double quotes.
     * 
     */
    static private List<String> goodSplitQuotedString(String str)
    {
        List<String> strings = new ArrayList<String>();
        boolean inQuotes = false;
        boolean quoteStateChange = false;
        StringBuffer buffer = new StringBuffer();
        //Find some spaces, 
        for(int i = 0; i < str.length(); i++){
            //Have we toggled the quote state?
            char c = str.charAt(i);
            quoteStateChange = false;
            if(c == '"' && (i == 0 || str.charAt(i-1) != '\\')){
                inQuotes = !inQuotes;
                quoteStateChange = true;
            }
            //Peek at the next character - if we have a \", we need to only insert a "
            if(c == '\\' && i < str.length()-1 && str.charAt(i+1) == '"'){
                c = '"';
                i++;
            }

            //If we're not in quotes, and we've hit a space...
            if(!inQuotes && str.charAt(i) == ' '){
                //Do we actually have somthing in the buffer?
                if(buffer.length() > 0){
                    strings.add(buffer.toString());
                    buffer.setLength(0);
                }
            }else if(!quoteStateChange){
                //We only want to add stuff to the buffer if we're forced to by quotes, or we're not a "
                buffer.append(c);
            }
        }
        //Add on the last string if needed
        if(buffer.length() > 0){
            strings.add(buffer.toString());
        }

        return strings;
    }

    public void splitTest(){
    	for(int i=0; i < 145; i++)
    	{
    		for(String s : splitQuotedString(" This is \"my split\" string\" \"with lots o\' fish \\\"and even escaped\\\" ")){
                System.out.println(s + ",");
            }
    	}
        
    }
    
    
    public static String[] splitQuotedString(String s)
    {
    	return new ArrayList<String>(goodSplitQuotedString(s)).toArray(new String[0]);
    }
    
    
	
	
	
	public interface LineHandler
	{
		public void processLine(String line);
		
	}

	static public class InputReader implements Runnable
	{
		private final InputStream t;
		
		private final LineHandler h;

		
		
		
		public InputReader(InputStream t)
		{
			this(t, new LineHandler() {

				@Override
				public void processLine(String line) {
				}
				
				
			});
		}
		
		public InputReader(InputStream t, LineHandler h)
		{
			this(t, "SERVER>", h);
			
			
		}
		
		public InputReader(InputStream t, String prefix)
		{
			this(t, prefix, new LineHandler() {

				@Override
				public void processLine(String line) {
				}
				
				
			});
		}
		
		public InputReader(InputStream t, String prefix, LineHandler h)
		{
			this.t = t;
			//this.prefix=prefix;
			this.h = h;
			if(h == null)
			{
				throw new IllegalArgumentException("Line Handler cannot be null");
			}
			
			if(t == null)
			{
				throw new IllegalArgumentException("Input Stream cannot be null");
			}
			
			if(prefix == null)
			{
				throw new IllegalArgumentException("Prefix cannot be null");
			}
		}

		@Override
		public void run() {
			Scanner procIn = new Scanner(t);
			
			try {
				try {
					while(procIn.hasNextLine())
					{
						String line = procIn.nextLine();
						
						h.processLine(line);
						
						if(Thread.currentThread().isInterrupted())
						{
							return;
							
						}
					}
				} finally {
					t.close();
				}
					
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
