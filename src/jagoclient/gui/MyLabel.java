package jagoclient.gui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * A label in a specified font.
 */

public class MyLabel extends JLabel
{
	public MyLabel (String s)
	{
		super(s);
		// setFont(Global.SansSerif);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

}
