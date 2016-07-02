package jagoclient.partner;

import jagoclient.Global;
import jagoclient.dialogs.Question;

/**
Question to undo a move, or decline the undo request.
*/

public class UndoQuestion extends Question
{	PartnerFrame G;
	public UndoQuestion (PartnerFrame g)
	{	super(g,Global.resourceString("Partner_request_undo__Accept_"),Global.resourceString("Undo"),g,true);
		G=g;
		setVisible(true);
	}
	public void tell (Question q, Object o, boolean f)
	{	q.setVisible(false); q.dispose();
	    if (f) G.doundo();
		else G.declineundo();
	}
	public boolean close ()
	{	G.declineundo();
		return true;
	}
}

