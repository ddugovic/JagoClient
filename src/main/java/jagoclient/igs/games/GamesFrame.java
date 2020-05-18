package jagoclient.igs.games;

import jagoclient.Global;
import jagoclient.dialogs.Help;
import jagoclient.dialogs.Message;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.MenuItemAction;
import jagoclient.gui.MyLabel;
import jagoclient.gui.MyMenu;
import jagoclient.gui.MyPanel;
import jagoclient.gui.Panel3D;
import jagoclient.igs.ConnectionFrame;
import jagoclient.igs.Distributor;
import jagoclient.igs.IgsStream;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import rene.gui.CloseFrame;
import rene.gui.CloseListener;
import rene.util.list.ListElement;
import rene.util.parser.StringParser;
import rene.viewer.Lister;
import rene.viewer.SystemLister;

/**
 * This frame displays the games on the server. It is opened by a
 * GamesDistributor. To sort the games it uses the GamesObject class, which is a
 * SortObject implementation and can be sorted via the Sorter quicksort
 * algorithm.
 */

public class GamesFrame extends CloseFrame implements CloseListener, Distributor.Task
{
	IgsStream In;
	PrintWriter Out;
	Lister T;
	ConnectionFrame CF;
	GamesDistributor GD;
	List<String> L;
	boolean Closed = false;
	int LNumber;

	public GamesFrame (ConnectionFrame cf, PrintWriter out, IgsStream in)
	{
		super(Global.resourceString("_Games_"));
		cf.addCloseListener(this);
		In = in;
		Out = out;
		MenuBar mb = new MenuBar();
		setMenuBar(mb);
		Menu m = new MyMenu(Global.resourceString("Options"));
		m.add(new MenuItemAction(this, Global.resourceString("Close")));
		mb.add(m);
		Menu help = new MyMenu(Global.resourceString("Help"));
		help.add(new MenuItemAction(this, Global
			.resourceString("About_this_Window")));
		mb.add(help);
		setLayout(new BorderLayout());
		T = Global.getParameter("systemlister", false)?new SystemLister():new Lister();
		T.setFont(Global.Monospaced);
		T.setText(Global.resourceString("Loading"));
		add("Center", T);
		MyPanel p = new MyPanel();
		p.add(new ButtonAction(this, Global.resourceString("Observe")));
		p.add(new ButtonAction(this, Global.resourceString("Peek")));
		p.add(new ButtonAction(this, Global.resourceString("Status")));
		p.add(new MyLabel(" "));
		p.add(new ButtonAction(this, Global.resourceString("Refresh")));
		p.add(new ButtonAction(this, Global.resourceString("Close")));
		add("South", new Panel3D(p));
		CF = cf;
		GD = null;
		seticon("igames.gif");
		PopupMenu pop = new PopupMenu();
		addpop(pop, Global.resourceString("Observe"));
		addpop(pop, Global.resourceString("Peek"));
		addpop(pop, Global.resourceString("Status"));
		if (T instanceof Lister) T.setPopupMenu(pop);
	}

	public void addpop (PopupMenu pop, String label)
	{
		MenuItem mi = new MenuItemAction(this, label, label);
		pop.add(mi);
	}

	@Override
	public void doAction (String o)
	{
		if (Global.resourceString("Refresh").equals(o))
		{
			refresh();
		}
		else if (Global.resourceString("Peek").equals(o))
		{
			String s = T.getSelectedItem();
			if (s == null) return;
			StringParser p = new StringParser(s);
			p.skipblanks();
			if ( !p.skip("[")) return;
			p.skipblanks();
			if ( !p.isint()) return;
			CF.peek(p.parseint(']'));
		}
		else if (Global.resourceString("Status").equals(o))
		{
			String s = T.getSelectedItem();
			if (s == null) return;
			StringParser p = new StringParser(s);
			p.skipblanks();
			if ( !p.skip("[")) return;
			p.skipblanks();
			if ( !p.isint()) return;
			CF.status(p.parseint(']'));
		}
		else if (Global.resourceString("Observe").equals(o))
		{
			String s = T.getSelectedItem();
			if (s == null) return;
			StringParser p = new StringParser(s);
			p.skipblanks();
			if ( !p.skip("[")) return;
			p.skipblanks();
			if ( !p.isint()) return;
			CF.observe(p.parseint(']'));
		}
		else if (Global.resourceString("About_this_Window").equals(o))
		{
			try
			{
				new Help("games").display();
			}
			catch (IOException ex)
			{
				new Message(Global.frame(), ex.getMessage()).setVisible(true);
			}
		}
		else super.doAction(o);
	}

	@Override
	public synchronized boolean close ()
	{
		if (GD != null) GD.unchain();
		CF.Games = null;
		CF.removeCloseListener(this);
		Closed = true;
		Global.notewindow(this, "games");
		return true;
	}

	/**
	 * Opens a new GamesDistributor to receive the games from the server. and
	 * asks the server to send the games.
	 */
	public synchronized void refresh ()
	{
		L = new ArrayList<String>();
		LNumber = 0;
		T.setText(Global.resourceString("Loading"));
		if (GD != null) GD.unchain();
		GD = new GamesDistributor(In, this);
		Out.println("games");
	}

	public synchronized void receive (String s)
	{
		if (Closed) return;
		L.add(s);
		LNumber++;
		if (LNumber == 1) T.setText(Global.resourceString("Receiving"));
	}

	/**
	 * When the distributor has all games, it calls allsended and the sorting
	 * will start.
	 */
	public synchronized void allsended ()
	{
		if (GD != null) GD.unchain();
		if (Closed) return;
		int i, n = L.size();
		if (n > 3)
		{
			GamesObject[] go = new GamesObject[n - 1];
			Iterator<String> p = L.iterator();
			p.next(); // skip first element for some reason
			for (i = 0; i < n - 1; i++)
			{
				go[i] = new GamesObject(p.next());
			}
			Arrays.sort(go);
			T.setText("");
			T.appendLine0(" " + L.get(0));
			Color FC = Color.green.darker().darker();
			for (i = 0; i < n - 1; i++)
			{
				T.appendLine0(go[i].game(), go[i].friend()?FC:Color.black);
			}
			T.doUpdate(false);
		}
		else
		{
			for (String s : L)
			{
				T.appendLine(s);
			}
			T.doUpdate(false);
		}
	}

	@Override
	public void finished () {}

	@Override
	public void closed ()
	{
		if (Global.getParameter("menuclose", true)) setMenuBar(null);
		setVisible(false);
		dispose();
	}
}
