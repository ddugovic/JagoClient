package jagoclient.igs;

import jagoclient.Global;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.GrayTextField;
import jagoclient.gui.MyLabel;
import jagoclient.gui.MyPanel;
import jagoclient.gui.Panel3D;

import javax.swing.JPanel;
import javax.swing.JTextField;

import rene.gui.CloseDialog;

/**
 * This dialog is opened by IgsGoFrame, when the "Send" button is pressed.
 */

public class SendQuestion extends CloseDialog
{
	IgsGoFrame F;
	JTextField T;
	Distributor Dis;

	public SendQuestion (IgsGoFrame f, Distributor dis)
	{
		super(f, Global.resourceString("Send"), false);
		F = f;
		add("North", new MyLabel(Global.resourceString("Message_")));
		add("Center", T = new GrayTextField(40));
		JPanel p = new MyPanel();
		p.add(new ButtonAction(this, Global.resourceString("Kibitz")));
		if (dis instanceof PlayDistributor)
			p.add(new ButtonAction(this, Global.resourceString("Say")));
		p.add(new ButtonAction(this, Global.resourceString("Cancel")));
		add("South", new Panel3D(p));
		Global.setpacked(this, "send", 200, 150);
		validate();
		Global.setpacked(this, "sendquestion", 300, 150, f);
		setVisible(true);
		Dis = dis;
	}

	@Override
	public void doAction (String o)
	{
		Global.notewindow(this, "send");
		if (Global.resourceString("Kibitz").equals(o))
		{
			if ( !T.getText().equals(""))
			{
				Dis.out("kibitz " + Dis.game() + " " + T.getText());
				F.addComment("Kibitz: " + T.getText());
			}
			setVisible(false);
			dispose();
		}
		else if (Global.resourceString("Say").equals(o))
		{
			if ( !T.getText().equals(""))
			{
				Dis.out("say " + T.getText());
				F.addComment("Say: " + T.getText());
			}
			setVisible(false);
			dispose();
		}
		else if (Global.resourceString("Cancel").equals(o))
		{
			setVisible(false);
			dispose();
		}
		else super.doAction(o);
	}
}
