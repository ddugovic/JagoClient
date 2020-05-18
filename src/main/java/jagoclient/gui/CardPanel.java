package jagoclient.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;

class CardPanelButton extends JButton implements ActionListener, KeyListener
{
	String Name;
	JPanel P;
	CardLayout CL;

	public CardPanelButton (String text, CardLayout cl, String name, JPanel p)
	{
		super(text);
		Name = name;
		P = p;
		CL = cl;
		addActionListener(this);
		addKeyListener(this);
		// setFont(Global.SansSerif);
	}

	public void actionPerformed (ActionEvent e)
	{
		CL.show(P, Name);
	}

	public void keyPressed (KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			CL.show(P, Name);
		}
	}

	public void keyReleased (KeyEvent e)
	{}

	public void keyTyped (KeyEvent e)
	{}
}


/**
 * A simplified card panel. The panel has a south component, which displays
 * buttons, which switch the center component.
 */

public class CardPanel extends Panel
{
	MyPanel P, Bp;
	CardLayout CL;

	public CardPanel ()
	{
		setLayout(new BorderLayout());
		P = new MyPanel();
		P.setLayout(CL = new CardLayout());
		add("Center", new Panel3D(P));
		Bp = new MyPanel();
		add("South", new Panel3D(Bp));
	}

	/**
	 * Adds a component to the card panel. The name is used to create a button
	 * with this label.
	 */
	public void add (Component c, String name)
	{
		P.add(name, c);
		Bp.add(new CardPanelButton(name, CL, name, P));
	}
}
