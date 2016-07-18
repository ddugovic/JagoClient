package jagoclient.igs;

import jagoclient.Global;
import jagoclient.dialogs.Help;
import jagoclient.dialogs.Message;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.HistoryTextField;
import jagoclient.gui.MyLabel;
import jagoclient.gui.MyMenu;
import jagoclient.gui.MyPanel;
import jagoclient.gui.MyTextArea;
import jagoclient.gui.Panel3D;

import java.awt.BorderLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.PrintWriter;

import rene.gui.CloseFrame;
import rene.gui.CloseListener;

/**
 * Contains a text area and a text field for anwers.
 * 
 * @see jagoclient.igs.ChannelDistributor
 */
public class ChannelDialog extends CloseFrame implements CloseListener, KeyListener
{
	PrintWriter Out;
	TextArea T;
	ConnectionFrame CF;
	ChannelDistributor MDis;
	int N;
	HistoryTextField Answer;

	public ChannelDialog (ConnectionFrame cf, PrintWriter out, int n,
		ChannelDistributor mdis)
	{
		super(Global.resourceString("Channel"));
		CF = cf;
		MDis = mdis;
		N = n;
		CF.addCloseListener(this);
		setLayout(new BorderLayout());
		MenuBar M = new MenuBar();
		Menu help = new MyMenu(Global.resourceString("Help"));
		help.add(new MenuItem(Global.resourceString("Channels")));
		M.setHelpMenu(help);
		add("North", new MyLabel(Global.resourceString("Channel_") + n));
		MyPanel pm = new MyPanel();
		pm.setLayout(new BorderLayout());
		pm.add("Center", T = new MyTextArea("", 0, 0,
			TextArea.SCROLLBARS_VERTICAL_ONLY));
		pm.add("South", Answer = new HistoryTextField(this, "Answer"));
		add("Center", pm);
		MyPanel pb = new MyPanel();
		pb.add(new ButtonAction(this, Global.resourceString("Close")));
		add("South", new Panel3D(pb));
		Out = out;
		seticon("iwho.gif");
		Global.setwindow(this, "channeldialog", 500, 400);
		validate();
		setVisible(true);
	}

	@Override
	public void doAction (String o)
	{
		if (Global.resourceString("Channels").equals(o))
		{
			try
			{
				new Help("channels").display();
			}
			catch (IOException ex)
			{
				new Message(Global.frame(), ex.getMessage());
			}
		}
		else if ("Answer".equals(o))
		{
			if ( !Answer.getText().equals(""))
			{
				Out.println("; " + Answer.getText());
				T.append("---> " + Answer.getText() + "\n");
				Answer.setText("");
			}
		}
		else super.doAction(o);
	}

	@Override
	public boolean close ()
	{
		return true;
	}

	public void append (String s)
	{
		T.append(s + "\n");
	}

	public boolean escape ()
	{
		return false;
	}

	public void closed ()
	{
		doclose();
	}

	@Override
	public void doclose ()
	{
		MDis.CD = null;
		Global.notewindow(this, "channeldialog");
		super.doclose();
	}

	public void keyPressed (KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE && close()) doclose();
	}

	public void keyTyped (KeyEvent e)
	{}

	public void keyReleased (KeyEvent e)
	{
		String s = Global.getFunctionKey(e.getKeyCode());
		if (s.equals("")) return;
		Answer.setText(s);
		Answer.requestFocus();
	}
}
