package ca.ubc.cpsc.beta.mysqldbtae;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class InputReader implements Runnable
{
	private final InputStream t;
	private final String prefix;

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
	
	public InputReader(InputStream t)
	{
		this(t, "SERVER>");
		
		
	}
	
	public InputReader(InputStream t, String prefix)
	{
		this.t = t;
		this.prefix=prefix;
	}

	@Override
	public void run() {
		Scanner procIn = new Scanner(t);
		
		while(procIn.hasNextLine())
		{
			System.out.println(prefix + procIn.nextLine());
			
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