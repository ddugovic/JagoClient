package jagoclient.gui;

import java.awt.*;
import java.awt.event.*;

import jagoclient.Global;

import rene.gui.DoItemListener;

class ChoiceTranslator implements ItemListener
{   DoItemListener C;
    String S;
    public Choice Ch;
    public ChoiceTranslator
        (Choice ch, DoItemListener c, String s)
    {   C=c; S=s; Ch=ch;
    }
    public void itemStateChanged (ItemEvent e)
    {   C.itemAction(S,e.getStateChange()==ItemEvent.SELECTED);
    }
}

/**
This is a choice item, which sets a specified font and translates
events into strings, which are passed to the doAction method of the
DoActionListener.
@see jagoclient.gui.CloseFrame#doAction
@see jagoclient.gui.CloseDialog#doAction
*/

public class ChoiceAction extends Choice
{   public ChoiceAction (DoItemListener c, String s)
    {   addItemListener(new ChoiceTranslator(this,c,s));
        setFont(Global.SansSerif);
    }
}
