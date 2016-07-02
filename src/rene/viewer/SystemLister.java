package rene.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.List;
import java.awt.PopupMenu;

public class SystemLister extends Lister
{   List L;
	static Font F=null;
	public SystemLister ()
    {   super("dummy");
    	setLayout(new BorderLayout());
    	add("Center",L=new java.awt.List());
    	if (F!=null) L.setFont(F);
    }
   	public String getSelectedItem ()
   	{	return L.getSelectedItem();
   	}
   	public void appendLine (String s)
   	{	L.add(s);
   	}
   	public void setText (String s)
   	{	if (s.equals("")) L.removeAll();
   		else L.add(s);
   	}
	public void add (String s)
	{	L.add(s);
	}
	public void add (String s, Color c)
	{	add(s);
	}
	public void setPopupMenu (PopupMenu pm)
	{
	}
}
