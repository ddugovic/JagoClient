package jagoclient.dialogs;

import jagoclient.Global;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.IntField;
import jagoclient.gui.MyLabel;
import jagoclient.gui.MyPanel;
import jagoclient.gui.Panel3D;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import rene.gui.CloseDialog;
import rene.gui.DoActionListener;

class ColorScrollbar extends Panel implements AdjustmentListener, DoActionListener
{
	public int Value;
	ColorEdit CE;
	Scrollbar SB;
	IntField L;

	public ColorScrollbar (ColorEdit ce, String s, int value)
	{
		super(new GridLayout(1, 0));
		CE = ce;
		Value = value;
		MyPanel p = new MyPanel(new GridLayout(1, 0));
		p.add(new MyLabel(s));
		p.add(L = new IntField(this, "L", Value, 4));
		add(p);
		add(SB = new Scrollbar(Scrollbar.HORIZONTAL, value, 40, 0, 295));
		SB.addAdjustmentListener(this);
	}

	public void doAction (String o)
	{
		if ("L".equals(o))
		{
			Value = L.value(0, 255);
			SB.setValue(Value);
			CE.setcolor();
		}
	}

	public void itemAction (String o, boolean flag)
	{}

	public void adjustmentValueChanged (AdjustmentEvent e)
	{
		Value = SB.getValue();
		L.set(Value);
		SB.setValue(Value);
		CE.setcolor();
	}

	public int value ()
	{
		return Value;
	}
}


/**
 * A dialog to edit a color. The result is stored in the Global parameters under
 * the specified name string. Modality is handled as in the Question dialog.
 * 
 * @see jagoclient.Global
 * @see rene.dialogs.Question
 */

public class ColorEdit extends CloseDialog
{
	ColorScrollbar Red, Green, Blue;
	Label RedLabel, GreenLabel, BlueLabel;
	Color C;
	MyPanel CP;
	String Name;

	public ColorEdit (Frame F, String s, int red, int green, int blue, Color c, boolean flag)
	{
		super(F, Global.resourceString("Edit_Color"), flag);
		Name = s;
		C = Global.getColor(s, red, green, blue, c);
		MyPanel p = new MyPanel();
		p.setLayout(new GridLayout(0, 1));
		p.add(Red = new ColorScrollbar(this, Global.resourceString("Red"), C.getRed()));
		p.add(Green = new ColorScrollbar(this, Global.resourceString("Green"), C.getGreen()));
		p.add(Blue = new ColorScrollbar(this, Global.resourceString("Blue"), C.getBlue()));
		add("Center", new Panel3D(p));
		MyPanel pb = new MyPanel();
		pb.add(new ButtonAction(this, Global.resourceString("OK")));
		pb.add(new ButtonAction(this, Global.resourceString("Cancel")));
		addbutton(pb);
		add("South", new Panel3D(pb));
		CP = new MyPanel();
		CP.add(new MyLabel(Global.resourceString("Your_Color")));
		CP.setBackground(C);
		add("North", new Panel3D(CP));
		Global.setpacked(this, "coloredit", 350, 150);
		validate();
	}

	public void addbutton (MyPanel p)
	{}

	public ColorEdit (Frame F, String s, Color C, boolean flag)
	{
		this(F, s, C.getRed(), C.getGreen(), C.getBlue(), C, flag);
	}

	@Override
	public void doAction (String o)
	{
		Global.notewindow(this, "coloredit");
		if (Global.resourceString("Cancel").equals(o))
		{
			setVisible(false);
			dispose();
		}
		else if (Global.resourceString("OK").equals(o))
		{
			Global.setColor(Name, C);
			tell(C);
			setVisible(false);
			dispose();
		}
	}

	public void setcolor ()
	{
		C = new Color(Red.value(), Green.value(), Blue.value());
		CP.setBackground(C);
		CP.repaint();
	}

	public void tell (Color C)
	{}

	public Color color ()
	{
		return C;
	}
}
