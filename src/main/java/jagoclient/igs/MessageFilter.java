package jagoclient.igs;

import jagoclient.Global;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.FormTextField;
import jagoclient.gui.MenuItemAction;
import jagoclient.gui.MyLabel;
import jagoclient.gui.MyMenu;
import jagoclient.gui.MyPanel;
import jagoclient.gui.Panel3D;

import java.awt.Checkbox;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;

import rene.gui.CloseDialog;
import rene.gui.CloseFrame;

class SingleMessageFilter
{
	public String Name, Start, End, Contains;
	public boolean BlockComplete, Positive;

	public SingleMessageFilter (String n, String s, String e, String c,
		boolean bc, boolean pos)
	{
		Name = n;
		Start = s;
		End = e;
		Contains = c;
		BlockComplete = bc;
		Positive = pos;
	}

	public boolean matches (String s)
	{
		if ( !Start.equals("") && !s.startsWith(Start)) return false;
		if ( !End.equals("") && !s.endsWith(End)) return false;
		if ( !Contains.equals("") && s.indexOf(Contains) < 0) return false;
		return true;
	}

	public boolean positive ()
	{
		return Positive;
	}
}


/**
 * A message filter can be either positive or negative. It is used to either
 * block messages, or see messages, even when there source is blocked by global
 * flags.
 * <p>
 * The filter is determined by a start string, a string it must contain or an
 * end string. Filters are loaded at program start from filter.cfg. This is done
 * by a call to the load method.
 * <p>
 * The MessageFilter class has a list of SingleMessageFilter to check the
 * message against.
 */

public class MessageFilter
{
	List<SingleMessageFilter> F;
	public static final int BLOCK_COMPLETE = 2;
	public static final int BLOCK_POPUP = 1;

	public MessageFilter ()
	{
		F = new ArrayList<SingleMessageFilter>();
		load();
	}

	public int blocks (String s)
	{
		for (SingleMessageFilter f : F)
		{
			if ( !f.positive() && f.matches(s))
			{
				if (f.BlockComplete)
					return BLOCK_COMPLETE;
				else return BLOCK_POPUP;
			}
		}
		return 0;
	}

	public boolean posfilter (String s)
	{
		for (SingleMessageFilter f : F)
		{
			if (f.positive() && f.matches(s)) { return true; }
		}
		return false;
	}

	/**
	 * Load the message filters from filter.cfg.
	 */
	public void load ()
	{
		try
		{
			BufferedReader in = Global.getStream(".filter.cfg");
			while (true)
			{
				String name = in.readLine();
				if (name == null || name.equals("")) break;
				boolean pos = false;
				if (name.startsWith("+++++"))
				{
					pos = true;
					name = name.substring(5);
					if (name.equals("")) break;
				}
				String start = in.readLine();
				if (start == null) break;
				String end = in.readLine();
				if (end == null) break;
				String contains = in.readLine();
				if (contains == null) break;
				String blockcomplete = in.readLine();
				if (blockcomplete == null) break;
				F.add(new SingleMessageFilter(name, start, end, contains,
					blockcomplete.equals("true"), pos));
			}
			in.close();
		}
		catch (Exception e)
		{
			return;
		}
	}

	public void save ()
	{
		if (Global.isApplet()) return;
		try
		{
			PrintWriter out = new PrintWriter(new FileOutputStream(Global
				.home()
				+ ".filter.cfg"));
			for (SingleMessageFilter p : F)
			{
				if (p.positive())
					out.println("+++++" + p.Name);
				else out.println(p.Name);
				out.println(p.Start);
				out.println(p.End);
				out.println(p.Contains);
				out.println(p.BlockComplete);
			}
			out.close();
		}
		catch (IOException e)
		{
			return;
		}
	}

	public void edit ()
	{
		new MessageFilterEdit(F);
	}
}


class MessageFilterEdit extends CloseFrame
{
	List<SingleMessageFilter> F;
	java.awt.List L;

	public MessageFilterEdit (List f)
	{
		super(Global.resourceString("Message_Filter"));
		MenuBar mb = new MenuBar();
		setMenuBar(mb);
		Menu m = new MyMenu(Global.resourceString("Options"));
		m.add(new MenuItemAction(this, Global.resourceString("Close")));
		mb.add(m);
		F = f;
		L = new java.awt.List();
		L.setFont(Global.SansSerif);
		add("Center", new Panel3D(L));
		for (SingleMessageFilter filter : F)
		{
			L.add(filter.Name);
		}
		JPanel p = new MyPanel();
		p.add(new ButtonAction(this, Global.resourceString("Edit")));
		p.add(new ButtonAction(this, Global.resourceString("New")));
		p.add(new ButtonAction(this, Global.resourceString("Delete")));
		p.add(new MyLabel(" "));
		p.add(new ButtonAction(this, Global.resourceString("OK")));
		add("South", new Panel3D(p));
		seticon("ijago.gif");
		Global.setwindow(this, "filteredit", 300, 300);
		validate();
		setVisible(true);
	}

	@Override
	public void doAction (String o)
	{
		if (Global.resourceString("Edit").equals(o))
		{
			new SingleFilterEdit(this, F, selected());
		}
		else if (Global.resourceString("New").equals(o))
		{
			new SingleFilterEdit(this, F, null);
		}
		else if (Global.resourceString("Delete").equals(o))
		{
			removeselected();
		}
		else if (Global.resourceString("OK").equals(o))
		{
			Global.saveMessageFilter();
			doclose();
		}
		else super.doAction(o);
	}

	@Override
	public void doclose ()
	{
		Global.notewindow(this, "filteredit");
		super.doclose();
	}

	SingleMessageFilter selected ()
	{
		String s = L.getSelectedItem();
		if (s == null) return null;
		for (SingleMessageFilter f : F)
		{
			if (f.Name.equals(s)) return f;
		}
		return null;
	}

	void removeselected ()
	{
		String s = L.getSelectedItem();
		if (s == null) return;
		if (F.removeIf((SingleMessageFilter f) -> f.Name.equals(s)))
			updatelist();
	}

	void updatelist ()
	{
		L.removeAll();
		for (SingleMessageFilter f : F)
		{
			L.add(f.Name);
		}
	}
}


class SingleFilterEdit extends CloseDialog
{
	SingleMessageFilter MF;
	List<SingleMessageFilter> F;
	JTextField N, S, E, C;
	Checkbox BC;
	MessageFilterEdit MFE;
	boolean isnew;
	Checkbox CB;

	public SingleFilterEdit (MessageFilterEdit fr, List<SingleMessageFilter> f, SingleMessageFilter mf)
	{
		super(fr, Global.resourceString("Edit_Filter"), false);
		F = f;
		MF = mf;
		MFE = fr;
		if (MF == null)
		{
			isnew = true;
			MF = new SingleMessageFilter(Global.resourceString("Name"), Global
				.resourceString("Starts_with"), Global
				.resourceString("Ends_With"),
				Global.resourceString("Contains"), false, false);
		}
		else isnew = false;
		CB = new Checkbox(Global.resourceString("Positive_Filter"));
		CB.setState(MF.Positive);
		CB.setFont(Global.SansSerif);
		add("North", CB);
		JPanel p = new MyPanel();
		p.setLayout(new GridLayout(0, 2));
		p.add(new MyLabel(Global.resourceString("Name")));
		p.add(N = new FormTextField(MF.Name));
		p.add(new MyLabel(Global.resourceString("Starts_with")));
		p.add(S = new FormTextField(MF.Start));
		p.add(new MyLabel(Global.resourceString("Ends_With")));
		p.add(E = new FormTextField(MF.End));
		p.add(new MyLabel(Global.resourceString("Contains")));
		p.add(C = new FormTextField(MF.Contains));
		p.add(new MyLabel(Global.resourceString("Block_completely")));
		p.add(BC = new Checkbox());
		BC.setState(MF.BlockComplete);
		add("Center", p);
		JPanel bp = new MyPanel();
		bp.add(new ButtonAction(this, Global.resourceString("OK")));
		bp.add(new ButtonAction(this, Global.resourceString("Cancel")));
		add("South", bp);
		Global.setpacked(this, "singlefilteredit", 300, 300);
		validate();
		setVisible(true);
	}

	@Override
	public void doAction (String o)
	{
		Global.notewindow(this, "singlefilteredit");
		if (Global.resourceString("OK").equals(o) && !N.getText().equals(""))
		{
			MF.Name = N.getText();
			MF.Start = S.getText();
			MF.End = E.getText();
			MF.Contains = C.getText();
			MF.BlockComplete = BC.getState();
			MF.Positive = CB.getState();
			if (isnew)
			{
				F.add(MF);
			}
			MFE.updatelist();
		}
		setVisible(false);
		dispose();
	}

}
