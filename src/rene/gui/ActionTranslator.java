package rene.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
A translator for Actions.
*/

public class ActionTranslator implements ActionListener
{   String Name;
    DoActionListener C;
    ActionEvent E;
    public ActionTranslator (DoActionListener c, String name)
    {   Name=name; C=c;
    }
    public void actionPerformed (ActionEvent e)
    {   E=e;
    	C.doAction(Name);
    }
    public void trigger ()
    {	C.doAction(Name);
    }
}
