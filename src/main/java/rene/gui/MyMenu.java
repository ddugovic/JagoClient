package rene.gui;

import java.awt.Menu;

public class MyMenu extends Menu
{
	public MyMenu (String s)
	{
		super(s);
		if (Global.NormalFont != null) setFont(Global.NormalFont);
	}
}
