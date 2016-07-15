package jagoclient.gui;

import java.awt.*;
import java.awt.event.*;

import jagoclient.Global;

import rene.gui.DoItemListener;

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
Similar to ChoiceAction, but for checkboxes.
@see jagoclient.gui.ChoiceAction
*/

public class CheckboxAction extends Checkbox
{   public CheckboxAction (DoItemListener c, String s)
    {   super(s);
        setFont(Global.SansSerif);
        if (c!=null) addItemListener(new CheckboxActionTranslator(this,c,s));
   }
    public CheckboxAction (DoItemListener c, String s, String h)
    {   super(s);
        if (c!=null) addItemListener(new CheckboxActionTranslator(this,c,h));
    }
}
