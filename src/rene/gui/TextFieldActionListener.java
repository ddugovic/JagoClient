package rene.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class TextFieldActionListener implements ActionListener
{   DoActionListener C;
    String Name;
    public TextFieldActionListener (DoActionListener c, String name)
    {   C=c; Name=name;
    }
    public void actionPerformed (ActionEvent e)
    {   C.doAction(Name);
    }
}

