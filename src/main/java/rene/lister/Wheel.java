/*
 * Created on 14.01.2006
 *
 */
package rene.lister;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

class Wheel
implements MouseWheelListener
{	WheelListener V;

	public Wheel (WheelListener v)
	{	V=v;
	}
	
	public void mouseWheelMoved (MouseWheelEvent e)
	{	if (e.getScrollType()==MouseWheelEvent.WHEEL_BLOCK_SCROLL)
		{	if (e.getWheelRotation()>0)
				V.pageUp();
			else
				V.pageDown();
		}
		else
		{	int n=e.getScrollAmount();
			if (e.getWheelRotation()>0) V.up(n);
			else V.down(n);
		}
	}
}