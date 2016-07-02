package jagoclient.dialogs;

import jagoclient.Global;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.CloseDialog;
import jagoclient.gui.MyPanel;
import jagoclient.gui.Panel3D;

import java.awt.Frame;

import rene.viewer.SystemViewer;
import rene.viewer.Viewer;

/**
 * This class is used to display messages from the go server. The message can
 * have several lines.
 */

public class Message extends CloseDialog
{
	Viewer T;

	public Message (Frame f, String m)
	{
		super(f, Global.resourceString("Message"), false);
		add("Center",
			T = Global.getParameter("systemviewer", false)?new SystemViewer()
				:new Viewer());
		T.setFont(Global.Monospaced);
		MyPanel p = new MyPanel();
		p.add(new ButtonAction(this, Global.resourceString("OK")));
		add("South", new Panel3D(p));
		Global.setwindow(this, "message", 300, 150);
		validate();
		setVisible(true);
		T.setText(m);
	}

	@Override
	public void doAction (String o)
	{
		Global.notewindow(this, "message");
		if (Global.resourceString("OK").equals(o))
		{
			setVisible(false);
			dispose();
		}
		else super.doAction(o);
	}
}
