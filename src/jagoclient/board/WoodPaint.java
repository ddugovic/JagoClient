package jagoclient.board;

import jagoclient.Global;

import java.awt.Color;
import java.awt.Frame;

/**
This is a thread to create an empty board.
@see jagoclient.board.EmptyPaint
*/

public class WoodPaint implements Runnable
{	int W,H,Ox,Oy,D;
	Color C;
	Frame F;
	boolean Shadows;
	public WoodPaint (Frame f)
	{	F=f;
	}
	public void run ()
	{	EmptyPaint.createwood(F,
			Global.getParameter("sboardwidth",0),
			Global.getParameter("sboardheight",0),
			Global.getColor("boardcolor",170,120,70,Color.RED),
			Global.getParameter("shadows",true),
			Global.getParameter("sboardox",5),
			Global.getParameter("sboardoy",5),
			Global.getParameter("sboardd",10));
	}
}

