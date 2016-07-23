package jagoclient.partner;

import jagoclient.Global;
import jagoclient.Go;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.MenuItemAction;
import jagoclient.gui.MyLabel;
import jagoclient.gui.MyMenu;
import jagoclient.gui.MyPanel;
import jagoclient.partner.partner.Partner;

import java.awt.BorderLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.util.List;

import javax.swing.JPanel;

import rene.gui.CloseFrame;

class OpenPartnerFrameUpdate implements Runnable
{
	OpenPartnerFrame OPF;

	public OpenPartnerFrameUpdate (OpenPartnerFrame f)
	{
		OPF = f;
	}

	@Override
	public void run ()
	{
		while (!Thread.interrupted())
		{
			try
			{
				Thread.sleep(30000);
			}
			catch (Exception e)
			{
				break;
			}
			OPF.refresh();
		}
	}
}


/**
 * This is a frame, which displays a list of all open partner servers. It
 * contains buttons to connect to one of one of them and to refresh the list.
 */

public class OpenPartnerFrame extends CloseFrame
{
	Go G;
	java.awt.List L;
	Thread OPFU;

	public OpenPartnerFrame (Go go)
	{
		super(Global.resourceString("Open_Partners"));
		G = go;
		MenuBar mb = new MenuBar();
		setMenuBar(mb);
		Menu m = new MyMenu(Global.resourceString("Options"));
		m.add(new MenuItemAction(this, Global.resourceString("Close")));
		mb.add(m);
		setLayout(new BorderLayout());
		L = new java.awt.List();
		L.setFont(Global.SansSerif);
		refresh();
		add("Center", L);
		JPanel bp = new MyPanel();
		bp.add(new ButtonAction(this, Global.resourceString("Connect")));
		bp.add(new ButtonAction(this, Global.resourceString("Refresh")));
		bp.add(new MyLabel(" "));
		bp.add(new ButtonAction(this, Global.resourceString("Close")));
		add("South", bp);
		Global.setwindow(this, "openpartner", 300, 200);
		seticon("ijago.gif");
		setVisible(true);
		OPFU = new Thread(new OpenPartnerFrameUpdate(this));
		OPFU.start();
	}

	@Override
	public void doAction (String o)
	{
		if (o.equals(Global.resourceString("Refresh")))
		{
			refresh();
		}
		else if (o.equals(Global.resourceString("Close")))
		{
			doclose();
		}
		else if (o.equals(Global.resourceString("Connect")))
		{
			connect();
		}
		else super.doAction(o);
	}

	public void refresh ()
	{
		List<Partner> PL = Global.OpenPartnerList;
		L.removeAll();
		if (PL == null) return;
		for (Partner partner : PL)
		{
			L.add(partner.Name);
		}
	}

	@Override
	public void doclose ()
	{
		G.OPF = null;
		OPFU.interrupt();
		Global.notewindow(this, "openpartner");
		super.doclose();
	}

	public void connect ()
	{
		String s = L.getSelectedItem();
		for (Partner partner : Global.OpenPartnerList)
		{
			if (partner.Name.equals(s))
			{
				PartnerFrame cf = new PartnerFrame(Global.resourceString("Connection_to_") + partner.Name, false);
				Global.setwindow(cf, "partner", 500, 400);
				new Thread(new ConnectPartner(partner, cf)).start();
				return;
			}
		}
	}
}
