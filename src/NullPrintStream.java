import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;


public class NullPrintStream extends PrintStream {

	
	public NullPrintStream() throws IOException 
	{
		
		super(File.createTempFile("dummy", "file-deleteMe"));
		
	}
	
	@Override
	public void write(byte[] b, int off, int len)
	{
		
	}
	
	@Override
	public void write(int b)
	{
		
	}

}
