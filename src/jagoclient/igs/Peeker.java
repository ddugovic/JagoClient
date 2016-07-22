package jagoclient.igs;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import rene.util.parser.StringParser;

class PeekerSizeDistributor extends Distributor
{	Peeker P;
	public PeekerSizeDistributor (IgsStream in, Peeker p)
	{	super(in,22,0,p,null);
		P=p;
	}
	public void send (String c)
	{	P.receivesize(c);
	}
}

class PeekDistributor extends Distributor
{	Peeker P;
	public PeekDistributor (IgsStream in, Peeker p, int n)
	{	super(in,15,n,null,p);
		P=p;
	}
	public void send (String c)
	{	P.receive(c);
	}
	public void remove ()
	{	P.remove();
	}
}

/**
The Peeker class is much like a player, but it does follow
a game. Thus there is no complication about the order
of moves.
@see jagoclient.igs.Player
*/

public class Peeker implements Distributor.Task, Distributor.SizeTask
{	private static final Logger LOG = Logger.getLogger(Peeker.class.getName());
	IgsGoFrame GF;
	IgsStream In;
	PrintWriter Out;
	PeekDistributor PD;
	int N,L,BS;
	String Black,White;
	
	public Peeker (IgsGoFrame gf, IgsStream in, PrintWriter out, int n)
	{	GF=gf; In=in;
		GF.active(false);
		new PeekerSizeDistributor(in,this);
		out.println("status "+n);
		Out=out;
		N=n; L=1; BS=19;
	}

	@Override
	public void sizefinished ()
	{	if (BS!=19) GF.doboardsize(BS);
		setinformation();
		PD=new PeekDistributor(In,this,N);
		Out.println("moves "+N);
	}

	void receivesize (String s)
	{	if (L==1) White=s;
		else if (L==2) Black=s;
		else
		{	while (true)
			{	StringParser p=new StringParser(s);
				p.skipblanks();
				if (!p.isint()) return;
				int n=p.parseint(':');
				if (p.error()) return;
				if (!p.skip(":")) return;
				p.skipblanks();
				char c;
				int i=0;
				while (!p.error())
				{	c=p.next();
					i++;
				}
				if (i!=BS)
				{	if (i<5 || i>29) break;
					BS=i;
				}
				else break;
			}
		}
		L++;
	}

	void receive (String s)
	{	StringParser p;
		int nu,i,j;
		LOG.log(Level.INFO, "Peeked: {0}", s);
		p=new StringParser(s);
		p.skipblanks();
		if (!p.isint())
		{	if (s.startsWith("Game"))
			GF.settime(s);
			return;
		}
		nu=p.parseint('(');
		if (p.error()) return;
		p.skipblanks();
		p.skip("(");
		String c=p.parseword(')');
		if (p.error()) return;
		p.skip(")");
		p.skipblanks();
		p.skip(":");
		String m=p.parseword();
		if (m.length()<2) return;
		if (m.equals("Pass"))
		{	GF.pass();
			return;
		}
		if (m.equals("Handicap"))
		{	int hn;
			p.skipblanks();
			hn=p.parseint();
			LOG.log(Level.INFO, "Peeker read: Handicap {0}", hn);
			GF.handicap(hn);
			return;
		}
		i=m.charAt(0)-'A';
		if (i>=9) i--;
		try
		{	j=Integer.parseInt(m.substring(1))-1;
		}
		catch (NumberFormatException e)
		{	j=-1;
		}
		if (i<0 || j<0) return;
		LOG.log(Level.INFO, "Peeker read: {0} {1},{2}", new Object[]{c, i, j});
		if (c.equals("W")) GF.white(i,BS-1-j);
		else GF.black(i,BS-1-j);
	}

	@Override
	public void finished ()
	{	LOG.info("Peeker is finished");
		GF.active(true);
	}

	void remove ()
	{	PD=null;
	}

    public void setinformation ()
    {	StringParser p;
    	p=new StringParser(Black);
    	String BlackPlayer=p.parseword();
    	String BlackRank=p.parseword();
    	p.parseword(); p.parseword(); p.parseword(); p.parseword();
    	String Komi=p.parseword();
    	String Handicap=p.parseword();
    	p=new StringParser(White);
    	String WhitePlayer=p.parseword();
    	String WhiteRank=p.parseword();
    	GF.setinformation(BlackPlayer,BlackRank,WhitePlayer,WhiteRank,Komi,Handicap);
    }
}

