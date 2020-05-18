package jagoclient.igs;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
This is a specified IGS stream, which reads byte over a telnet proxy.
Thus telnet commands must be filtered. Consequently, it must use
a byte stream and cannot translate into locales.
*/

public class ProxyIgsStream extends IgsStream
{	private static final Logger LOG = Logger.getLogger(ProxyIgsStream.class.getName());
	DataInputStream In;
	
	public ProxyIgsStream (ConnectionFrame cf, InputStream in, 
		PrintWriter out)
	{	super(cf,in,out);
	}
		
	public void initstream (InputStream in)
	{	In=new DataInputStream(in);
	}

	public char read () throws IOException
	{	while (true)
		{	byte c=In.readByte();
			if (c==-1) // Telnet ??
			{	c=In.readByte();
				LOG.log(Level.INFO, "Telnet received!{0}", (256+c));
				if (c==-3)
				{	c=In.readByte();
					CF.Outstream.write(255);
					CF.Outstream.write(252); 
					CF.Outstream.write(c);
					continue;
				}
				else if (c==-5)
				{	c=In.readByte();
					continue;
				}
			}
			if (c==10)
			{	if (lastcr==13)
				{	lastcr=0;
					continue;
				}
				lastcr=10;
				return '\n';
			}
			else if (c==13)
			{	if (lastcr==10)
				{	lastcr=0;
					continue;
				}
				lastcr=13;
				return '\n';
			}
			return (char)c;
		}
	}
	
	@Override
	public boolean available ()
	{	try
		{	return (In.available()>0);
		}
		catch (IOException e) { return false; }
	}

	@Override
	public void close () throws IOException
	{	In.close();
	}

}
