package jagoclient.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import jagoclient.Global;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import rene.gui.DoActionListener;

/**
A TextField, which display the old input, when cursor up is
pressed. The old input is stored in a list. The class is derived
from TextFieldAction.
@see TextFieldAction
*/

public class HistoryTextField extends TextFieldAction
    implements KeyListener,DoActionListener
{	LinkedList<String> H;
	PopupMenu M=null;
	public HistoryTextField (DoActionListener l, String name)
	{	super(l,name);
	    H=new LinkedList<String>();
		H.add("");
		addKeyListener(this);
	}
	public HistoryTextField (DoActionListener l, String name, int s)
	{	super(l,name,s);
	    H=new LinkedList<String>();
		H.add("");
		addKeyListener(this);
	}
	@Override
	public void keyPressed (KeyEvent ev)
	{	switch (ev.getKeyCode())
		{	case KeyEvent.VK_UP :
			case KeyEvent.VK_DOWN : 
				if (M==null)
				{	M=new PopupMenu();
					for (String t : H)
					{	if (!t.equals(""))
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
		H.set(H.size()-1, s);
		H.add("");
		M=null;
	}
	public void deleteFromHistory (String s)
	{
		H.remove(s);
	}
	public void remember ()
	{	remember(getText());
	}
	public void saveHistory (String name)
	{	int n=Global.getParameter("history.length",10);
		Global.removeAllParameters("history."+name);
		if (H.isEmpty()) return;
		Iterator<String> it = H.descendingIterator();
		for (int i=0; i<n && it.hasNext(); )
		{	String s=it.next();
			if (!s.isEmpty())
			{	i++;
				Global.setParameter("history."+name+"."+i,s);
			}
		}
	}
	public void loadHistory (String name)
	{	int i=1;
		while (Global.haveParameter("history."+name+"."+i))
		{	String s=Global.getParameter("history."+name+"."+i,"");
			if (!s.equals("")) H.addFirst(s);
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
