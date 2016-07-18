package jagoclient.igs.who;

import jagoclient.Global;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.GrayTextField;
import jagoclient.gui.MyLabel;
import jagoclient.gui.MyPanel;
import jagoclient.gui.Panel3D;
import jagoclient.gui.SimplePanel;
import jagoclient.gui.TextFieldAction;
import jagoclient.igs.ConnectionFrame;

import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JTextField;

import rene.gui.CloseDialog;

/**
 * Ask to tell the chosen user something, using the IGS tell command.
 */

public class TellQuestion extends CloseDialog
{
	ConnectionFrame F;
	JTextField T;
	JTextField User;

	/**
	 * @param f
	 *            the connection frame, which is used to send the output to IGS.
	 * @param user
	 *            the user name of the person, which is to receive the message.
	 */
	public TellQuestion (Frame fr, ConnectionFrame f, String user)
	{
		super(fr, Global.resourceString("Tell"), false);
		F = f;
		add("North", new SimplePanel(new MyLabel(Global.resourceString("To_")),
			0.4, User = new GrayTextField(user), 0.6));
		add("Center", T = new TextFieldAction(this, Global
			.resourceString("Tell")));
		T.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased (KeyEvent e)
			{
				String s = Global.getFunctionKey(e.getKeyCode());
				if (s.equals("")) return;
				T.setText(s);
			}
		});
		MyPanel p = new MyPanel();
		p.add(new ButtonAction(this, Global.resourceString("Tell")));
		p.add(new ButtonAction(this, Global.resourceString("Message")));
		p.add(new ButtonAction(this, Global.resourceString("Cancel")));
		add("South", new Panel3D(p));
		Global.setpacked(this, "tell", 200, 150, f);
		validate();
		setVisible(true);
	}

	@Override
	public void windowOpened (WindowEvent e)
	{
		T.requestFocus();
	}

	@Override
	public void doAction (String o)
	{
		Global.notewindow(this, "tell");
		if (Global.resourceString("Tell").equals(o))
		{
			if ( !T.getText().equals(""))
			{
				F.out("tell " + User.getText() + " " + T.getText());
			}
			setVisible(false);
			dispose();
		}
		else if (Global.resourceString("Message").equals(o))
		{
			if ( !T.getText().equals(""))
			{
				F.out("message " + User.getText() + " " + T.getText());
			}
			setVisible(false);
			dispose();
		}
		else if (Global.resourceString("Cancel").equals(o))
		{
			setVisible(false);
			dispose();
		}
		else super.doAction(o);
	}
}
