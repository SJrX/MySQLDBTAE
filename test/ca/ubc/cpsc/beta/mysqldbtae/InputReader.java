package ca.ubc.cpsc.beta.mysqldbtae;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class InputReader implements Runnable
{
	private final InputStream t;
	private final String prefix;
	private final LineHandler h;

	public interface LineHandler
	{
		public void processLine(String line);
		
	}
	public static final void createReadersForProcess(Process proc)
	{
		Thread t = new Thread(new InputReader(proc.getInputStream()));
		t.setDaemon(false);
		t.start();
		
		t = new Thread(new InputReader(proc.getErrorStream()));
		t.setDaemon(false);
		t.start();
		
	}
	
	
	public static final void createReadersForProcess(Process proc, String prefix)
	{
		Thread t = new Thread(new InputReader(proc.getInputStream(),prefix));
		t.setDaemon(false);
		t.start();
		
		t = new Thread(new InputReader(proc.getErrorStream(),prefix));
		t.setDaemon(false);
		t.start();
		
	}
	
	public static final void createReadersForProcess(Process proc, String prefix, LineHandler h)
	{
		Thread t = new Thread(new InputReader(proc.getInputStream(),prefix, h));
		t.setDaemon(false);
		t.start();
		
		t = new Thread(new InputReader(proc.getErrorStream(),prefix));
		t.setDaemon(false);
		t.start();
		
		
	}
	
	
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
		this(t, "SERVER>");
		
		
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
		this.prefix=prefix;
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
		
		while(procIn.hasNextLine())
		{
			String line = procIn.nextLine();
			
			System.out.println(prefix + line);
			h.processLine(line);
		}
		
		System.out.flush();
		try {
			t.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}