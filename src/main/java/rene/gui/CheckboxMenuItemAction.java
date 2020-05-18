package rene.gui;

import java.awt.CheckboxMenuItem;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class CheckboxTranslator implements ItemListener
{   DoItemListener C;
    String S;
    public CheckboxMenuItem CB;
    public CheckboxTranslator 
        (CheckboxMenuItem cb, DoItemListener c, String s)
    {   C=c; S=s; CB=cb;
    }
    public void itemStateChanged (ItemEvent e)
    {   C.itemAction(S,CB.getState());
    }
}

/**
 * A CheckboxMenuItem with customizable font.
 * <p>
 * This is to be used in DoActionListener interfaces.
 */
public class CheckboxMenuItemAction extends CheckboxMenuItem
{   public CheckboxMenuItemAction (DoItemListener c, String s, String st)
    {   super(s);
        addItemListener(new CheckboxTranslator(this,c,st));
        if (Global.NormalFont!=null) setFont(Global.NormalFont);
    }
	public CheckboxMenuItemAction (DoItemListener c, String s)
	{	this(c,s,s);
	}
}
