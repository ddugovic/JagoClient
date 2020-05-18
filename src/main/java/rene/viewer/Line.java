package rene.viewer;

import java.awt.*;

public class Line
{   TextDisplay TD;
	boolean Chosen;
	int Pos,Posend;
	int Block; // block state
	static final int NONE=0,START=1,END=2,FULL=4; // block states (logically or'ed)
	Color C,IC; // Color of line and high key color of the line
	char a[]; // Contains the characters of this line
	
	public Line (String s, TextDisplay td)
	{	this(s,td,Color.black);
	}
	
	/**
	 * Generate a line containing s in the textdisplay td.
	 * Display color is c.
	 * @param s
	 * @param td
	 * @param c
	 */
	public Line (String s, TextDisplay td, Color c)
    {	TD=td;
    	C=c;
    	// Create a color that is very bright, but resembles the line color
    	IC=new Color(C.getRed()/4+192,C.getGreen()/4+192,C.getBlue()/4+192);
    	Block=NONE;
    	a=s.toCharArray();
    }
    
    public void expandTabs (int tabwidth)
    {	int pos=0;
    	for (int i=0; i<a.length; i++)
    	{	pos++;
    		if (a[i]=='\t') pos=(pos/tabwidth+1)*tabwidth;
    	}
    	char b[]=new char[pos];
    	pos=0;
    	for (int i=0; i<a.length; i++)
    	{	if (a[i]=='\t')
    		{	int newpos=((pos+1)/tabwidth+1)*tabwidth;
    			for (int k=pos; k<newpos; k++) b[k]=' ';
    			pos=newpos;
    		}
    		else b[pos++]=a[i];
    	}
    	a=b;	
    }
    int length ()
    {	return a.length;
    }
    int getpos (int x, int offset)
    {	int l[]=TD.getwidth(a);
    	int h=offset-TD.Offset*TD.FM.charWidth(' ');
    	if (x<h) return 0;
    	int i=0;
    	while (x>h && i<a.length)
    	{	h+=l[i];
    		i++;
    	}
    	return i;
    }
    
    /**
     * Draw a line.
     * If the line is in a block, draw it white on black or on dark gray,
     * depending on the focus.
     * Drawing in blocks does not use antialias.
	 * @param g
	 * @param x
	 * @param y
	 */
	public void draw (Graphics g, int x, int y)
    {	int i1=0,i2,p=0;
    	x-=TD.Offset*TD.FM.charWidth(' ');
    	if (Chosen) // Complete line is chosen (in Lister)
    	{	// To see, if the display has the focus:
    		if (TD.hasFocus()) g.setColor(Color.darkGray);
    		else g.setColor(Color.gray);
    		g.fillRect(0,y-TD.Ascent,TD.getSize().width,TD.Height);
    		g.setColor(IC); // draw in light color
			TD.antialias(false);
    		g.drawChars(a,0,a.length,x,y);
			TD.antialias(true);
    	}
    	else if ((Block&FULL)!=0) // Line in full block (in Viewer)
    	{	g.setColor(Color.darkGray);
    		g.fillRect(x,y-TD.Ascent,TD.FM.charsWidth(a,0,a.length),TD.Height);
    		g.setColor(Color.white);
    		TD.antialias(false);
    		g.drawChars(a,0,a.length,x,y);
    		TD.antialias(true);
    	}
    	else if ((Block&START)!=0)
    	{	if (Pos>0) // Draw text before block
    		{	g.setColor(C);
    			g.drawChars(a,0,Pos,x,y);
    			x+=TD.FM.charsWidth(a,0,Pos);
    		}
    		if ((Block&END)!=0) // draw text in block
    		{	if (Posend>Pos)
    			{	int h=TD.FM.charsWidth(a,Pos,Posend-Pos);
	    			g.setColor(Color.darkGray);
    				g.fillRect(x,y-TD.Ascent,h,TD.Height);
    				g.setColor(Color.white);
					TD.antialias(false);
	    			g.drawChars(a,Pos,Posend-Pos,x,y);
					TD.antialias(true);
    				g.setColor(C);
    				x+=h;
	    			if (a.length>Posend)
    				{	g.drawChars(a,Posend,a.length-Posend,x,y);
    				}
    			}
    			else g.drawChars(a,Pos,a.length-Pos,x,y);
    		}
    		else // draw the rest of the line in block 
    		{	int h=TD.FM.charsWidth(a,Pos,a.length-Pos);
	    		g.setColor(Color.darkGray);
    			g.fillRect(x,y-TD.Ascent,h,TD.Height);
    			g.setColor(Color.white);
				TD.antialias(false);
    			g.drawChars(a,Pos,a.length-Pos,x,y);	
				TD.antialias(true);
    		}
    	}
    	else if ((Block&END)!=0)
    	{	int h=TD.FM.charsWidth(a,0,Posend);
    		g.setColor(Color.darkGray);
   			g.fillRect(x,y-TD.Ascent,h,TD.Height);
   			g.setColor(Color.white);
			TD.antialias(false);
    		g.drawChars(a,0,Posend,x,y);	
			TD.antialias(true);
    		g.setColor(C);
    		x+=h;
    		if (a.length>Posend)
    		{	g.drawChars(a,Posend,a.length-Posend,x,y);
    		}
    	}
    	else
    	{	g.setColor(C);
    		g.drawChars(a,0,a.length,x,y);
    	}
    }
    void append (String s)
    {	a=(new String(a)+s).toCharArray();
    }
    void chosen (boolean f)
    {	Chosen=f;
    }
    public boolean chosen () { return Chosen; }
    void block (int pos, int mode)
    {	switch (mode)
    	{	case NONE : Block=NONE; break;
    		case FULL : Block=FULL; break;
    		case START :
    			Block|=START; Pos=pos; break;
    		case END :
    			Block|=END; Posend=pos; break;
    	}
    }
    String getblock ()
    {	if (Block==FULL)
    	{	return new String(a,0,a.length); 
    	}
    	else if ((Block&START)!=0)
    	{	if ((Block&END)!=0)
    		{	return new String(a,Pos,Posend-Pos);
    		}
    		else return new String(a,Pos,a.length-Pos);
    	}
    	else if ((Block&END)!=0) return new String(a,0,Posend);
    	else return "";
    }
}
