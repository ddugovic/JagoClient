package rene.gui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import rene.dialogs.InfoDialog;

/**
 * A dialog, which can be closed by clicking on the close window field (a cross
 * on the top right corner in Windows 95), or by pressing the escape key.
 * <p>
 * Moreover, the dialog is a DoActionListener, which makes it possible to use
 * the simplified TextFieldAction etc.
 */

public class CloseDialog extends Dialog implements WindowListener,
		ActionListener, DoActionListener, KeyListener, FocusListener
{
	boolean Dispose = true;
	public boolean Aborted = false;
	Frame F;
	public String Subject = "";

	public CloseDialog (Frame f, String s, boolean modal)
	{
		super(f, s, modal);
		F = f;
		if (Global.ControlBackground != null)
			setBackground(Global.ControlBackground);
		addWindowListener(this);
		addKeyListener(this);
		addFocusListener(this);
	}

	public void windowActivated (WindowEvent e)
	{}

	public void windowClosed (WindowEvent e)
	{}

	public void windowClosing (WindowEvent e)
	{
		if (close()) doclose();
	}

	public void windowDeactivated (WindowEvent e)
	{}

	public void windowDeiconified (WindowEvent e)
	{}

	public void windowIconified (WindowEvent e)
	{}

	public void windowOpened (WindowEvent e)
	{}

	/**
	 * @return true if the dialog is closed.
	 */
	public boolean close ()
	{
		return true;
	}

	/**
	 * Calls close(), when the escape key is pressed.
	 * 
	 * @return true if the dialog may close.
	 */
	public boolean escape ()
	{
		return close();
	}

	public ActionEvent E;

	public void actionPerformed (ActionEvent e)
	{
		E = e;
		doAction(e.getActionCommand());
	}

	public void doAction (String o)
	{
		if ("Close".equals(o) && close())
		{
			Aborted = true;
			doclose();
		}
		else if (o.equals("Help"))
		{
			showHelp();
		}
	}

	public void showHelp ()
	{
		InfoDialog.Subject = Subject;
		InfoDialog id = new InfoDialog(F);
	}

	public void itemAction (String o, boolean flag)
	{}

	public void keyPressed (KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE && escape()) doclose();
	}

	public void keyReleased (KeyEvent e)
	{}

	public void keyTyped (KeyEvent e)
	{}

	/**
	 * Closes the dialog. This may be used in subclasses to do some action. Then
	 * call super.doclose()
	 */
	public void doclose ()
	{
		setVisible(false);
		// Because of a bug in Linux Java 1.4.2 etc.
		// dispose in a separate thread.
		Thread t = new Thread()
		{
			@Override
			public void run ()
			{
				if (Dispose) dispose();
			}
		};
		t.start();
	}

	public void center (Frame f)
	{
		Dimension si = f.getSize(), d = getSize(), dscreen = getToolkit()
				.getScreenSize();
		Point lo = f.getLocation();
		int x = lo.x + si.width / 2 - d.width / 2;
		int y = lo.y + si.height / 2 - d.height / 2;
		if (x + d.width > dscreen.width) x = dscreen.width - d.width - 10;
		if (x < 10) x = 10;
		if (y + d.height > dscreen.height) y = dscreen.height - d.height - 10;
		if (y < 10) y = 10;
		setLocation(x, y);
	}

	static public void center (Frame f, Dialog dialog)
	{
		Dimension si = f.getSize(), d = dialog.getSize(), dscreen = f
				.getToolkit().getScreenSize();
		Point lo = f.getLocation();
		int x = lo.x + si.width / 2 - d.width / 2;
		int y = lo.y + si.height / 2 - d.height / 2;
		if (x + d.width > dscreen.width) x = dscreen.width - d.width - 10;
		if (x < 10) x = 10;
		if (y + d.height > dscreen.height) y = dscreen.height - d.height - 10;
		if (y < 10) y = 10;
		dialog.setLocation(x, y);
	}

	public void centerOut (Frame f)
	{
		Dimension si = f.getSize(), d = getSize(), dscreen = getToolkit()
				.getScreenSize();
		Point lo = f.getLocation();
		int x = lo.x + si.width - getSize().width + 20;
		int y = lo.y + si.height / 2 + 40;
		if (x + d.width > dscreen.width) x = dscreen.width - d.width - 10;
		if (x < 10) x = 10;
		if (y + d.height > dscreen.height) y = dscreen.height - d.height - 10;
		if (y < 10) y = 10;
		setLocation(x, y);
	}

	public void center ()
	{
		Dimension d = getSize(), dscreen = getToolkit().getScreenSize();
		setLocation((dscreen.width - d.width) / 2,
				(dscreen.height - d.height) / 2);
	}

	/**
	 * Note window position in Global.
	 */
	public void notePosition (String name)
	{
		Point l = getLocation();
		Dimension d = getSize();
		Global.setParameter(name + ".x", l.x);
		Global.setParameter(name + ".y", l.y);
		Global.setParameter(name + ".w", d.width);
		if (d.height - Global.getParameter(name + ".h", 0) != 19)
		// works around a bug in Windows
			Global.setParameter(name + ".h", d.height);
		boolean maximized = false;
	}

	/**
	 * Set window position and size.
	 */
	public void setPosition (String name)
	{
		Point l = getLocation();
		Dimension d = getSize();
		Dimension dscreen = getToolkit().getScreenSize();
		int x = Global.getParameter(name + ".x", l.x);
		int y = Global.getParameter(name + ".y", l.y);
		int w = Global.getParameter(name + ".w", d.width);
		int h = Global.getParameter(name + ".h", d.height);
		if (w > dscreen.width) w = dscreen.width;
		if (h > dscreen.height) h = dscreen.height;
		if (x < 0) x = 0;
		if (x + w > dscreen.width) x = dscreen.width - w;
		if (y < 0) y = 0;
		if (y + h > dscreen.height) y = dscreen.height - h;
		setLocation(x, y);
		setSize(w, h);
	}

	/**
	 * Override to set the focus somewhere.
	 */
	public void focusGained (FocusEvent e)
	{}

	public void focusLost (FocusEvent e)
	{}

	/**
	 * Note window size in Global.
	 */
	public void noteSize (String name)
	{
		Dimension d = getSize();
		Global.setParameter(name + ".w", d.width);
		Global.setParameter(name + ".h", d.height);
	}

	/**
	 * Set window size.
	 */
	public void setSize (String name)
	{
		if ( !Global.haveParameter(name + ".w"))
			pack();
		else
		{
			Dimension d = getSize();
			int w = Global.getParameter(name + ".w", d.width);
			int h = Global.getParameter(name + ".h", d.height);
			setSize(w, h);
		}
	}

	/**
	 * This inihibits dispose(), when the dialog is closed.
	 */
	public void setDispose (boolean flag)
	{
		Dispose = flag;
	}

	public boolean isAborted ()
	{
		return Aborted;
	}

	/**
	 * To add a help button to children.
	 * 
	 * @param p
	 * @param subject
	 */
	public void addHelp (Panel p, String subject)
	{
		p.add(new MyLabel(""));
		p.add(new ButtonAction(this, Global.name("help"), "Help"));
		Subject = subject;
	}

}
