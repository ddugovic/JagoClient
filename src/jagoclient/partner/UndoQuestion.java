package jagoclient.partner;

import jagoclient.Global;

import rene.dialogs.Question;

/**
Question to undo a move, or decline the undo request.
*/

public class UndoQuestion extends Question<PartnerFrame>
{
	public UndoQuestion (PartnerFrame g)
	{	super(g,Global.resourceString("Partner_request_undo__Accept_"),Global.resourceString("Undo"),true);
		setVisible(true);
	}
	public void tell (Question q, boolean f)
	{	if (f) F.doundo();
		else F.declineundo();
	}
	public boolean close ()
	{	F.declineundo();
		return true;
	}
}

