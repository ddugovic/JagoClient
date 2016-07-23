package jagoclient;

import rene.dialogs.Question;
import rene.gui.CloseFrame;

public class CloseMainQuestion extends Question
{
	public CloseMainQuestion (CloseFrame g)
	{	super(g,Global.resourceString("End_Application_"),
			Global.resourceString("Exit"),true);
	}
}
