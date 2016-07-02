package jagoclient.partner;

import jagoclient.Global;
import jagoclient.dialogs.Question;

class ClosePartnerQuestion extends Question
{	public ClosePartnerQuestion (PartnerFrame g)
	{	super(g,Global.resourceString("This_will_close_your_connection_"),Global.resourceString("Close"),g,true);
		setVisible(true);
	}
	public void tell (Question q, Object o, boolean f)
	{	q.setVisible(false); q.dispose();
	    if (f) ((PartnerFrame)o).doclose();
	}
}
