package jagoclient.dialogs;

import jagoclient.Global;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.MyPanel;
import jagoclient.gui.Panel3D;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import rene.gui.CloseDialog;
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

	/**
	 * Display the help from subject.txt,Global.url()/subject.txt or from the
	 * ressource /subject.txt.
	 */
	public HelpDialog (Frame f, String subject) throws IOException
	{
		super(f, Global.resourceString("Help"), true);
		V = Global.getParameter("systemviewer", false)? new SystemViewer() : new Viewer();
		V.setFont(Global.Monospaced);
		String s;
		try (InputStream is = Global.class.getResourceAsStream("/helptexts/" +
				subject + Global.resourceString("HELP_SUFFIX") + ".txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(is)))
		{
			while ((s = in.readLine()) != null)
				V.appendLine(s);
		}
		catch (IOException ex) // Fall back to default help file
		{
			try (InputStream is = Global.class.getResourceAsStream("/helptexts/" + subject + ".txt");
				BufferedReader in = new BufferedReader(new InputStreamReader(is)))
			{
				while ((s = in.readLine()) != null)
					V.appendLine(s);
			}
			catch (IOException ex2)
			{
				throw new IOException(Global.resourceString("Could_not_find_the_help_file_"), ex2);
			}
		}
	}

	public void doclose ()
	{
		setVisible(false);
		dispose();
	}

	public void display ()
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
