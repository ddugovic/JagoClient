package rene.dialogs;

import jagoclient.Global;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.MyLabel;
import jagoclient.gui.MyPanel;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionListener;

import rene.gui.CloseDialog;

/**
 * The Question dialog displays a question and yes/no buttons. It can be modal
 * or non-modal. If the dialog is used modal, there is no need to subclass it.
 * The result can be asked after the show method returns (which must be called
 * from the creating method). If the dialog is non-modal, it should be
 * subclassed and the tell method needs to be redefined to do something useful.
 */

public class Question extends CloseDialog implements ActionListener
{
	Object O;
	Frame F;
	public boolean Result = false;

	/**
	 * @param o
	 *            an object to be passed to the tell method (may be null)
	 * @param flag
	 *            determines, if the dialog is modal or not
	 */
	public Question (Frame f, String c, String title, Object o, boolean flag)
	{
		super(f, title, flag);
		F = f;
		MyPanel pc = new MyPanel();
		FlowLayout fl = new FlowLayout();
		pc.setLayout(fl);
		fl.setAlignment(FlowLayout.CENTER);
		pc.add(new MyLabel(" " + c + " "));
		add("Center", pc);
		MyPanel p = new MyPanel();
		p.add(new ButtonAction(this, Global.resourceString("Yes")));
		p.add(new ButtonAction(this, Global.resourceString("No")));
		add("South", p);
		O = o;
		if (flag)
			Global.setpacked(this, "question", 300, 150, f);
		else Global.setpacked(this, "question", 300, 150);
		validate();
	}

	@Override
	public void doAction (String o)
	{
		Global.notewindow(this, "question");
		if (Global.resourceString("Yes").equals(o))
		{
			tell(this, O, true);
			Result = true;
		}
		else if (Global.resourceString("No").equals(o))
		{
			tell(this, O, false);
		}
		setVisible(false);
		dispose();
	}

	/** callback for non-modal dialogs */
	public void tell (Question q, Object o, boolean f)
	{}

	/** to get the result of the question */
	public boolean result ()
	{
		return Result;
	}
}
