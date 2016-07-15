package rene.gui;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Label;

/**
 * A Label with a customizable Font.
 */
public class MyLabel extends Label
{   public MyLabel (String s)
    {   super(s);
        if (Global.NormalFont!=null) setFont(Global.NormalFont);
    }
    public MyLabel (String s, int allign)
    {   super(s,allign);
        if (Global.NormalFont!=null) setFont(Global.NormalFont);
    }
    /**
    This is for Java 1.2 on Windows.
    */
    public void paint (Graphics g)
    {	Container c=getParent();
    	if (c!=null) setBackground(c.getBackground());
    	super.paint(g);
    }
}
