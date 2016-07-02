/*
 * Created on 15.01.2006
 *
 */
package rene.lister;

import java.awt.event.*;

public class ListerMouseEvent
	extends ActionEvent
{	static int ID=0;
	MouseEvent E;
	
	public ListerMouseEvent (Object o, String name, MouseEvent e)
	{	super(o,ID++,name);
		E=e;
	}
	
	public MouseEvent getEvent ()
	{	return E;
	}
	
	public String getName ()
	{	return E.paramString();
	}

	public boolean rightMouse ()
	{	return E.isMetaDown();
	}
	
	public int clickCount ()
	{	return E.getClickCount();
	}
}
