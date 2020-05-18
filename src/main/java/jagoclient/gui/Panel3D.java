package jagoclient.gui;

import jagoclient.Global;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Panel3D extends the Panel class with a 3D look.
 */

public class Panel3D extends JPanel
{
	Component C;

	/**
	 * Adds the component to the panel. This component is resized to leave 5
	 * pixel on each side.
	 */
	public Panel3D (Component c)
	{
		C = c;
		add(C);
		if (Global.ControlBackground != null)
			setBackground(Global.ControlBackground);
	}

	public Panel3D (Component c, Color background)
	{
		C = c;
		add(C);
		setBackground(background);
	}

	/**
	 * An empty 3D panel.
	 */
	public Panel3D ()
	{
		C = null;
	}

	@Override
	public void paint (Graphics g)
	{
		g.setColor(getBackground());
		g.fill3DRect(0, 0, getSize().width - 1, getSize().height - 1, true);
		C.repaint();
	}

	@Override
	public void update (Graphics g)
	{
		paint(g);
	}

	@Override
	public void doLayout ()
	{
		if (C != null)
		{
			C.setLocation(5, 5);
			C.setSize(getSize().width - 10, getSize().height - 10);
			C.doLayout();
		}
		else super.doLayout();
	}
}
