package jagoclient.dialogs;

import jagoclient.Global;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.CloseDialog;
import jagoclient.gui.MyPanel;
import jagoclient.gui.Panel3D;

import java.awt.Frame;
import java.io.BufferedReader;

import rene.viewer.SystemViewer;
import rene.viewer.Viewer;

/**
 * The same as Help.java but as a dialog. This is for giving help in modal
 * dialogs.
 * 
 * @see jagoclient.dialogs.Help
 */

public class HelpDialog extends CloseDialog
{
	Viewer V; // The viewer
	Frame F;

	/**
	 * Display the help from subject.txt,Global.url()/subject.txt or from the
	 * ressource /subject.txt.
	 */
	public HelpDialog (Frame f, String subject)
	{
		super(f, Global.resourceString("Help"), true);
		F = f;
		V = Global.getParameter("systemviewer", false)?new SystemViewer()
			:new Viewer();
		V.setFont(Global.Monospaced);
		try
		{
			BufferedReader in;
			String s;
			try
			{
				in = Global.getStream("helptexts/" + subject
					+ Global.resourceString("HELP_SUFFIX") + ".txt");
				s = in.readLine();
			}
			catch (Exception e)
			{
				try
				{
					in = Global.getStream(subject
						+ Global.resourceString("HELP_SUFFIX") + ".txt");
					s = in.readLine();
				}
				catch (Exception ex)
				{
					in = Global.getStream("helptexts/" + subject
						+ ".txt");
					s = in.readLine();
				}
			}
			while (s != null)
			{
				V.appendLine(s);
				s = in.readLine();
			}
			in.close();
		}
		catch (Exception e)
		{
			new Message(Global.frame(), Global
				.resourceString("Could_not_find_the_help_file_"));
			doclose();
			return;
		}
		display();
	}

	public void doclose ()
	{
		setVisible(false);
		dispose();
	}

	void display ()
	{
		Global.setwindow(this, "help", 500, 400);
		add("Center", V);
		MyPanel p = new MyPanel();
		p.add(new ButtonAction(this, Global.resourceString("Close")));
		add("South", new Panel3D(p));
		setVisible(true);
	}

	@Override
	public void doAction (String o)
	{
		Global.notewindow(this, "help");
		super.doAction(o);
	}
}
