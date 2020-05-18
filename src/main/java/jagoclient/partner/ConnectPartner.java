package jagoclient.partner;

import jagoclient.Global;
import jagoclient.dialogs.Message;
import jagoclient.partner.partner.Partner;

/**
A thread, which will try to connect to a go partner.
If it is successful, a Partner Frame will open.
Otherwise, an error message will appear.
*/

public class ConnectPartner implements Runnable
{	Partner P;
	PartnerFrame PF;
	public ConnectPartner (Partner p, PartnerFrame pf)
	{	P=p; PF=pf;
	}
	@Override
	public void run ()
	{	P.Trying=true;
		if (Global.getParameter("userelay",false))
		{	if (!PF.connectvia(P.Server,P.Port,
				Global.getParameter("relayserver","localhost"),
				Global.getParameter("relayport",6971)))
			{	PF.setVisible(false); PF.dispose();
				new Message(Global.frame(),
					Global.resourceString("No_connection_to_")+P.Server).setVisible(true);
				try
				{	Thread.sleep(10000);
				}
				catch (Exception e)
				{	P.Trying=false;
				}
			}
		}
		else if (!PF.connect(P.Server,P.Port))
		{	PF.setVisible(false); PF.dispose();
			new Message(Global.frame(),
				Global.resourceString("No_connection_to_")+P.Server).setVisible(true);
			try
			{	Thread.sleep(10000);
			}
			catch (Exception e)
			{	P.Trying=false;
			}
		}
		P.Trying=false;
	}
}

