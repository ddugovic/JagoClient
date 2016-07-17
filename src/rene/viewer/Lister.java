package rene.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.ItemSelectable;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import rene.util.list.ListElement;

public class Lister extends Viewer 
	implements MouseListener,ItemSelectable,KeyListener,FocusListener
{	ListElement Chosen=null;
	Vector<ActionListener> AL;
	Vector<ItemListener> IL;
	PopupMenu PM=null;
	public boolean FocusTraversable=true;
	boolean Focus=false;
	boolean Multiple=false;

	public Lister ()
	{	super(true,true);
		AL=new Vector<ActionListener>();
		IL=new Vector<ItemListener>();
		addKeyListener(this);
		addFocusListener(this);
	}

	public Lister (String dummy)
	{	super(dummy);
	}
	
	public void setMultipleMode (boolean flag)
	{	Multiple=flag;
	}

	@Override
	public void keyPressed (KeyEvent e)
	{	if (Multiple) return;
		if (e.getKeyCode()==KeyEvent.VK_ENTER || e.getKeyCode()==KeyEvent.VK_SPACE)
		{	if (Chosen==null) return;
			if (e.isControlDown())
			{	for (ItemListener li : IL)
				{	li.itemStateChanged(
						new ItemEvent(this,0,getSelectedItem(),
							ItemEvent.ITEM_STATE_CHANGED));
				}
			}
			for (ActionListener li : AL)
			{	li.actionPerformed(
					new ActionEvent(this,0,getSelectedItem()));
			}
			return;
		}
		else if (e.getKeyCode()==KeyEvent.VK_DOWN || e.getKeyCode()==KeyEvent.VK_UP)
		{	if (Chosen==null)
			{	Chosen=TD.L.first();
				((Line)Chosen.content()).chosen(true);
				if (Chosen==null) return;
				TD.showLine(Chosen);
				TD.repaint();
			}
			else if (e.getKeyCode()==KeyEvent.VK_DOWN)
			{	((Line)Chosen.content()).chosen(false);
				Chosen=Chosen.next();
				if (Chosen==null) Chosen=TD.L.first();
				((Line)Chosen.content()).chosen(true);
				TD.showLine(Chosen);
				TD.repaint();
			}
			else if (e.getKeyCode()==KeyEvent.VK_UP)
			{	((Line)Chosen.content()).chosen(false);
				Chosen=Chosen.previous();
				if (Chosen==null) Chosen=TD.L.last();
				((Line)Chosen.content()).chosen(true);
				TD.showLine(Chosen);
				TD.repaint();
			}
			for (ItemListener li : IL)
			{	li.itemStateChanged(
					new ItemEvent(this,0,getSelectedItem(),
						ItemEvent.ITEM_STATE_CHANGED));
			}
		}
	}
	
	/*
	public boolean isFocusTraversable ()
	{	return FocusTraversable;
	}
	*/
	
	public String getSelectedItem ()
	{	if (Chosen==null) return null;
		return new String(((Line)Chosen.content()).a);
	}
	public void setText (String s)
	{	Chosen=null;
		super.setText(s);
	}
	public void add (String s)
	{	appendLine(s);
	}
	public void add (String s, Color c)
	{	appendLine(s,c);
	}
	public void addItem (String s)
	{	add(s);
	}

	public void addActionListener (ActionListener l)
	{	AL.addElement(l);
	}
	public void addItemListener (ItemListener l)
	{	IL.addElement(l);
	}
	public void removeItemListener (ItemListener l)
	{	IL.removeElement(l);
	}
	public Object[] getSelectedObjects ()
	{	return getSelectedItems();
	}
	public String[] getSelectedItems ()
	{	int n=0;
		for (ListElement<Line> le : TD.L)
		{	Line l=le.content();
			if (l.chosen()) n++;
		}
		String s[]=new String[n];
		n=0;
		for (ListElement<Line> le : TD.L)
		{	Line l=le.content();
			if (l.chosen())
				s[n++]=new String(l.a);
		}
		return s;
	}
	public int[] getSelectedIndexes ()
	{	int n=0;
		for (ListElement<Line> le : TD.L)
		{	Line l=le.content();
			if (l.chosen()) n++;
		}
		int s[]=new int[n];
		n=0;
		int i=0;
		for (ListElement<Line> le : TD.L)
		{	Line l=le.content();
			if (l.chosen())
				s[n++]=i;
			i++;
		}
		return s;
	}
	public int getSelectedIndex ()
	{	int k[]=getSelectedIndexes();
		if (k.length==0) return -1;
		else return k[0];
	}
	public void select (int n)
	{
		int i=0;
		for (ListElement<Line> le : TD.L)
		{	Line l=le.content();
			if (i==n)
			{	l.chosen(true);
				Chosen=le;
				TD.repaint();
				return;
			}
			i++;
		}
	}
	public void select (String s)
	{
		for (ListElement<Line> le : TD.L)
		{	Line l=le.content();
			if (s.equals(l.a))
			{	l.chosen(true);
				Chosen=le;
				TD.repaint();
				return;
			}
		}
	}

	public void mouseClicked (MouseEvent e) {}
	public void mousePressed (MouseEvent e)
	{	if (e.getClickCount()>=2 || e.isControlDown())
		{	if (e.isControlDown())
			{
				for (ItemListener li : IL)
				{	li.itemStateChanged(
						new ItemEvent(this,0,getSelectedItem(),
							ItemEvent.ITEM_STATE_CHANGED));
				}
			}
			for (ActionListener li : AL)
			{	li.actionPerformed(
					new ActionEvent(this,0,getSelectedItem()));
			}
			return;
		}
		requestFocus();
		ListElement le=TD.getline(e.getY());
		if (le==null) return;
		Line l=(Line)le.content();
		if (!Multiple && Chosen!=null)
		{	((Line)Chosen.content()).chosen(false);
		}
		Chosen=le;
		l.chosen(!l.chosen());
		TD.paint(TD.getGraphics());
		if (Multiple) return;
		for (ItemListener li : IL)
		{	li.itemStateChanged(
				new ItemEvent(this,0,getSelectedItem(),
					ItemEvent.ITEM_STATE_CHANGED));
		}
		if (e.isPopupTrigger() || e.isMetaDown() && PM!=null)
		{	PM.show(e.getComponent(),e.getX(),e.getY());
		}		
	}
	public void mouseReleased (MouseEvent e) {}
	public void mouseEntered (MouseEvent e) {}
	public void mouseExited (MouseEvent e) {}

	public void removeAll ()
	{	setText("");
	}
	
	public static Lister v;

	public static void main (String args[])
	{	Frame f=new rene.gui.CloseFrame()
			{	public void doclose()
				{	String s[]=v.getSelectedItems();
					for (int i=0; i<s.length; i++)
					{	System.out.println(s[i]);
					}
					super.doclose();
					System.exit(0);
				}
			};
		f.setLayout(new BorderLayout());
		v=new Lister();
		f.add("Center",v);
		f.setSize(300,300);
		f.setVisible(true);
		v.add("test",Color.black);
		v.addItem("test");
		for (int i=0; i<10; i++)
			v.add("test "+i,
				new Color((float)Math.random()/2,
					(float)Math.random()/2,(float)Math.random()/2));
		v.setMultipleMode(true);
	}
	
	public void setPopupMenu (PopupMenu pm)
	{	PM=pm;
		add(PM);
	}

	public void focusGained (FocusEvent e)
	{	Focus=true; TD.repaint();
	}
	public void focusLost (FocusEvent e)
	{	Focus=false; TD.repaint();
	}
	public boolean hasFocus () { return Focus; }

	public void setBackground (Color c)
	{	TD.setBackground(c);
		super.setBackground(c);
	}	

}
