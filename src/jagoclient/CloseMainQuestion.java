package jagoclient;

import rene.dialogs.Question;
import rene.gui.CloseFrame;

public class CloseMainQuestion extends Question
{
	public CloseMainQuestion (CloseFrame g)
	{	super(g,Global.resourceString("End_Application_"),
			Global.resourceString("Exit"), g,true);
	}
	@Override
	public void tell (Question q, Object o, boolean f)
	{	q.setVisible(false); q.dispose();
	}
}
