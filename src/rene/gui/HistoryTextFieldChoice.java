package rene.gui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import rene.util.FileName;
import rene.util.MyVector;
import rene.util.list.ListClass;
import rene.util.list.ListElement;

public class HistoryTextFieldChoice extends MyChoice
	implements ItemListener
{	HistoryTextField T;
	DoActionListener AL;
	MyVector V=new MyVector();
	public int MaxLength=32;

	public HistoryTextFieldChoice (HistoryTextField t)
	{	T=t;
		addItemListener(this);
	}
	
	public void setDoActionListener (DoActionListener al)
	{	AL=al;
	}
	
	public void itemStateChanged (ItemEvent e)
	{	int n=getSelectedIndex();
		String s=(String)V.elementAt(n);
		if (s.equals("   ")) return;
		if (AL!=null) AL.doAction(s); 
		else T.doAction(s);
	}
	
	public void update ()
	{	removeAll();
		V.removeAllElements();
		ListClass l=T.getHistory();
		ListElement e=l.last();
		if (e==null || ((String)e.content()).equals(""))
		{	V.addElement("   ");
			add("   ");
		}
		while (e!=null)
		{	String s=(String)e.content();
			if (!s.equals(""))
			{	V.addElement(s);
				add(FileName.chop(s,MaxLength));
			}
			e=e.previous();
		}
	}
	
	public String getRecent ()
	{	if (V.size()>1) return (String)V.elementAt(1);
		else return "";
	}
}
