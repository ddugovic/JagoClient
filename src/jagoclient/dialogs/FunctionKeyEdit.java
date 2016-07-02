package jagoclient.dialogs;

import jagoclient.Global;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.CloseDialog;
import jagoclient.gui.GrayTextField;
import jagoclient.gui.MyLabel;
import jagoclient.gui.MyPanel;
import jagoclient.gui.Panel3D;

import java.awt.GridLayout;

class FunctionKey extends GrayTextField
{
	public FunctionKey (int i)
	{
		super(Global.getParameter("f" + i, ""));
	}
}


/**
 * A dialog, which lets the user edit all function keys. Contains an array of 10
 * text fields.
 * <P>
 * The function keys are stored as global parameters.
 */
public class FunctionKeyEdit extends CloseDialog
{
	FunctionKey FK[];

	public FunctionKeyEdit ()
	{
		super(Global.frame(), Global.resourceString("Function_Keys"), false);
		MyPanel p = new MyPanel();
		p.setLayout(new GridLayout(0, 2));
		FK = new FunctionKey[10];
		for (int i = 0; i < 10; i++)
		{
			p.add(new MyLabel("F" + (i + 1)));
			p.add(FK[i] = new FunctionKey(i + 1));
		}
		add("Center", new Panel3D(p));
		MyPanel bp = new MyPanel();
		bp.add(new ButtonAction(this, Global.resourceString("Close")));
		add("South", new Panel3D(bp));
		Global.setpacked(this, "functionkeys", 300, 400);
		validate();
		setVisible(true);
	}

	@Override
	public void doAction (String o)
	{
		Global.notewindow(this, "functionkeys");
		for (int i = 0; i < 10; i++)
			Global.setParameter("f" + (i + 1), FK[i].getText());
		setVisible(false);
		dispose();
	}
}
