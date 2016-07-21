package jagoclient.igs;

import jagoclient.Global;
import jagoclient.dialogs.Message;
import jagoclient.igs.connection.Connection;

/**
A thread, which tries to connect to a server. It will open
a ConnectionFrame to display the connection on success.
<P>
If it fails, it will display an error message for 10 seconds.
*/

public class Connect implements Runnable
{	Connection C;
	ConnectionFrame CF;
	String S;
	public Connect (Connection c, ConnectionFrame cf)
	{	C=c; CF=cf; S="";
	}
	public Connect (Connection c, String s, ConnectionFrame cf)
	{	C=c; CF=cf; S=s;
	}
	public void run ()
	{	C.Trying=true;
		CF.movestyle(C.MoveStyle);
		if (Global.getParameter("userelay",false))
		{	if (!CF.connectvia(C.Server,C.Port,C.User,
				S.equals("")?C.Password:S,
				Global.getParameter("relayserver","localhost"),
				Global.getParameter("relayport",6971)))
			{	CF.setVisible(false); CF.dispose();
				new Message(Global.frame(),Global.resourceString("No_connection_to_")+C.Server+"!").setVisible(true);
				try
				{	Thread.sleep(10000);
				}
				catch (Exception e)
				{	C.Trying=false;	
				}
			}
		}
		else if (!CF.connect(C.Server,C.Port,C.User,
			S.equals("")?C.Password:S,C.Port==23?true:false))
		{	CF.setVisible(false); CF.dispose();
			new Message(Global.frame(),Global.resourceString("No_connection_to_")+C.Server+"!").setVisible(true);
			try
			{	Thread.sleep(10000);
			}
			catch (Exception e)
			{	C.Trying=false;	
			}
		}
		C.Trying=false;
	}
}

