package jagoclient.igs.who;

import jagoclient.igs.Distributor;
import jagoclient.igs.IgsStream;

/**
A distributor for the player listing.
*/

public class WhoDistributor extends Distributor
{	WhoFrame P;
	public WhoDistributor (IgsStream in, WhoFrame p)
	{	super(in,27,0,true);
		P=p;
	}
	public void send (String c)
	{	P.receive(c);
	}
	public void allsended ()
	{	P.allsended();
	}
}
