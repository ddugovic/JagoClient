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

public class ShadowPanel extends Panel
	implements LayoutManager
{	Component C;
	public int Boundary=6;

	/**
	Adds the component to the panel.
	This component is resized to leave 5 pixel on each side.
	*/
	public ShadowPanel (Component c)
	{	C=c;
		setLayout(this);
		add(C);
		setBackground(C.getBackground());
	}
	
	public ShadowPanel (Component c, Color background)
	{	C=c;
		setLayout(this);
		add(C);
		setBackground(background);
	}
	
	public void paint (Graphics g)
	{	g.setColor(getBackground());
		if (getSize().width>0 && getSize().height>0)
			g.fillRect(0,0,getSize().width,getSize().height);
		int k=Boundary/3;
		Color cb=getBackground();
		double red=cb.getRed()/255.0,green=cb.getGreen()/255.0,blue=cb.getBlue()/255.0;
		for (int i=0; i<=2*k; i++)
		{	double x=(double)i/(2*k);
			x=1.0-0.5*x;
			g.setColor(new Color((float)(red*x),(float)(green*x),(float)(blue*x)));
			g.fillRect(i+2*k,i+2*k,
				getSize().width-(i+2*k)-i,getSize().height-(i+2*k)-i);
		}
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
			C.getPreferredSize().width+2*Boundary,C.getPreferredSize().height+2*Boundary);
		return new Dimension(10,10);
	}
	
	public Dimension minimumLayoutSize (Container arg0) 
	{	if (C!=null) return new Dimension(
			C.getMinimumSize().width+2*Boundary,C.getMinimumSize().height+2*Boundary);
		return new Dimension(10,10);
	}
	
	public Dimension getPreferredSize ()
	{	if (C!=null) return new Dimension(
			C.getPreferredSize().width+2*Boundary,C.getPreferredSize().height+2*Boundary);
		return new Dimension(10,10);
	}
	
	public Dimension getMinimumSize ()
	{	return getPreferredSize();
	}
	
	public void layoutContainer (Container arg0) 
	{	if (C==null) return;
		C.setLocation(Boundary,Boundary);
		C.setSize(getSize().width-2*Boundary,getSize().height-2*Boundary);
	}
	
	public static void main (String args[])
	{	CloseFrame f=new CloseFrame ("Test");
		MyPanel p=new MyPanel();
		//p.setBackground(Color.green);
		f.add("Center",new ShadowPanel(p,f.getBackground()));
		f.setSize(400,400);
		f.setLocation(100,100);
		f.setVisible(true);
	}
}
