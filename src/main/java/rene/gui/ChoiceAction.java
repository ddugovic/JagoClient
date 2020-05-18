package rene.gui;

import java.awt.Choice;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
@see rene.gui.CloseFrame#doAction
@see rene.gui.CloseDialog#doAction
*/

public class ChoiceAction extends Choice
{   public ChoiceAction (DoItemListener c, String s)
    {   addItemListener(new ChoiceTranslator(this,c,s));
        if (Global.NormalFont!=null) setFont(Global.NormalFont);
		if (Global.Background!=null) setBackground(Global.Background);
    }
}
