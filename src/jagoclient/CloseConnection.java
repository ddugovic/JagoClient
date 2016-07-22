package jagoclient;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CloseConnection implements Runnable
{	private static final Logger LOG = Logger.getLogger(CloseConnection.class.getName());
	Socket S;
	BufferedReader In;
	public CloseConnection (Socket s, BufferedReader in)
	{	S=s; In=in;
	}
	@Override
	public void run ()
	{	try
		{	if (S!=null) S.close();
			if (In!=null) In.close();
		}
		catch (IOException ex)
		{	LOG.log(Level.WARNING, null, ex);
		}
	}
}