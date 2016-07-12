package jagoclient.gui;

import jagoclient.Global;

import java.awt.TextArea;

/**
 * A text area that takes care of the maximal length imposed by Windows and
 * other OSs. This should be replaced by jagoclient.viewer.Viewer
 * <p>
 * The class works much like TextArea, but takes care of its length.
 * 
 * @see jagoclient.viewer.Viewer
 * @deprecated
 */
public class MyTextArea extends TextArea
{
	public MyTextArea ()
	{
		setFont(Global.Monospaced);
	}

	public MyTextArea (String s, int x, int y, int f)
	{
		super(s, x, y, f);
		setFont(Global.Monospaced);
		setText(s);
	}
}
