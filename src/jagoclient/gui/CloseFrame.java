package jagoclient.gui;

import jagoclient.Global;

import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import rene.util.list.ListClass;
import rene.util.list.ListElement;

/**
 * A Frame, which can be closed with the close button in the window. Moreover,
 * event handling is simplified with the DoActionListnener interface. There is
 * also a method for setting the icon of this window.
 */
public class CloseFrame extends Frame implements WindowListener,
	ActionListener, DoActionListener
{
	ListClass<CloseListener> L = new ListClass<CloseListener>();

	public CloseFrame (String s)
	{
		super("");
		addWindowListener(this);
		setTitle(s);
	}

	public void windowActivated (WindowEvent e)
	{}

	public void windowClosed (WindowEvent e)
	{}

	public void windowClosing (WindowEvent e)
	{
		if (close())
		{
			doclose();
		}
	}

	public void windowDeactivated (WindowEvent e)
	{}

	public void windowDeiconified (WindowEvent e)
	{}

	public void windowIconified (WindowEvent e)
	{}

	public void windowOpened (WindowEvent e)
	{}

	public boolean close ()
	{
		return true;
	}

	public void actionPerformed (ActionEvent e)
	{
		doAction(e.getActionCommand());
	}

	public void doAction (String o)
	{
		if (Global.resourceString("Close").equals(o) && close())
		{
			doclose();
		}
	}

	public void doclose ()
	{
		if (Global.getParameter("menuclose", true)) setMenuBar(null);
		setVisible(false);
		dispose();
	}

	public void addCloseListener (CloseListener cl)
	{
		L.add(new ListElement(L, cl));
	}

	public void inform ()
	{
		L.stream().forEach((e) -> {
			try
			{
				e.content().isClosed();
			}
			catch (Exception ex)
			{}
		});
	}

	public void removeCloseListener (CloseListener cl)
	{
		L.removeIf((ListElement<CloseListener> t) -> t.content() == cl);
	}

	@Override
	public void itemAction (String o, boolean flag)
	{}

	// the icon things
	static Map<String, Image> Icons = new HashMap<String, Image>();

	public void seticon (String file)
	{
		try
		{
			Object o = Icons.get(file);
			if (o == null)
			{
				Image i;
				InputStream in = getClass().getResourceAsStream(
					"/gifs/" + file);
				int pos = 0;
				int n = in.available();
				byte b[] = new byte[20000];
				while (n > 0)
				{
					int k = in.read(b, pos, n);
					if (k < 0) break;
					pos += k;
					n = in.available();
				}
				i = Toolkit.getDefaultToolkit().createImage(b, 0, pos);
				MediaTracker T = new MediaTracker(this);
				T.addImage(i, 0);
				T.waitForAll();
				Icons.put(file, i);
				setIconImage(i);
			}
			else
			{
				setIconImage((Image)o);
			}
		}
		catch (Exception e)
		{}
	}
}
