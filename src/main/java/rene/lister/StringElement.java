/*
 * Created on 14.01.2006
 *
 */
package rene.lister;

import java.awt.Color;

public class StringElement
	implements Element
{	public String S;
	public Color C;
	
	public StringElement (String s, Color c)
	{	S=s; C=c;
	}
	
	public StringElement (String s)
	{	this(s,null);
	}
	
	public String getElementString ()
	{	return S;
	}
	
	public String getElementString (int state)
	{	return S;
	}
	
	public Color getElementColor ()
	{	return C;
	}
}
