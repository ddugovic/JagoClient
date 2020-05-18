package jagoclient.igs;

import java.io.PrintWriter;

/**
This distributor opens a ChannelDialog for channel n. IgsStream
sorts out the channels and calls the correct distributor.
@see jagoclient.igs.ChannelDialog
*/
public class ChannelDistributor extends Distributor
{	ConnectionFrame CF;
	PrintWriter Out;
	ChannelDialog CD;
	public ChannelDistributor
		(ConnectionFrame cf, IgsStream in, PrintWriter out, int n)
	{	super(in,32,n,null,null);
		CF=cf; Out=out;
		CD=new ChannelDialog(CF,Out,game(),this);
	}
	public void send (String s)
	{	if (CD==null)
		{	CD=new ChannelDialog(CF,Out,game(),this);
		}
		CD.append(s);
	}
}

