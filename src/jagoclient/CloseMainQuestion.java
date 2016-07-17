package jagoclient;

import jagoclient.gui.CloseFrame;

import rene.dialogs.Question;

public class CloseMainQuestion extends Question
{	MainFrame G;
	public CloseMainQuestion (MainFrame g)
	{	super((CloseFrame)g,Global.resourceString("End_Application_"),
			Global.resourceString("Exit"),(CloseFrame)g,true);
		G=g;
		setVisible(true);
	}
	public void tell (Question q, Object o, boolean f)
	{	q.setVisible(false); q.dispose();
	}
}
