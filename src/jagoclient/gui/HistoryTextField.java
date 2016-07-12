package jagoclient.gui;

import java.awt.*;
import java.awt.event.*;

import jagoclient.Global;

import rene.util.list.ListClass;
import rene.util.list.ListElement;

/**
A TextField, which display the old input, when cursor up is
pressed. The old input is stored in a list. The class is derived
from TextFieldAction.
@see TextFieldAction
*/

public class HistoryTextField extends TextFieldAction
    implements KeyListener,DoActionListener
{	ListClass<String> H;
	PopupMenu M=null;
	public HistoryTextField (DoActionListener l, String name)
	{	super(l,name);
	    H=new ListClass<String>();
		H.append("");
		addKeyListener(this);
	}
	public HistoryTextField (DoActionListener l, String name, int s)
	{	super(l,name,s);
	    H=new ListClass<String>();
		H.append("");
		addKeyListener(this);
	}
	@Override
	public void keyPressed (KeyEvent ev)
	{	switch (ev.getKeyCode())
		{	case KeyEvent.VK_UP :
			case KeyEvent.VK_DOWN : 
				if (M==null)
				{	M=new PopupMenu();
					for (ListElement<String> e : H)
					{	String t=e.content();
						if (!t.equals(""))
						{	MenuItem item=new MenuItemAction(this,t,t);
							M.add(item);
						}
					}
					add(M);
				}
				M.show(this,10,10);
				break;
			default : return;
		}
	}
	@Override
	public void keyReleased (KeyEvent e) {}
	@Override
	public void keyTyped (KeyEvent e) {}
	String Last;
	public void remember (String s)
	{	if (s.equals(Last)) return;
		deleteFromHistory(s);
		Last=s;
		H.last().content(s);
		H.append("");
		M=null;
	}
	public void deleteFromHistory (String s)
	{
		H.removeIf((ListElement<String> t) -> t.content().equals(s));
	}
	public void remember ()
	{	remember(getText());
	}
	public void saveHistory (String name)
	{	int i,n=Global.getParameter("history.length",10);
		Global.removeAllParameters("history."+name);
		ListElement e=H.last();
		if (e==null) return;
		for (i=0; i<n && e!=null; e=e.previous())
		{	String s=(String)e.content();
			if (!s.equals(""))
			{	i++;
				Global.setParameter("history."+name+"."+i,s);
			}
		}
	}
	public void loadHistory (String name)
	{	int i=1;
		while (Global.haveParameter("history."+name+"."+i))
		{	String s=Global.getParameter("history."+name+"."+i,"");
			if (!s.equals("")) H.prepend(s);
			i++;
		}
	}
	public void doAction (String o)
	{	if (!o.equals(""))
		{	setText(o);
		}
	}
	public void itemAction (String o, boolean flag)
	{
	}
}
