package rene.gui;

import java.awt.Checkbox;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class CheckboxActionTranslator implements ItemListener
{   DoItemListener C;
    String S;
    public Checkbox CB;
    public CheckboxActionTranslator
        (Checkbox cb, DoItemListener c, String s)
    {   C=c; S=s; CB=cb;
    }
    public void itemStateChanged (ItemEvent e)
    {   C.itemAction(S,CB.getState());
    }
}

/**
 * A Checkbox with customizable font.
 * <p>
 * To be used in DoActionListener interfaces.
 */
public class CheckboxAction extends Checkbox
{   public CheckboxAction (DoItemListener c, String s)
    {   super(s);
    	if (Global.NormalFont!=null) setFont(Global.NormalFont);
        if (c!=null) addItemListener(new CheckboxActionTranslator(this,c,s));
    }
    public CheckboxAction (DoItemListener c, String s, String h)
    {   super(s);
    	if (Global.NormalFont!=null) setFont(Global.NormalFont);
        if (c!=null) addItemListener(new CheckboxActionTranslator(this,c,h));
    }
}
