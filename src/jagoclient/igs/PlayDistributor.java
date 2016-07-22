package jagoclient.igs;

import jagoclient.Global;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
The PlayDistributor is opened with a ConnectionFrame to display
tha board. When its game method is invoked, it will start a Player
object and send output to it.
@see jagoclient.igs.Player
*/

public class PlayDistributor extends Distributor
{	private static final Logger LOG = Logger.getLogger(PlayDistributor.class.getName());
	Player P;
	IgsStream In;
	PrintWriter Out;
	ConnectionFrame F;
	public PlayDistributor (ConnectionFrame f, IgsStream in, PrintWriter out)
	{	super(in,15,-1,null,null);
		F=f;
		In=in; Out=out;
		P=null;
		Playing=true;
	}
	public void send (String c)
	{	if (P!=null) P.receive(c);
	}
	public void remove ()
	{	if (P!=null) P.remove();
		out("adjourn "+G);
	}
	/**
	This method opens an IgsGoFrame and a Player to handle the
	moves.
	*/
	public void game (int n)
	{	LOG.log(Level.INFO, "Opening go frame for game {0}", n);
		IgsGoFrame gf=new IgsGoFrame(F,Global.resourceString("Play_game"));
		gf.distributor(this);
		gf.Playing.setState(true);
		gf.setVisible(true); gf.repaint();
		P=new Player(gf,In,Out,this);
		G=n;
		P.game(n);
		new PlayDistributor(F,In,Out);
	}
	/** called from the goframe to set a move (passed to Player) */
	public void set (int i, int j, int sec)
	{	LOG.log(Level.INFO, "Play Distributor got move at {0},{1}", new Object[]{i, j});
		P.set(i,j,sec);
	}
	/** called from the goframe to pass (passed to Player) */
	public void pass ()
	{	LOG.info("Play Distributor got a pass");
		P.pass();
	}
	/** called from the goframe to refresh the board (passed to Player) */
	public void refresh ()
	{	P.refresh();
	}
	public boolean started ()
	{	return P.started();
	}
}

