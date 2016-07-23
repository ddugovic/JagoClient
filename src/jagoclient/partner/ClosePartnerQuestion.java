package jagoclient.partner;

import jagoclient.Global;

import rene.dialogs.Question;

class ClosePartnerQuestion extends Question<PartnerFrame>
{	public ClosePartnerQuestion (PartnerFrame g)
	{	super(g,Global.resourceString("This_will_close_your_connection_"),Global.resourceString("Close"),true);
	}
	@Override
	public void tell (Question q, boolean answer)
	{	if (answer) F.doclose();
	}
}
