package jagoclient.igs;

import jagoclient.Global;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.CloseFrame;
import jagoclient.gui.CloseListener;
import jagoclient.gui.HistoryTextField;
import jagoclient.gui.MyLabel;
import jagoclient.gui.MyPanel;
import jagoclient.gui.Panel3D;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.TextArea;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;

import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * The SayDialog is opened by the SayDistributor in response to a say.
 */

public class SayDialog extends CloseFrame implements CloseListener, KeyListener
{
	PrintWriter Out;
	JTextField Answer;
	TextArea T;
	SayDistributor SD;
	ConnectionFrame CF;

	public SayDialog (ConnectionFrame cf, SayDistributor sd, String m,
		PrintWriter out)
	{
		super(Global.resourceString("Say"));
		seticon("ijago.gif");
		cf.addCloseListener(this);
		SD = sd;
		CF = cf;
		add("North", new MyLabel(Global.resourceString("Opponent_said_")));
		JPanel pm = new MyPanel();
		pm.setLayout(new BorderLayout());
		pm.add("Center", T = new TextArea());
		T.setFont(Global.Monospaced);
		T.setEditable(false);
		pm.add("South", Answer = new HistoryTextField(this, "Answer", 40));
		add("Center", pm);
		JPanel p = new MyPanel();
		p.add(new ButtonAction(this, Global.resourceString("Close")));
		p.add(new ButtonAction(this, Global.resourceString("Send_Answer")));
		add("South", new Panel3D(p));
		Out = out;
		SD.MD = this;
		Global.setwindow(this, "say", 400, 200);
		validate();
		setVisible(true);
		T.setText(m);
		Answer.addKeyListener(this);
	}

	@Override
	public void doAction (String o)
	{
		Global.notewindow(this, "say");
		if (Global.resourceString("Close").equals(o))
		{
			close();
			setVisible(false);
			dispose();
		}
		else if (Global.resourceString("Send_Answer").equals(o)
			|| "Answer".equals(o))
		{
			if ( !Answer.getText().equals(""))
			{
				Out.println("say " + Answer.getText());
				CF.append("say: " + Answer.getText());
				Answer.setText("");
			}
		}
		else super.doAction(o);
	}

	public void append (String s)
	{
		T.append("\n" + s);
	}

	@Override
	public void paint (Graphics g)
	{
		if (SD.MD == null)
		{
			CF.removeCloseListener(this);
			setVisible(false);
			dispose();
			return;
		}
		super.paint(g);
	}

	@Override
	public boolean close ()
	{
		return true;
	}

	public void isClosed ()
	{
		doclose();
	}

	@Override
	public void doclose ()
	{
		SD.MD = null;
		CF.removeCloseListener(this);
		super.doclose();
	}

	@Override
	public void windowOpened (WindowEvent e)
	{
		Answer.requestFocus();
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
	}
}
