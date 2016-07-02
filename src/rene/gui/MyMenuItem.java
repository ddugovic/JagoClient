package rene.gui;

import java.awt.MenuItem;

public class MyMenuItem extends MenuItem
{
	public MyMenuItem (String s)
	{
		super(s);
		if (Global.NormalFont != null) setFont(Global.NormalFont);
	}
}
