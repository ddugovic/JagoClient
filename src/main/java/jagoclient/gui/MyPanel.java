package jagoclient.gui;

import jagoclient.Global;

import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * Panel which respects color configuration settings.
 *
 * @see jagoclient.dialogs.ColorEdit
 */
public class MyPanel extends JPanel
{
	public MyPanel ()
	{
		setBackground(Global.ControlBackground);
	}
	public MyPanel (LayoutManager layout)
	{
		super(layout);
		setBackground(Global.ControlBackground);
	}
	public MyPanel (boolean isDoubleBuffered)
	{
		super(isDoubleBuffered);
		setBackground(Global.ControlBackground);
	}
	public MyPanel (LayoutManager layout, boolean isDoubleBuffered)
	{
		super(layout, isDoubleBuffered);
		setBackground(Global.ControlBackground);
	}
}
