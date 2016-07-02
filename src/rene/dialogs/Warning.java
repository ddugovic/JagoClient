package rene.dialogs;

import java.awt.*;
import java.awt.event.*;

import rene.gui.*;

/**
This is a simple warning dialog. May be used as modal or non-modal
dialog.
*/

public class Warning extends CloseDialog 
    implements ActionListener
{	public boolean Result;
	Frame F;
	
	public Warning (Frame f, String c, String title, boolean flag, String help)
	{	super(f,title,flag);
		F=f;
		Panel pc=new MyPanel();
		FlowLayout fl=new FlowLayout();
		pc.setLayout(fl);
		fl.setAlignment(FlowLayout.CENTER);
		pc.add(new MyLabel(" "+c+" "));
		add("Center",pc);
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.name("close"),"Close"));
		if (help!=null && !help.equals("")) addHelp(p,help);
		add("South",p);
		pack();
	}
	
	public Warning (Frame f, String c, String title, boolean flag)
	{	this(f,c,title,flag,"");
	}

	public Warning (Frame f, String c, String title)
	{	this(f,c,title,true,"");
	}
	
	public Warning (Frame f, String c1, String c2, String title, boolean flag,
			String help)
	{	super(f,title,flag);
		F=f;
		Panel pc=new MyPanel();
		pc.setLayout(new GridLayout(0,1));
		pc.add(new MyLabel(" "+c1+" "));
		pc.add(new MyLabel(" "+c2+" "));
		add("Center",pc);
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.name("close"),"Close"));
		if (help!=null && !help.equals("")) addHelp(p,help);
		add("South",p);
		pack();
	}
	
	public Warning (Frame f, String c1, String c2, String title, boolean flag)
	{	this(f,c1,c2,title,flag,"");
	}
	
	public Warning (Frame f, String c1, String c2, String title)
	{	this(f,c1,c2,title,true,"");
	}
}
