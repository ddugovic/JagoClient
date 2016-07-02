package jagoclient.gui;

import javax.swing.JTextField;

/**
 * A TextField with a background and font as specified in the Global class.
 */

public class GrayTextField extends JTextField
{
	public GrayTextField (String s)
	{
		super(s, 25);
		// setFont(Global.SansSerif);
	}

	public GrayTextField ()
	{
		super(25);
		// setFont(Global.SansSerif);
	}

	public GrayTextField (int n)
	{
		super(n);
		// setFont(Global.SansSerif);
	}
}
