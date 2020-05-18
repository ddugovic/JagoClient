package jagoclient.gui;

import jagoclient.Global;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;

public class BigLabel extends Panel
{
	Image I = null;
	Graphics GI;
	FontMetrics FM;
	int Offset;
	int W, H;
	Font F;

	public BigLabel (Font f)
	{
		F = f;
		if (f != null) setFont(f);
		FM = getFontMetrics(f);
	}

	@Override
	public void paint (Graphics g)
	{
		Dimension d = getSize();
		int w = d.width, h = d.height;
		if (I == null || w != W || h != H)
		{
			W = w;
			H = h;
			I = createImage(W, H);
			if (I == null) return;
			GI = I.getGraphics();
			if (F != null) GI.setFont(F);
			FM = GI.getFontMetrics();
			Offset = FM.charWidth('m') / 2;
		}
		GI.setColor(Global.ControlBackground);
		GI.fillRect(0, 0, W, H);
		GI.setColor(Color.black);
		drawString(GI, Offset, (H + FM.getAscent() - FM.getDescent()) / 2, FM);
		g.drawImage(I, 0, 0, W, H, this);
	}

	@Override
	public void update (Graphics g)
	{
		paint(g);
	}

	public void drawString (Graphics g, int x, int y, FontMetrics fm)
	{}

	@Override
	public Dimension getPreferredSize ()
	{
		return new Dimension(getSize().width,
			(FM.getAscent() + FM.getDescent()) * 3 / 2);
	}

	@Override
	public Dimension getMinimumSize ()
	{
		return getPreferredSize();
	}
}
