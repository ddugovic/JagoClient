package rene.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Panel;

/**
Panel3D extends the Panel class with a 3D look.
*/

public class Panel3D extends Panel
	implements LayoutManager
{	Component C;
	/**
	Adds the component to the panel.
	This component is resized to leave 5 pixel on each side.
	*/
	public Panel3D (Component c)
	{	C=c;
		setLayout(this);
		add(C);
		setBackground(C.getBackground());
	}
	
	public Panel3D (Component c, Color background)
	{	C=c;
		setLayout(this);
		add(C);
		setBackground(background);
	}
	
	public void paint (Graphics g)
	{	g.setColor(getBackground());
		if (getSize().width>0 && getSize().height>0)
			g.fill3DRect(0,0,getSize().width,getSize().height,true);
		// C.repaint(); // probably not necessary, but Mac OSX bug ?!?
	}

	public void addLayoutComponent (String arg0, Component arg1) 
	{	C=arg1;
	}
	
	public void removeLayoutComponent(Component arg0) 
	{	C=null;
	}
	
	public Dimension preferredLayoutSize (Container arg0) 
	{	if (C!=null) return new Dimension(
			C.getPreferredSize().width+10,C.getPreferredSize().height+10);
		return new Dimension(10,10);
	}
	
	public Dimension minimumLayoutSize (Container arg0) 
	{	if (C!=null) return new Dimension(
			C.getMinimumSize().width+10,C.getMinimumSize().height+10);
		return new Dimension(10,10);
	}
	
	public Dimension getPreferredSize ()
	{	if (C!=null) return new Dimension(
			C.getPreferredSize().width+10,C.getPreferredSize().height+10);
		return new Dimension(10,10);
	}
	
	public Dimension getMinimumSize ()
	{	return getPreferredSize();
	}
	
	public void layoutContainer (Container arg0) 
	{	if (C==null) return;
		C.setLocation(5,5);
		C.setSize(getSize().width-10,getSize().height-10);
	}
	
	public static void main (String args[])
	{	CloseFrame f=new CloseFrame ("Test");
		f.add("Center",new Panel3D(new MyPanel()));
		f.setSize(400,400);
		f.setLocation(100,100);
		f.setVisible(true);
	}
}
