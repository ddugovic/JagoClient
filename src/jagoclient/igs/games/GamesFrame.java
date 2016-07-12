package jagoclient.igs.games;

import jagoclient.Global;
import jagoclient.dialogs.Help;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.CloseFrame;
import jagoclient.gui.CloseListener;
import jagoclient.gui.MenuItemAction;
import jagoclient.gui.MyLabel;
import jagoclient.gui.MyMenu;
import jagoclient.gui.MyPanel;
import jagoclient.gui.Panel3D;
import jagoclient.igs.ConnectionFrame;
import jagoclient.igs.IgsStream;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.io.PrintWriter;
import java.util.Arrays;

import rene.util.list.ListClass;
import rene.util.list.ListElement;
import rene.util.parser.StringParser;
import rene.viewer.Lister;
import rene.viewer.SystemLister;

/**
 * This frame displays the games on the server. It is opened by a
 * GamesDistributor. To sort the games it uses the GamesObject class, which is a
 * SortObject implementation and can be sorted via the Sorter quicksort
 * algorithm.
 * 
 * @see jagoclient.sort.Sorter
 */

public class GamesFrame extends CloseFrame implements CloseListener
{
	IgsStream In;
	PrintWriter Out;
	Lister T;
	ConnectionFrame CF;
	GamesDistributor GD;
	ListClass<String> L;
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
		T = Global.getParameter("systemlister", false)?new SystemLister()
			:new Lister();
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
			new Help("games");
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
		L = new ListClass();
		LNumber = 0;
		T.setText(Global.resourceString("Loading"));
		if (GD != null) GD.unchain();
		GD = new GamesDistributor(In, this);
		Out.println("games");
	}

	public synchronized void receive (String s)
	{
		if (Closed) return;
		L.append(s);
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
		ListElement p = L.first();
		int i, n = 0;
		while (p != null)
		{
			n++;
			p = p.next();
		}
		if (n > 3)
		{
			GamesObject v[] = new GamesObject[n - 1];
			p = L.first().next();
			for (i = 0; i < n - 1; i++)
			{
				v[i] = new GamesObject((String)p.content());
				p = p.next();
			}
			Arrays.sort(v);
			T.setText("");
			T.appendLine0(" " + (String)L.first().content());
			Color FC = Color.green.darker().darker();
			for (i = 0; i < n - 1; i++)
			{
				T.appendLine0(v[i].game(), v[i].friend()?FC:Color.black);
			}
			T.doUpdate(false);
		}
		else
		{
			p = L.first();
			while (p != null)
			{
				T.appendLine((String)p.content());
				p = p.next();
			}
			T.doUpdate(false);
		}
	}

	public void isClosed ()
	{
		if (Global.getParameter("menuclose", true)) setMenuBar(null);
		setVisible(false);
		dispose();
	}
}
