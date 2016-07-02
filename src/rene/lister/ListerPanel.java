/*
 * Created on 14.01.2006
 *
 */
package rene.lister;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.*;

import rene.gui.*;
import rene.util.*;

public class ListerPanel
	extends MyPanel implements WheelListener
{
		private MyVector V; // Vector of listed Elements
		int Top; // Top Element
	
		Image I; // Buffer Image
		int W,H; // width and height of current panel and image
		Graphics IG; // Graphics for the image
		Font F; // current font
		FontMetrics FM; // current font metrics
		int Leading,Height,Ascent,Descent; // font stuff
		int PageSize; // numbers of lines per page
		int HOffset; // horizontal offset of display
		boolean ShowLast; // Show last on next redraw
		
		Lister LD;
		String Name;
		
		public Color ListingBackground=null;
		
		public boolean MultipleSelection=true; // Allow multiple selection
		public boolean EasyMultipleSelection=false; // Multiple select without right mouse
		public boolean ReportSingleClick=false; // Report single clicks also
		public boolean RightMouseClick=false; // Report right mouse clicks also
		
		public ListerPanel (Lister ld, String name)
		{	LD=ld; Name=name;
			V=new MyVector();
			Top=0;
			Wheel W=new Wheel(this);
			addMouseWheelListener(W);
			addMouseListener(new MouseAdapter()
				{	public void mouseClicked (MouseEvent e)
					{	clicked(e);
					}
				}
			);
		}
		
		public ListerPanel (Lister ld)
		{	this(ld,"");
		}
		
		/**
		 * Paint routine.
		 * Simply sets up the buffer image, calls dopaint
		 * and paints the image to the screen.
		 */
		public synchronized void paint (Graphics g)
		{	Dimension d=getSize();
			if (I==null || I.getWidth(this)!=d.width ||
					I.getHeight(this)!=d.height)
			{	I=createImage(W=d.width,H=d.height);
				if (I==null) return;
				IG=I.getGraphics();
				init();
			}
			dopaint(IG);
			g.drawImage(I,0,0,W,H,this);
			double vp,vs,hp,hs;
			if (V.size()>1) vp=(double)Top/V.size();
			else vp=0;
			if (V.size()>2*PageSize) vs=(double)PageSize/V.size();
			else vs=0.5;
			if (HOffset<10*W) hp=(double)HOffset/(10*W);
			else hp=0.9;
			hs=0.1;
			LD.setScrollbars(vp,vs,hp,hs);
		}
		
		public void update (Graphics g)
		{	paint(g);	
		}
		
		/**
		 * Initialize the font stuff and
		 * set the background of the panel.
		 */
		synchronized void init ()
		{	F=getFont();
			FM=getFontMetrics(F);
			Leading=FM.getLeading()+Global.getParameter("fixedfont.spacing",-1);
			Height=FM.getHeight();
			Ascent=FM.getAscent();
			Descent=FM.getDescent();
			if (Global.Background!=null) setBackground(Global.Background);
			if (Height+Leading>0)
				PageSize=H/(Height+Leading);
			else
				PageSize=10;
			antialias(true);
			Top=0;
		}

		/**
		 * Set Anti-Aliasing on or off, if in Java 1.2 or better and the Parameter
		 * "font.smooth" is switched on.
		 * @param flag
		 */
		public void antialias (boolean flag)
		{	if (Global.getParameter("font.smooth",true))
			{	IG=(Graphics2D)IG;
				((Graphics2D)IG).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					flag?RenderingHints.VALUE_TEXT_ANTIALIAS_ON:
						RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			}
		}

		/**
		 * Paint the current text lines on the image.
		 * @param g
		 */
		public synchronized void dopaint (Graphics g)
		{	if (ShowLast)
			{	Top=V.size()-PageSize+1;
				if (Top<0) Top=0;
				ShowLast=false;
			}
			if (ListingBackground!=null) g.setColor(ListingBackground); 
			else g.setColor(getBackground());
			g.fillRect(0,0,W,H);
			g.setColor(Color.black);
			int h=Leading+Ascent;
			int totalh=getSize().height-Descent;
			int line=Top;
			if (line<0) return;
			while (line-Top<PageSize && line<V.size())
			{	Element el=(Element)V.elementAt(line);
				if (isSelected(line))
				{	g.setColor(getBackground().darker());
					g.fillRect(0,h-Ascent,W,Height);
					g.setColor(Color.black);
				}
				Color col=el.getElementColor();
				if (col!=null) g.setColor(col);
				else g.setColor(Color.black);
				g.drawString(el.getElementString(State),2-HOffset,h);
				h+=Leading+Height;
				line++;
			}
		}
		
		int State=0;
		public void setState (int s)
		{	State=s;
		}

		/**
		 * Add a new line of type rene.lister.Element
		 * @param e
		 */
		public synchronized void add (Element e)
		{	V.addElement(e);
		}
		
		// Used by the mouse wheel or external programs:
		
		public synchronized void up (int n) 
		{	Top+=n;
			if (Top>=V.size()) Top=V.size()-1;
			if (Top<0) Top=0;
			repaint();
		}

		public synchronized void down (int n) 
		{	Top-=n;
			if (Top<0) Top=0;
			repaint();
		}

		public synchronized void pageUp() 
		{	up(PageSize-1);
			repaint();
		}

		public synchronized void pageDown() 
		{	down(PageSize-1);
			repaint();
		}	

		/**
		 * Set the vertical position.
		 * Used by the scrollbars in the Lister. 
		 * @param x percentage of text
		 */		
		public synchronized void setVerticalPos (double x)
		{	Top=(int)(x*V.size());
			if (Top>=V.size()) Top=V.size()-1;
			repaint();
		}
		
		/**
		 * Set the horizontal offset.
		 * @param x ofset in percent of 10 times the screen width
		 */
		public synchronized void setHorizontalPos (double x)
		{	HOffset=(int)(x*10*W);
			repaint();
		}
		
		/**
		 * Delete all items from the panel.
		 */
		public synchronized void clear ()
		{	Selected.removeAllElements();
			V.removeAllElements();
			Top=0;
		}
		
		/**
		 * Make sure, the last elment displays.
		 */
		public synchronized void showLast ()
		{	ShowLast=true;
		}
		
		// Mouse routines:
		
		Vector VAL=new Vector(); // Vector of action listener
		MyVector Selected=new MyVector(); // currently selected items
		
		/**
		 * Determine if line sel is selected
		 * @param sel
		 * @return selected or not
		 */
		public synchronized boolean isSelected (int sel)
		{	Enumeration e=Selected.elements();
			while (e.hasMoreElements())
			{	int n=((Integer)e.nextElement()).intValue();
				if (n==sel) return true;
			}
			return false;
		}
		
		/**
		 * Toggle the line sel to be selected or not.
		 * @param sel
		 */
		public synchronized void toggleSelect (int sel)
		{	Enumeration e=Selected.elements();
			while (e.hasMoreElements())
			{	Integer i=(Integer)e.nextElement();
				if (i.intValue()==sel)
				{	Selected.removeElement(i);
					return;
				}
			}
			Selected.addElement(new Integer(sel));
		}
		
		/**
		 * Expand the selection to include sel and all
		 * elements in between.
		 * @param sel
		 */
		public synchronized void expandSelect (int sel)
		{	// compute maximal selected index below sel.
			int max=-1;
			Enumeration e=Selected.elements();
			while (e.hasMoreElements())
			{	int i=((Integer)e.nextElement()).intValue();
				if (i>max && i<sel) max=i;
			}
			if (max>=0)
			{	for (int i=max+1; i<=sel; i++) select(i);
				return;
			}
			int min=V.size();
			e=Selected.elements();
			while (e.hasMoreElements())
			{	int i=((Integer)e.nextElement()).intValue();
				if (i<min && i>sel) min=i;
			}
			if (min<V.size())
			{	for (int i=sel; i<=min; i++) select(i);
			}
		}
		
		/**
		 * Selecte an item by number sel.
		 * @param sel 
		 */
		public synchronized void select (int sel)
		{	if (!isSelected(sel))
				Selected.addElement(new Integer(sel));
		}

		
		/**
		 * Add an action listener for all actions of this panel.
		 * @param al
		 */
		public void addActionListener (ActionListener al)
		{	VAL.addElement(al);
		}
		
		/**
		 * Remove an action listener
		 * @param al
		 */
		public void removeActionListener (ActionListener al)
		{	VAL.removeElement(al);
		}
		
		/**
		 * React on mouse clicks (single or double, or right click).
		 * single: select the item (according multiple mode) cause change action.
		 * double: select only this item and cause action. 
		 * right: popup menu, if possible.
		 * In any case, report the result to the action listeners.
		 * @param e
		 */
		public void clicked (MouseEvent e)
		{	int n=e.getY()/(Leading+Height);
			if (e.isMetaDown() && RightMouseClick)
			{	Enumeration en=VAL.elements();
				while (en.hasMoreElements())
				{	((ActionListener)(en.nextElement())).actionPerformed(
						new ListerMouseEvent(LD,Name,e));
				}
			}
			else
			{	if (Top+n>=V.size()) return;
				int sel=n+Top;
				if (e.getClickCount()>=2)
				{	if (!MultipleSelection) Selected.removeAllElements();
					select(sel);
				}
				else if (MultipleSelection &&
						(e.isControlDown() || EasyMultipleSelection || e.isShiftDown()) 
						)
				{	if (e.isControlDown() || EasyMultipleSelection)
						toggleSelect(sel);
					else if (e.isShiftDown())
						expandSelect(sel);
				}
				else
				{	Selected.removeAllElements();
					Selected.addElement(new Integer(sel));
				}
				Graphics g=getGraphics();
				paint(g);
				g.dispose();
				if (e.getClickCount()>=2 || ReportSingleClick)
				{	Enumeration en=VAL.elements();
					while (en.hasMoreElements())
					{	((ActionListener)(en.nextElement())).actionPerformed(
							new ListerMouseEvent(LD,Name,e));
					}
				}
			}
		}

		public Dimension getPreferredSize ()
		{	return new Dimension(200,300);
		}

		public synchronized Element getElementAt (int n)
		{	return (Element)V.elementAt(n);
		}

		public synchronized void save (PrintWriter o)
		{	Enumeration e=V.elements();
			while (e.hasMoreElements())
			{	Element el=(Element)e.nextElement();
				o.println(el.getElementString());
			}
		}

		public void setListingBackground (Color c)
		{	ListingBackground=c;
		}
}

