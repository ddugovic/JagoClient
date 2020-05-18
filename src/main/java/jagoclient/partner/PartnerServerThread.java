package jagoclient.partner;

import jagoclient.Global;
import jagoclient.datagram.DatagramMessage;
import jagoclient.partner.partner.Partner;

import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import rene.util.list.ListElement;

/**
This is the server thread for partner connections. If anyone connects
to the server, a new PartnerFrame will open to handle the connection.
If the server starts, it will open a new PartnerServerThread, which
checks for datagrams that announce open partners.
*/

public class PartnerServerThread extends Thread
{	private static final Logger LOG = Logger.getLogger(PartnerServerThread.class.getName());
	int Port;
	boolean Public;
	private static PartnerServer PST=null;
	ServerSocket SS;
	/**
	@param p the server port
	@param publ server is public or not
	*/
	public PartnerServerThread (int p, boolean publ)
	{	Port=p; Public=publ;
		start();
	}
	public void run ()
	{	if (PST==null)
		{	PST=new PartnerServer(Global.getParameter("serverport",6970)+2);
			new Thread(PST).start();
		}
		try { sleep(1000); } catch (Exception e) {}
		try
		{	SS=new ServerSocket(Port);
			while (true)
			{	Socket S=SS.accept();
				if (Global.Busy) // user set the busy checkbox
				{	PrintWriter o=new PrintWriter(
						new DataOutputStream(S.getOutputStream()),true);
					o.println("@@busy");
					S.close();
					continue;
				}
				PartnerFrame cf = new PartnerFrame(Global.resourceString("Server"),true);
				Global.setwindow(cf,"partner",500,400);
				cf.setVisible(true);
				cf.open(S);
			}
		}
		catch (Exception e)
		{	LOG.warning("Server Error");
		}
	}
	
	/**
	This is called, when the server is opened. It will announce
	the opening to known servers by a datagram.
	*/
	public void open ()
	{	if (Public)
		{	
			for (Partner partner : Global.PartnerList)
			{	if (partner.State>0)
				{	DatagramMessage d=new DatagramMessage();
					d.add("open");
					d.add(Global.getParameter("yourname","Unknown"));
					try
					{	String s=InetAddress.getLocalHost().toString();
						d.add(s.substring(s.lastIndexOf('/')+1));
					}
					catch (Exception e) { d.add("Unknown Host"); }
					d.add(""+Global.getParameter("serverport",6970));
					d.add(""+partner.State);
					d.send(partner.Server,partner.Port+2);
				}
			}
		}
		Global.Busy=false;
	}
	/**
	This is called, when the server is closed. It will announce
	the closing to known servers by a datagram.
	*/
	public void close ()
	{	if (!Public) return;
		DatagramMessage d=new DatagramMessage();
		d.add("close");
		d.add(Global.getParameter("yourname","Unknown"));
		try
		{	String s=InetAddress.getLocalHost().toString();
			d.add(s.substring(s.lastIndexOf('/')+1));
		}
		catch (Exception e) { d.add("Unknown Host"); }
		for (Partner partner : Global.PartnerList)
		{	if (partner.State>0) d.send(partner.Server,partner.Port+2);
		}
		Global.Busy=true;
	}
}
