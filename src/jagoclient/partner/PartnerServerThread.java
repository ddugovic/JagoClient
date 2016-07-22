package jagoclient.partner;

import jagoclient.Global;
import jagoclient.datagram.DatagramMessage;
import jagoclient.partner.partner.Partner;

import java.net.InetAddress;
import java.util.Vector;
import java.util.logging.Logger;

import rene.util.list.ListClass;
import rene.util.list.ListElement;

/**
The PartnerServerThread handles datagrams from other servers. It
will relay the such messages to other known servers, unless they
are private.
*/

class PartnerServerThread extends Thread
{	private static final Logger LOG = Logger.getLogger(PartnerServerThread.class.getName());
	int Port;
	
	public PartnerServerThread (int port)
	{	Port=port; Global.OpenPartnerList=new ListClass(); start();
	}
	
	public void run ()
	{	DatagramMessage M=new DatagramMessage();
		while (true)
		{	// wait for a datagram
			Vector<String> v=M.receive(Port);
			// got one
			LOG.info("*** Datagram received ***");
			for (String s : v)
				LOG.info(s);
			LOG.info("*************************");
			try
			{	interpret(v);
			}
			catch (RuntimeException ex) {}
		}
	}
	
	void interpret (Vector<String> v)
	{	if (v.size()<1) return; // empty datagram
		switch (v.firstElement()) {
		// a server opened, send him an openinfo datagram
		case "open":
			open(v.elementAt(1), v.elementAt(2),
				Integer.parseInt(v.elementAt(3)),
				Integer.parseInt(v.elementAt(4)));
			openinfo(v.elementAt(2),
				Integer.parseInt(v.elementAt(3)));
			break;
		// a server notifies about opening of another server
		case "spreadopen":
			open(v.elementAt(1), v.elementAt(2),
				Integer.parseInt(v.elementAt(3)),
				Integer.parseInt(v.elementAt(4)));
			break;
		// a server sent me his list of open servers
		case "openinfo":
			openinfo(v);
			break;
		// a server closed or notifies about the closing of another server
		case "close":
		case "spreadclose":
			close(v.elementAt(1), v.elementAt(2));
			break;
		default:
			break;
		}
	}
	
	/**
	open is called, when another server opened and this message
	is to be spread to all other servers. If the server is private,
	the message will not be spread. If the open message is not
	public, it will be spread as private. Circularity is prevented
	by spreading only those openings, which are not already known
	to be opened.
	*/
	void open (String name, String address, int port, int state)
	{	ListElement e=find(name,address);
		if (e!=null) return; // we got that already
		// append to my list of open servers
		Global.OpenPartnerList.append(new Partner(name,address,port,state));
		// return, if the server is private
		if (state<Partner.PRIVATE) return;
		// spread to all other open servers
		DatagramMessage d=new DatagramMessage();
		d.add("spreadopen");
		d.add(name);
		d.add(address);
		d.add(""+port);
		d.add(""+(state<Partner.PUBLIC?Partner.PRIVATE:Partner.PUBLIC));
		for (ListElement<Partner> pe : Global.PartnerList)
		{	Partner p=pe.content();
			if (!p.Name.equals(name)) d.send(p.Server,p.Port+2);
		}
	}
	
	/**
	close is called, when a datagram is received about a closing of
	a server. this is spread to all known partner servers.
	*/
	void close (String name, String address)
	{	// find the server in my list of open servers
		ListElement e=find(name,address);
		if (e!=null) // only, if the server was open before
		{	// remove the server from the list
			Global.OpenPartnerList.remove(e);
			// spread the closing to all partners
			DatagramMessage d=new DatagramMessage();
			d.add("spreadclose");
			d.add(name);
			d.add(address);
			for (ListElement<Partner> pe : Global.PartnerList)
			{	Partner p=pe.content();
				if (!p.Name.equals(name)) d.send(p.Server,p.Port+2);
			}
		}
	}

	/**
	Notify all other servers about all known open servers. This is
	sent in response to an open message from this server so that
	the server is up to date about open servers.
	*/	
	void openinfo (String address, int port)
	{
		DatagramMessage d;
		int count=0;
		d=new DatagramMessage();
		d.add("openinfo");
		d.add(Global.getParameter("yourname","Unknown"));
		try
		{	String s=InetAddress.getLocalHost().toString();
			d.add(s.substring(s.lastIndexOf('/')+1));
		}
		catch (Exception ex) { d.add("Unknown Host"); }
		d.add(""+Global.getParameter("serverport",6970));
		d.add(""+Partner.PRIVATE);
		count++;
		for (ListElement<Partner> e : Global.OpenPartnerList)
		{	if (count==0)
			{	d=new DatagramMessage();
			}
			Partner p=e.content();
			if (p.State>Partner.PRIVATE)
			{	d.add("openinfo");
				d.add(p.Name);
				d.add(p.Server);
				d.add(""+p.Port);
				d.add(""+p.State);
			}
			count++;
			if (count>=10)
			{	d.send(address,port+2);
				count=0;
			}
		}
		if (count>0) d.send(address,port+2);
	}

	/**
	openinfo is called, when an openinfo datagram is received
	from any other server. It will add all the noted servers
	to my open server list.
	*/
	void openinfo (Vector v)
	{	int i=0;
		String name,server;
		int port,state;
		try
		{	if (!((String)v.elementAt(i)).equals("openinfo")) return;
			i++;
			name=(String)v.elementAt(i); i++;
			server=(String)v.elementAt(i); i++;
			port=Integer.parseInt((String)v.elementAt(i)); i++;
			state=Integer.parseInt((String)v.elementAt(i)); i++;
			ListElement<Partner> le=find(name,server);
			if (le==null)
			{	Global.OpenPartnerList.append(new Partner(name,server,port,state));
			}
		}
		catch (Exception e) {}
	}

	ListElement<Partner> find (String name, String server)
	{	String address=name+server;
		for (ListElement<Partner> e : Global.OpenPartnerList)
		{	Partner p=e.content();
			if ((p.Name+p.Server).equals(address)) return e;
		}
		return null;
	}
}
