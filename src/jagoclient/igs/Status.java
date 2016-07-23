package jagoclient.igs;

import java.io.PrintWriter;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rene.util.parser.StringParser;

/**
 * This interprets, what the server send in response to a status command
 * (or to the problem command). It gets a GoFrame to display the game status.
 */
public class Status implements Distributor.Task
{	private static final Logger LOG = Logger.getLogger(Status.class.getName());
	protected static final Pattern WORD_PATTERN = Pattern.compile("\\w+");
	IgsGoFrame GF;
	IgsStream In;
	StatusDistributor PD;
	String Black,White;
	int L;
	
	/**
	Sends a status command to the server for game n.
	@param n the game number.
	*/
	public Status (IgsGoFrame gf, IgsStream in, PrintWriter out, int n)
	{	GF=gf; In=in;
		GF.active(false);
		PD=new StatusDistributor(in,this);
		out.println("status "+n);
		L=1;
	}
	
	/**
	A status object for an unknown game. This is used, when the
	status command has already been sent to the server.
	*/
	public Status (IgsGoFrame gf, IgsStream in, PrintWriter out)
	{	GF=gf; In=in;
		GF.active(false);
		PD=new StatusDistributor(in,this);
		L=1;
	}
	
	private String getname (String s)
	{	Matcher matcher = WORD_PATTERN.matcher(s);
		String name = null, title = null;
		if (matcher.find())
			name = matcher.group();
		if (matcher.find())
			title = matcher.group();
		return name+" ("+title+")";
	}

	/** 
	This is called from the StatusDistributor.
	The output is interpreted and the go frame is updated.
	*/
	public void receive (String s)
	{	if (L==1) Black=s;
		else if (L==2)
		{	White=s;
			GF.settitle(getname(Black)+" - "+getname(White));
		}
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
					if (c=='0') GF.setblack(n,i);
					else if (c=='1') GF.setwhite(n,i);
					else if (c=='4' || c=='5') GF.territory(n,i);
					i++;
				}
				if (i!=GF.getboardsize())
				{	if (i<5 || i>29) break;
					GF.doboardsize(i);
				}
				else break;
			}
		}
		L++;
	}

	/**
	 * When the board status is complete, displays the GoFrame window.
	 */
	@Override
	public void finished ()
	{	LOG.info("Status is finished");
		GF.setVisible(true);
		GF.active(true);
		// GF.B.showinformation();
		// GF.B.repaint();
	}

}
