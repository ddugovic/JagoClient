package rene.gui;

import java.awt.Graphics;
import java.awt.Panel;

public class MyPanel extends Panel
{
	public MyPanel ()
	{
		if (Global.ControlBackground != null)
			setBackground(Global.ControlBackground);
		repaint();
	}

	@Override
	public void paint (Graphics g)
	{
		super.paint(g);
		getToolkit().sync();
	}
}
