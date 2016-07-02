package jagoclient.partner;

import jagoclient.Global;
import jagoclient.Go;
import jagoclient.StopThread;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.CloseFrame;
import jagoclient.gui.MenuItemAction;
import jagoclient.gui.MyLabel;
import jagoclient.gui.MyMenu;
import jagoclient.gui.MyPanel;
import jagoclient.partner.partner.Partner;

import java.awt.BorderLayout;
import java.awt.Menu;
import java.awt.MenuBar;

import javax.swing.JPanel;

import rene.util.list.ListClass;
import rene.util.list.ListElement;

class OpenPartnerFrameUpdate extends StopThread
{
	OpenPartnerFrame OPF;

	public OpenPartnerFrameUpdate (OpenPartnerFrame f)
	{
		OPF = f;
		start();
	}

	@Override
	public void run ()
	{
		while (stopped())
		{
			try
			{
				sleep(30000);
			}
			catch (Exception e)
			{}
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
	OpenPartnerFrameUpdate OPFU;

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
		OPFU = new OpenPartnerFrameUpdate(this);
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
		ListClass PL = Global.OpenPartnerList;
		L.removeAll();
		if (PL == null) return;
		ListElement le = PL.first();
		while (le != null)
		{
			L.add(((Partner)le.content()).Name);
			le = le.next();
		}
	}

	@Override
	public void doclose ()
	{
		G.OPF = null;
		OPFU.stopit();
		Global.notewindow(this, "openpartner");
		super.doclose();
	}

	public void connect ()
	{
		ListElement le = Global.OpenPartnerList.first();
		String s = L.getSelectedItem();
		while (le != null)
		{
			Partner p = (Partner)le.content();
			if (p.Name.equals(s))
			{
				PartnerFrame cf = new PartnerFrame(Global
					.resourceString("Connection_to_")
					+ p.Name, false);
				Global.setwindow(cf, "partner", 500, 400);
				new ConnectPartner(p, cf);
				return;
			}
			le = le.next();
		}
	}
}
