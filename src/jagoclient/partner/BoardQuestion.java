package jagoclient.partner;

import jagoclient.Global;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.CloseDialog;
import jagoclient.gui.FormTextField;
import jagoclient.gui.MyLabel;
import jagoclient.gui.MyPanel;
import jagoclient.gui.Panel3D;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Question to accept or decline a game with received parameters.
 */

public class BoardQuestion extends CloseDialog
{
	int BoardSize, Handicap, TotalTime, ExtraTime, ExtraMoves;
	String ColorChoice;
	PartnerFrame PF;

	public BoardQuestion (PartnerFrame pf, int s, String c, int h, int tt,
		int et, int em)
	{
		super(pf, Global.resourceString("Game_Setup"), true);
		PF = pf;
		BoardSize = s;
		Handicap = h;
		TotalTime = tt;
		ExtraTime = et;
		ExtraMoves = em;
		ColorChoice = c;
		JPanel pa = new MyPanel();
		add("Center", new Panel3D(pa));
		JTextField t;
		pa.setLayout(new GridLayout(0, 2));
		pa.add(new MyLabel(Global.resourceString("Board_size")));
		pa.add(t = new FormTextField("" + s));
		t.setEditable(false);
		pa.add(new MyLabel(Global.resourceString("Partners_color")));
		pa.add(t = new FormTextField(c));
		t.setEditable(false);
		pa.add(new MyLabel(Global.resourceString("Handicap")));
		pa.add(t = new FormTextField("" + h));
		t.setEditable(false);
		pa.add(new MyLabel(Global.resourceString("Total_Time__min_")));
		pa.add(t = new FormTextField("" + tt));
		t.setEditable(false);
		pa.add(new MyLabel(Global.resourceString("Extra_Time__min_")));
		pa.add(t = new FormTextField("" + et));
		t.setEditable(false);
		pa.add(new MyLabel(Global.resourceString("Moves_per_Extra_Time")));
		pa.add(t = new FormTextField("" + em));
		t.setEditable(false);
		JPanel pb = new MyPanel();
		pb.add(new ButtonAction(this, Global.resourceString("Accept")));
		pb.add(new ButtonAction(this, Global.resourceString("Decline")));
		add("South", new Panel3D(pb));
		Global.setpacked(this, "boardquestion", 300, 400, pf);
		validate();
		setVisible(true);
	}

	@Override
	public void doAction (String o)
	{
		Global.notewindow(this, "boardquestion");
		if (Global.resourceString("Accept").equals(o))
		{
			PF.doboard(BoardSize, ColorChoice, Handicap, TotalTime, ExtraTime,
				ExtraMoves);
			setVisible(false);
			dispose();
		}
		else if (Global.resourceString("Decline").equals(o))
		{
			PF.declineboard();
			setVisible(false);
			dispose();
		}
		else super.doAction(o);
	}
}
