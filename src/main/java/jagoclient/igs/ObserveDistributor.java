package jagoclient.igs;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
This distributor takes moves from the IgsStream and sends them
to a GoObserver, which is opened elswhere.
*/

public class ObserveDistributor extends Distributor
{	private static final Logger LOG = Logger.getLogger(ObserveDistributor.class.getName());
	GoObserver P;
	boolean Blocked;
	public ObserveDistributor (IgsStream in, GoObserver p, int n)
	{	super(in,15,n,null,null);
		P=p;
		Blocked=true;
	}
	public void send (String c)
	{	P.receive(c);
	}
	public void remove ()
	{	P.remove();
	}
	public boolean blocked ()
	{	if (Playing) return false;
		else return Blocked;
	}
	public void refresh ()
	{	P.refresh();
	}
	public void allsended ()
	{	P.sended();
	}
	public boolean started ()
	{	return P.started();
	}
	public void set (int i, int j)
	{	LOG.log(Level.INFO, "Observe Distributor got move at {0},{1}", new Object[]{i, j});
		P.set(i,j);
	}
	public void pass ()
	{	LOG.info("Observe Distributor got a pass");
		P.pass();
	}
	public boolean newmove ()
	{   return P.newmove();
	}
}

