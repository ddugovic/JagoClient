package rene.dialogs;

import java.awt.*;
import java.awt.event.*;

import rene.gui.*;

/**
 * A scroll bar together with a numeric input.
 * @author Rene Grothmann
 * 
 */
class ColorScrollbar extends Panel 
    implements AdjustmentListener, DoActionListener, FocusListener
{	public int Value;
	ColorEditor CE;
	Scrollbar SB;
	IntField L;
	int Max,SL;
	
	/**
	 * Initialize with color editor, and string for prompt. 
	 * The maximal value for colors is 255.
	 * @param ce
	 * @param s
	 * @param value
	 * @param max
	 */
	public ColorScrollbar (ColorEditor ce, String s, int value, int max)
	{	CE=ce;
		setLayout(new GridLayout(1,0));
		Value=value;
		Max=max;
		SL=max/10;
		
		Panel p=new MyPanel();
		p.setLayout(new GridLayout(1,0));
		p.add(new MyLabel(s));
		p.add(L=new IntField(this,"L",Value,4));
		add(p);
		add(SB=new Scrollbar(Scrollbar.HORIZONTAL,value,SL,0,Max+SL));
		SB.addAdjustmentListener(this);
		L.addFocusListener(this);
	}
	
	/**
	 * Check for return in the text field.
	 */
	public void doAction (String o)
	{	if ("L".equals(o))
		{	Value=L.value(0,Max);
			SB.setValue(Value);
			CE.setcolor(this);
		}
	}
	
	public void itemAction (String o, boolean flag) {}
	
	public void adjustmentValueChanged (AdjustmentEvent e)
	{	Value=SB.getValue();
		L.set(Value);
		SB.setValue(Value);
		CE.setcolor(this);
	}
	
	public int value () 
	{	return L.value(0,Max); 
	}
	
	public void set (int v)
	{	L.set(v);
		SB.setValue(v);
	}

	public void focusGained(FocusEvent arg0)
	{
	}

	/**
	 * if the text field lost focus, set the value.
	 */
	public void focusLost(FocusEvent arg0)
	{	doAction("L");
	}
}

/**
 * Displays the colors (color wheel, selected and old color)
 * @author Rene Grothmann
 */
class ColorPanel extends MyPanel
	implements MouseListener, MouseMotionListener
{	Color C,OldC;
	Color FixedC[],UserC[];
	int EditUser=-1;
	int W,H,D;
	static double v1,v2,v3,w1,w2,w3;
	static
	{	double x=Math.sqrt(2);
		v1=-1/x; v2=1/x; v3=0;
		x=Math.sqrt(6);
		w1=1/x; w2=1/x; w3=-2/x;
	};
	ColorEditor CE;
	
	/**
	 * Initialize with color editor and start color.
	 * @param c
	 * @param ce
	 */
	public ColorPanel (Color c, ColorEditor ce, Color fixedc[], Color userc[])
	{	C=OldC=c;
		CE=ce;
		FixedC=fixedc; UserC=userc;
		W=400; D=20; 
		int k=0;
		if (FixedC!=null) k+=(FixedC.length-1)/16+1;
		if (UserC!=null) k+=(UserC.length-1)/16+1;
		H=3*D+D/2+k*(D+D/2);
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	/**
	 * Necessary to get the right size
	 */
	public Dimension getPreferredSize ()
	{	return new Dimension(W,H);	
	}
	public Dimension getMinimumSize ()
	{	return getPreferredSize();	
	}
	
	/**
	 * Set the color from external, e.g., from the color sliders.
	 * @param c
	 */
	public void setNewColor (Color c)
	{	C=c;
		repaint();
	}
	
	/**
	 * Set the color via hue, brightness and saturation from external.
	 * @param a
	 * @param br
	 * @param sat
	 */
	public void setNewColor (double a, double br, double sat)
	{	double c=Math.cos(a),s=Math.sin(a);
		double x1=v1*c+w1*s,x2=v2*c+w2*s,x3=v3*c+w3*s;
		double max=Math.max(Math.max(x1,x2),x3);
		double min=Math.min(Math.min(x1,x2),x3);
		x1=-1+2*(x1-min)/(max-min);
		x2=-1+2*(x2-min)/(max-min);
		x3=-1+2*(x3-min)/(max-min);
		double f=sat*Math.min(1-br,br);
		C=new Color((float)(br+x1*f),(float)(br+x2*f),(float)(br+x3*f));
		if (EditUser>0) UserC[EditUser]=C;
		repaint();
	}
	
	/**
	 * Compute a fully saturated color with the specified hue.
	 * @param a
	 * @return
	 */
	public static Color getColor (double a)
	{	double c=Math.cos(a),s=Math.sin(a);
		double x1=v1*c+w1*s,x2=v2*c+w2*s,x3=v3*c+w3*s;
		double max=Math.max(Math.max(x1,x2),x3);
		double min=Math.min(Math.min(x1,x2),x3);
		x1=-1+2*(x1-min)/(max-min);
		x2=-1+2*(x2-min)/(max-min);
		x3=-1+2*(x3-min)/(max-min);
		return new Color((float)(0.5+x1*0.5),(float)(0.5+x2*0.5),(float)(0.5+x3*0.5));
	}
	
	Image I=null,IH=null;
	
	/**
	 * Repaint the colors, and the images, if necessary.
	 */
	public void paint (Graphics g)
	{	// Check, if image buffer is not up to date
		if (I==null || I.getWidth(this)!=W || I.getHeight(this)!=H)
		{	I=createImage(W,H);
		}
		Graphics ig=I.getGraphics();
		// clear rectangle
		ig.clearRect(0,0,W,H);
		// draw old and new colors
		ig.setColor(OldC);
		ig.fillRect(D/2,D/2,W/2-D,D);
		ig.setColor(C);
		ig.fillRect(W/2+D/2,D/2,W/2-D,D);
		// check if color wheel is up to date
		if (IH==null || IH.getWidth(this)!=W-D)
		{	IH=createImage(W-D,20);
			Graphics igh=IH.getGraphics();
			igh.clearRect(0,0,W-D,D);
			for (int i=0; i<W-D; i++)
			{	igh.setColor(getColor(2*Math.PI*((double)i)/(W-D)));
				igh.drawLine(i,0,i,D-1);
			}
		}
		// draw the color wheel
		ig.drawImage(IH,D/2,2*D,this);
		// draw the current hue
		int k=D/2+(int)(getHue(C)*(W-D));
		ig.setColor(Color.black);
		ig.drawLine(k,2*D,k,3*D-1);
		// draw the fixed colors
		int yr=3*D+D/2;
		if (FixedC!=null)
		{	int kc=0;
			for (int i=0; i<FixedC.length; i++)
			{	Color col=FixedC[i];
				if (col==null) col=Color.gray;
				ig.setColor(getBackground());
				ig.fill3DRect(D/2+kc*(D+D/2),yr,D,D,true);
				ig.setColor(col);
				ig.fillRect(D/2+kc*(D+D/2)+4,yr+4,D-8,D-8);
				kc++;
				if (kc==16) 
				{	kc=0; yr+=D+D/2;
				}
			}
			if (kc>0) yr+=D+D/2;
		}
		if (UserC!=null)
		{	int kc=0;
			for (int i=0; i<UserC.length; i++)
			{	Color col=UserC[i];
				if (col==null) col=Color.gray;
				if (EditUser==i) 
				{	ig.setColor(getBackground());
					ig.fill3DRect(D/2+kc*(D+D/2),yr,D,D,true);
					ig.setColor(col);
					ig.fillRect(D/2+kc*(D+D/2)+4,yr+4,D-8,D-8);
				}
				else 
				{	ig.setColor(col);
					ig.fillRect(D/2+kc*(D+D/2),yr,D,D);
				}
				kc++;
				if (kc==16) 
				{	kc=0; yr+=D+D/2;
				}
			}
		}
		// draw the image buffer
		g.drawImage(I,0,0,this);
	}
	
	/**
	 * Overrides update
	 */
	public void update (Graphics g)
	{	paint(g);
	}

	/**
	 * User dragged the slider in the color wheel.
	 */
	public void mouseDragged (MouseEvent e)
	{	double x=e.getX(),y=e.getY();
		if (y<2*D || y>3*D || x<=D/2 || x>=W-D/2) return;
		C=getColor(2*Math.PI*(x-D/2)/(W-D));
		CE.setcolor(C);
		if (EditUser>=0) UserC[EditUser]=C;
		repaint();
	}

	public void mouseMoved (MouseEvent arg0)
	{
	}

	/**
	 * Compute the saturation of a color.
	 * @param C
	 * @return
	 */
	public static double getSaturation (Color C)
	{	double r=C.getRed()/255.0,g=C.getGreen()/255.0,b=C.getBlue()/255.0;
		double m=(r+g+b)/3;
		return Math.sqrt((r-m)*(r-m)+(g-m)*(g-m)+(b-m)*(b-m))/Math.sqrt(2.0/3.0);
	}
	
	/**
	 * Compute the hue of a color.
	 * @param C
	 * @return
	 */
	public static double getHue (Color C)
	{	double r=C.getRed()/255.0,g=C.getGreen()/255.0,b=C.getBlue()/255.0;
		double m=(r+g+b)/3;
		double c=((r-m)*v1+(g-m)*v2+(b-m)*v3);
		double s=((r-m)*w1+(g-m)*w2+(b-m)*w3);
		double a=Math.atan2(s,c)/(2*Math.PI);
		if (a<0) a+=1;
		if (a>1) a-=1;
		return a;
	}
	
	/**
	 * Compute the brightness of a color.
	 * @param C
	 * @return
	 */
	public static double getBrightness (Color C)
	{	double r=C.getRed()/255.0,g=C.getGreen()/255.0,b=C.getBlue()/255.0;
		return (Math.max(r,Math.max(g,b))+Math.min(r,Math.min(g,b)))/2;
	}
	
	/**
	 * Selected color.
	 * @return
	 */
	public Color getColor ()
	{	return C;
	}

	/**
	 * Check if one of the user or fixed colors was clicked.
	 */
	public void mouseClicked (MouseEvent e)
	{	int x=e.getX(),y=e.getY();
		int yr=3*D+D/2;
		if (FixedC!=null)
		{	int kc=0;
			for (int i=0; i<FixedC.length; i++)
			{	Color col=FixedC[i];
				if (col==null) col=Color.gray;
				if (y>yr && y<yr+D && x>D/2+kc*(D+D/2) && x<D/2+kc*(D+D/2)+D)
				{	setNewColor(col); CE.doAction("OK"); EditUser=-1;
					return;
				}
				kc++;
				if (kc==16) 
				{	kc=0; yr+=D+D/2;
				}
			}
			if (kc>0) yr+=D+D/2;
		}
		if (UserC!=null)
		{	int kc=0;
			for (int i=0; i<UserC.length; i++)
			{	Color col=UserC[i];
				if (col==null) col=Color.gray;
				if (y>yr && y<yr+D && x>D/2+kc*(D+D/2) && x<D/2+kc*(D+D/2)+D)
				{	setNewColor(col);
					if (EditUser==i)
					{	CE.doAction("OK");  EditUser=-1;
					}
					else
					{	EditUser=i; repaint();
					}
					return;
				}
				kc++;
				if (kc==16) 
				{	kc=0; yr+=D+D/2;
				}
			}
			if (kc>0) yr+=D+D/2;
		}
		EditUser=-1; repaint(); return;
	}

	public void mouseEntered(MouseEvent arg0)
	{
	}

	public void mouseExited(MouseEvent arg0)
	{
	}

	public void mousePressed (MouseEvent e)
	{	
	}

	public void mouseReleased(MouseEvent arg0)
	{
	}
	
	/**
	 * Return 32 nice user colors.
	 * @return
	 */
	static public Color[] getSomeColors ()
	{	Color c[]=new Color[32];
		for (int i=0; i<32; i++)
		{	c[i]=getColor(2*Math.PI/32*i);
		}
		return c;
	}
}

/**
A dialog to edit a color. The result is stored in the Global
parameters under the specified name string.
@see rene.gui.Global
*/
public class ColorEditor extends CloseDialog
{	ColorScrollbar Red,Green,Blue,Hue,Saturation,Brightness;
	Label RedLabel,GreenLabel,BlueLabel;
	ColorPanel CP;
	String Name;
	Color Cret;
	
	/**
	 * Initialize the dialog.
	 * The color is read from the global settings of
	 * parameter s (color name) with default c.
	 * @param F Calling Frame
	 * @param s Color name in Global
	 * @param c Default color
	 */
	public ColorEditor (Frame F, String s, Color c, Color fixedc[], Color userc[])
	{	super(F,Global.name("coloreditor.title"),true);
		
		Name=s;
		Color C=Global.getParameter(s,c);
		if (C==null) C=new Color(255,255,255);
		
		CP=new ColorPanel(C,this,fixedc,userc);
		add("North",new Panel3D(CP));
		
		Panel pc=new MyPanel();
		pc.setLayout(new GridLayout(0,1));
		Panel p=new MyPanel();
		p.setLayout(new GridLayout(0,1));
		p.add(Hue=new ColorScrollbar(this,
			Global.name("coloreditor.hue"),
			(int)(ColorPanel.getHue(C)*360),360));
		p.add(Saturation=new ColorScrollbar(this,
			Global.name("coloreditor.saturation"),
			(int)(ColorPanel.getSaturation(C)*100),100));
		p.add(Brightness=new ColorScrollbar(this,
			Global.name("coloreditor.brightness"),
			(int)(ColorPanel.getBrightness(C)*100),100));
		pc.add(new Panel3D(p));
		p=new MyPanel();
		p.setLayout(new GridLayout(0,1));
		p.add(Red=new ColorScrollbar(this,
			Global.name("coloreditor.red"),C.getRed(),255));
		p.add(Green=new ColorScrollbar(this,
			Global.name("coloreditor.green"),C.getGreen(),255));
		p.add(Blue=new ColorScrollbar(this,
			Global.name("coloreditor.blue"),C.getBlue(),255));
		pc.add(new Panel3D(p));
		add("Center",new Panel3D(pc));
		
		Panel pb=new MyPanel();
		pb.add(new ButtonAction(this,Global.name("OK"),"OK"));
		pb.add(new ButtonAction(this,Global.name("abort"),"Close"));
		add("South",new Panel3D(pb));
		
		pack();
	}
	public ColorEditor (Frame F, String s, Color c)
	{	this(F,s,c,null,null);
	}
	
	/**
	 * On OK, save the color to the global parameter s.
	 */
	public void doAction (String o)
	{	if ("OK".equals(o))
		{	Global.setParameter(Name,CP.getColor());
			Aborted=false;
			doclose();
		}
		else super.doAction(o);
	}
	
	public void doclose ()
	{	Cret=CP.getColor();
		super.doclose();
	}
	
	/**
	 * Called from the scroll bars.
	 * @param cs
	 */
	public void setcolor (ColorScrollbar cs)
	{	if (cs==Red || cs==Green || cs==Blue)
		{	Color C=new Color(Red.value(),Green.value(),Blue.value());
			CP.setNewColor(C);
			C=CP.getColor();
			Hue.set((int)(ColorPanel.getHue(C)*360));
			Brightness.set((int)(ColorPanel.getBrightness(C)*100));
			Saturation.set((int)(ColorPanel.getSaturation(C)*100));
		}
		else
		{	CP.setNewColor(Hue.value()/180.0*Math.PI,
				Brightness.value()/100.0,Saturation.value()/100.0);
			Color C=CP.getColor();
			Red.set(C.getRed()); Green.set(C.getGreen()); Blue.set(C.getBlue());
		}
	}
	
	/**
	 * Called from the color panel, if the user scrolled the hue.
	 * @param C
	 */
	public void setcolor (Color C)
	{	Red.set(C.getRed()); Green.set(C.getGreen()); Blue.set(C.getBlue());
		Hue.set((int)(ColorPanel.getHue(C)*360));
		Brightness.set((int)(ColorPanel.getBrightness(C)*100));
		Saturation.set((int)(ColorPanel.getSaturation(C)*100));
	}
	
	static CloseFrame f;
	static Color cf=Color.red;
	static Color FixedC[]={Color.white,Color.black,Color.red,Color.blue,Color.green};
	static Color UserC[]=ColorPanel.getSomeColors();
	
	/**
	 * Main program for tests.
	 * Shows the dialog, when it is closed.
	 * @param args
	 */
	public static void main (String args[])
	{	f=new CloseFrame("Color Test")
		{	public void paint (Graphics g)
			{	Dimension d=getSize();
				g.setColor(cf);
				g.fillRect(0,0,d.width,d.height);
			}
		};
		f.addMouseListener(new MouseAdapter ()
			{	public void mouseClicked (MouseEvent e)
				{	ColorEditor ce=new ColorEditor(f,"",cf,FixedC,UserC);
					ce.center(f);
					ce.setVisible(true);
					cf=ce.getColor();
					f.repaint();
				}
			});
		f.setSize(500,500);
		f.center();
		f.setVisible(true);
	}
	
	public Color getColor ()
	{	return Cret;	
	}
	
	static public Color[] getSomeColors ()
	{	return ColorPanel.getSomeColors();
	}
}
