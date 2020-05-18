package jagoclient.dialogs;

import jagoclient.Global;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.MyLabel;
import jagoclient.gui.MyPanel;
import jagoclient.gui.Panel3D;
import jagoclient.gui.TextFieldAction;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import rene.gui.CloseDialog;

/**
 * A general dialog to get a string parameter. Contains a simple text field. The
 * modal flag is handled as in the Question class. Again, show has to be called
 * externally.
 * 
 * @see rene.dialogs.Question
 */

public abstract class GetParameter<F extends Frame> extends CloseDialog
{
	public boolean Result;
	F frame;
	JTextField T;
	String Helpfile;
	protected MyLabel Prompt;

	public GetParameter (F f, String c, String title, Object o,
		boolean modalflag)
	{
		this(f, c, title, o, modalflag, "");
	}

	public GetParameter (F f, String c, String title, Object o,
		boolean modalflag, String help)
	{
		super(f, title, modalflag);
		frame = f;
		Helpfile = help;

		JPanel n = new MyPanel();
		n.setLayout(new BorderLayout());
		n.add("North", Prompt = new MyLabel(c));
		n.add("Center", T = new TextFieldAction(this, "Input", 25));
		add("Center", new Panel3D(n));

		JPanel p = new MyPanel();
		p.add(new ButtonAction(this, Global.resourceString("OK")));
		p.add(new ButtonAction(this, Global.resourceString("Cancel")));
		if ( !help.equals(""))
		{
			p.add(new MyLabel(" "));
			p.add(new ButtonAction(this, Global.resourceString("Help")));
		}
		add("South", new Panel3D(p));

		if (modalflag)
			Global.setpacked(this, "getparameter", 300, 150, f);
		else Global.setpacked(this, "getparameter", 300, 150);

		validate();
		T.addKeyListener(this);
	}

	public GetParameter (F f, String c, String title, Object o, char echo,
		boolean modalflag)
	{
		this(f, c, title, o, echo, modalflag, "");
	}

	public GetParameter (F f, String c, String title, Object o, char echo,
		boolean modalflag, String help)
	{
		super(f, title, modalflag);
		frame = f;
		Helpfile = help;
		JPanel n = new MyPanel();
		n.setLayout(new BorderLayout());
		n.add("North", new MyLabel(c));
		n.add("Center", T = new JPasswordField("", 25));
		add("Center", new Panel3D(n));
		add("North", new MyLabel(c));
		JPanel p = new MyPanel();
		p.add(new ButtonAction(this, Global.resourceString("OK")));
		p.add(new ButtonAction(this, Global.resourceString("Cancel")));
		if ( !help.equals(""))
		{
			p.add(new MyLabel(" "));
			p.add(new ButtonAction(this, Global.resourceString("Help")));
		}
		add("South", new Panel3D(p));
		if (modalflag)
			Global.setpacked(this, "getparameter", 300, 150, f);
		else Global.setpacked(this, "getparameter", 300, 150);
		validate();
		T.addKeyListener(this);
	}

	@Override
	public void doAction (String o)
	{
		Global.notewindow(this, "getparameter");
		if (Global.resourceString("Cancel").equals(o))
		{
			close();
			setVisible(false);
			dispose();
		}
		else if (o.equals("Input") || o.equals(Global.resourceString("OK")))
		{
			if (tell(frame, T.getText()))
			{
				setVisible(false);
				dispose();
			}
		}
		else if (o.equals(Global.resourceString("Help")))
		{
			try
			{
				new HelpDialog(frame, Helpfile).display();
			}
			catch (IOException ex)
			{
				new Message(Global.frame(), ex.getMessage()).setVisible(true);
			}
		}
		else super.doAction(o);
	}

	/**
	 * This is called, when the dialog is finished with a valid entry (User
	 * pressed OK).
	 */
	public abstract boolean tell (F f, String S);

	public void set (String s)
	{
		T.setText(s);
	}

	@Override
	public boolean close ()
	{
		return true;
	}

	/**
	 * This is to be used in the modal case after the show method returns.
	 * 
	 * @return the text you asked for
	 */
	public String getText ()
	{
		return T.getText();
	}
}
