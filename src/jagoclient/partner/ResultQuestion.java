package jagoclient.partner;

import jagoclient.Global;

import rene.dialogs.Question;

/**
Question to accept a result or decline it.
*/

public class ResultQuestion extends Question<PartnerFrame>
{	int B,W;
	/**
	@param b,w Black and White results
	*/
	public ResultQuestion (PartnerFrame g, String m, int b, int w)
	{	super(g,m,Global.resourceString("Result"),true); B=b; W=w;
		setVisible(true);
	}
	public void tell (Question q, boolean f)
	{	if (f) F.doresult(B,W);
		else F.declineresult();
	}
	public boolean close ()
	{	F.declineresult();
		return true;
	}	
}

