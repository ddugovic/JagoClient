package jagoclient.partner;

import jagoclient.Global;

import rene.dialogs.Question;

/**
Question to end the game, or decline that.
*/

public class EndGameQuestion extends Question<PartnerFrame>
{
	public EndGameQuestion (PartnerFrame g)
	{	super(g,Global.resourceString("End_the_game_"),Global.resourceString("End"),true);
		setVisible(true);
	}
	public void tell (Question q, boolean f)
	{	if (f) F.doendgame();
		else F.declineendgame();
	}
	public boolean close ()
	{	F.declineendgame();
		return true;
	}
}

