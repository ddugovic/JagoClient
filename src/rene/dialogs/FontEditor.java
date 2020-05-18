package rene.dialogs;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextField;

import rene.gui.ButtonAction;
import rene.gui.CheckboxAction;
import rene.gui.ChoiceAction;
import rene.gui.CloseDialog;
import rene.gui.DoItemListener;
import rene.gui.Global;
import rene.gui.IntField;
import rene.gui.MyLabel;
import rene.gui.MyPanel;
import rene.gui.Panel3D;
import rene.gui.TextFieldAction;

/**
 * A canvas to display a sample of the chosen font. The samples is drawn from
 * the GetFontSize dialog.
 */

class ExampleCanvas extends Canvas
{
	FontEditor GFS;

	public ExampleCanvas (FontEditor gfs)
	{
		GFS = gfs;
	}

	@Override
	public void paint (Graphics g)
	{
		GFS.example(g, getSize().width, getSize().height);
	}

	@Override
	public Dimension getPreferredSize ()
	{
		return new Dimension(200, 100);
	}
}


/**
 * A dialog to get the font size of the fixed font and its name. Both items are
 * stored as a Global Parameter.
 */

public class FontEditor extends CloseDialog implements DoItemListener
{
	String FontTag;
	TextField FontName;
	IntField FontSize, FontSpacing;
	Choice Fonts, Mode;
	Canvas Example;
	String E = Global.name("fonteditor.sample");
	Checkbox Smooth;

	/**
	 * @param fonttag the font name resource tag
	 * @param fontdef the font name default value
	 * @param sizedef the font size default value
	 */
	public FontEditor (Frame f, String fonttag, String fontdef, int sizedef)
	{
		super(f, Global.name("fonteditor.title"), true);
		FontTag = fonttag;
		setLayout(new BorderLayout());
		Panel p = new MyPanel();
		p.setLayout(new GridLayout(0, 2));
		p.add(new MyLabel(Global.name("fonteditor.name")));
		p.add(FontName = new TextFieldAction(this, "FontName"));
		FontName.setText(Global.getParameter(fonttag + ".name", fontdef));
		p.add(new MyLabel(Global.name("fonteditor.available")));
		p.add(Fonts = new ChoiceAction(this, "Fonts"));
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		if (fonts != null)
		{
			for (int i = 0; i < fonts.length; i++)
			{
				Fonts.add(fonts[i]);
			}
		}
		else
		{
			Fonts.add("Dialog");
			Fonts.add("SansSerif");
			Fonts.add("Serif");
			Fonts.add("Monospaced");
			Fonts.add("DialogInput");
		}
		Fonts.add("Courier");
		Fonts.add("TimesRoman");
		Fonts.add("Helvetica");
		Fonts.select(FontName.getText());
		p.add(new MyLabel(Global.name("fonteditor.mode")));
		p.add(Mode = new ChoiceAction(this, "Mode"));
		Mode.add(Global.name("fonteditor.plain"));
		Mode.add(Global.name("fonteditor.bold"));
		Mode.add(Global.name("fonteditor.italic"));
		String name = Global.getParameter(fonttag + ".mode", "plain");
		if (name.startsWith("bold"))
			Mode.select(1);
		else if (name.startsWith("italic"))
			Mode.select(2);
		else Mode.select(0);
		p.add(new MyLabel(Global.name("fonteditor.size")));
		p.add(FontSize = new IntField(this, "FontSize", Global.getParameter(
			fonttag + ".size", sizedef)));
		p.add(new MyLabel(Global.name("fonteditor.spacing")));
		p.add(FontSpacing = new IntField(this, "FontSpacing", Global
			.getParameter(fonttag + ".spacing", 0)));
		p.add(new MyLabel(Global.name("fonteditor.antialias")));
		p.add(Smooth = new CheckboxAction(this, "", "Smooth"));
		Smooth.setState(Global.getParameter("font.smooth", true));
		add("North", new Panel3D(p));
		Example = new ExampleCanvas(this);
		add("Center", new Panel3D(Example));
		Panel bp = new MyPanel();
		bp.add(new ButtonAction(this, Global.name("OK"), "OK"));
		bp.add(new ButtonAction(this, Global.name("close"), "Close"));
		add("South", new Panel3D(bp));
		pack();
	}

	@Override
	public void doAction (String o)
	{
		if ("OK".equals(o))
		{
			Global.setParameter(FontTag + ".name", FontName.getText());
			String s = "plain";
			if (mode() == Font.BOLD)
				s = "bold";
			else if (mode() == Font.ITALIC) s = "Italic";
			Global.setParameter(FontTag + ".mode", s);
			Global.setParameter(FontTag + ".size", FontSize.value(3, 50));
			Global.setParameter(FontTag + ".spacing", FontSpacing.value( -10,
				10));
			Global.setParameter("font.smooth", Smooth.getState());
			doclose();
		}
		else super.doAction(o);
		Example.repaint();
	}

	@Override
	public void itemAction (String s, boolean flag)
	{
		FontName.setText(Fonts.getSelectedItem());
		Example.repaint();
	}

	int mode ()
	{
		if (Mode.getSelectedItem().equals(Global.name("fonteditor.bold")))
			return Font.BOLD;
		else if (Mode.getSelectedItem()
			.equals(Global.name("fonteditor.italic")))
			return Font.ITALIC;
		else return Font.PLAIN;
	}

	public void example (Graphics g, int w, int h)
	{
		Font f = new Font(FontName.getText(), mode(), FontSize.value(3, 50));
		g.setFont(f);
		FontMetrics fm = g.getFontMetrics();
		int d = FontSpacing.value( -10, 10);
		for (int i = 1; i <= 4; i++)
			g.drawString(E, 5, 5 + d + i * d + fm.getLeading() + fm.getAscent()
				+ i * fm.getHeight());
	}
}
