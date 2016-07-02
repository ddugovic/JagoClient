/*
 * Created on 14.01.2006
 *
 * This is a display for lists of items, stored in rene.lister.Element.
 * The display has two optional scrollbars and a ListerPanel.
 */
package rene.lister;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;

import rene.gui.*;

public class Lister 
	extends Panel implements AdjustmentListener
{	public ListerPanel L;
	Scrollbar Vertical,Horizontal;
	
	/**
	 * Initialize the display and the two optional scrollbars
	 * @param verticalscrollbar
	 * @param horizontal scrollbar
	 */
	public Lister (boolean vs, boolean hs)
	{	L=new ListerPanel(this);
		setLayout(new BorderLayout());
		add("Center",L);
		if (vs)
		{	add("East",Vertical=new Scrollbar(Scrollbar.VERTICAL,0,100,0,1100));
			Vertical.addAdjustmentListener(this);
		}
		if (hs)
		{	add("South",Horizontal=new Scrollbar(Scrollbar.HORIZONTAL,0,100,0,1100));
			Horizontal.addAdjustmentListener(this);
		}
	}
	public Lister ()
	{	this(true,true);
	}
	
	/**
	 * Calles by the lister to set the vertical scrollbars.
	 * @param vp vertical position
	 * @param vs vertical size
	 * @param hp horizontal position
	 * @param hs horizontal size
	 */
	public void setScrollbars (double vp, double vs, double hp, double hs)
	{	if (Vertical!=null)
		{	int size=(int)(vs*1000);
			int max=1000+size;
			int pos=(int)(vp*1000);
			Vertical.setValues(pos,size,0,max);
		}
	}
	
	/**
	 * Called by the scrollbars.
	 */
	public void adjustmentValueChanged (AdjustmentEvent e) 
	{	if (Vertical!=null && e.getSource()==Vertical)
		{	switch (e.getAdjustmentType())
			{	case AdjustmentEvent.UNIT_INCREMENT :
					L.up(1); break;
				case AdjustmentEvent.UNIT_DECREMENT :
					L.down(1); break;
				case AdjustmentEvent.BLOCK_INCREMENT :
					L.pageUp(); break;
				case AdjustmentEvent.BLOCK_DECREMENT :
					L.pageDown(); break;
				default :
					int size=Vertical.getVisibleAmount();
					int max=Vertical.getMaximum();
					int pos=Vertical.getValue();
					L.setVerticalPos((double)(pos)/(max-size));
			}
		}
		else if (Horizontal!=null && e.getSource()==Horizontal)
		{	int pos=Horizontal.getValue();
			switch (e.getAdjustmentType())
			{	case AdjustmentEvent.UNIT_INCREMENT :
					pos+=10; break;
				case AdjustmentEvent.UNIT_DECREMENT :
					pos-=10; break;
				case AdjustmentEvent.BLOCK_INCREMENT :
					pos+=50; break;
				case AdjustmentEvent.BLOCK_DECREMENT :
					pos-=50; break;
			}
			L.setHorizontalPos((double)(pos)/1000);
			Horizontal.setValue(pos);
		}
	}
	
	/**
	 * Return the lister for external use.
	 * @return lister panel
	 */
	public ListerPanel getLister ()
	{	return L;
	}
	
	public void addActionListener (ActionListener al)
	{	L.addActionListener(al);
	}
	
	public void updateDisplay ()
	{	L.repaint();
	}
	
	public void removeActionListener (ActionListener al)
	{	L.removeActionListener(al);
	}
	
	public void clear ()
	{	L.clear();
	}
	
	public void addElement (Element el)
	{	L.add(el);
	}
	
	/**
	 * Get the first selected index.
	 * @return index or -1
	 */
	public int getSelectedIndex ()
	{	if (L.Selected.size()>0) 
			return ((Integer)L.Selected.elementAt(0)).intValue();
		else
			return -1;
	}
	
	public String getSelectedItem ()
	{	int n=getSelectedIndex();
		if (n<0) return null;
		return L.getElementAt(n).getElementString();
	}
	
	/**
	 * Get a vector of all selected indices.
	 * @return vector of indices
	 */
	public int[] getSelectedIndices ()
	{	int k[]=new int[L.Selected.size()];
		for (int i=0; i<k.length; i++)
			k[i]=((Integer)L.Selected.elementAt(i)).intValue();
		return k;
	}
	
	/**
	 * Make sure, the lister shows the last element.
	 */
	public void showLast ()
	{	L.showLast();
	}

	/**
	 * Set the operations mode.
	 * @param multiple allows multiple clicks
	 * @param easymultiple multiple selection without control
	 * @param singleclick report single click events
	 * @param rightmouse report right mouse clicks
	 */
	public void setMode (boolean multiple, 
			boolean easymultiple, 
			boolean singleclick,
			boolean rightmouse)
	{	L.MultipleSelection=multiple;
		L.EasyMultipleSelection=easymultiple;
		L.ReportSingleClick=singleclick;
		L.RightMouseClick=rightmouse;
	}
	
	/**
	 * Print the lines to the printwriter o.
	 * @param o
	 */
	public void save (PrintWriter o)
	{	L.save(o);
	}
	
	public void select (int sel)
	{	
	}
	
	/**
	 * Shortcut to add a string with a specific color.
	 * @param name
	 * @param col
	 */
	public void addElement (String name, Color col)
	{	addElement(new StringElement(name,col));
	}
	public void addElement (String name)
	{	addElement(new StringElement(name));
	}
	
	public static void main (String args[])
	{	CloseFrame F=new CloseFrame("Test");
		F.setSize(300,400);
		F.setLocation(200,200);
		F.setLayout(new BorderLayout());
		Lister L=new Lister(true,true);
		F.add("Center",L);
		for (int i=0; i<1000; i++)
		{	L.getLister().add(
				new StringElement(
						"-------------- This is line number: "+i,
						new Color(0,0,i%256)));
		}	
		F.setVisible(true);
	}

	public void setState (int s)
	{	L.setState(s);
	}

	public void setListingBackground (Color c)
	{	L.setListingBackground(c);
	}
}
