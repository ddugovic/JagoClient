package rene.viewer;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.PrintWriter;

import rene.gui.Global;
import rene.util.list.ListClass;
import rene.util.list.ListElement;

class ClipboardCopy extends Thread
{
	String S;
	ClipboardOwner C;
	Canvas Cv;

	public ClipboardCopy (ClipboardOwner c, Canvas cv, String s)
	{
		S = s;
		C = c;
		Cv = cv;
		start();
	}

	@Override
	public void run ()
	{
		Clipboard clip = Cv.getToolkit().getSystemClipboard();
		StringSelection cont = new StringSelection(S);
		clip.setContents(cont, C);
	}
}


public class TextDisplay extends Canvas implements ClipboardOwner,
	ComponentListener
{
	ListClass L;
	Font F = null;
	FontMetrics FM;
	Viewer V;
	int Leading, Height, Ascent, Descent;
	int LineCount, TopLineCount;
	int PageSize;
	ListElement TopLine;
	Image I;
	Graphics2D IG;
	int W, H;
	public int Tabsize = 4;
	public int Offset;
	boolean LineFinished = true;
	int Widths[], HW[];
	long LastScrollTime;
	Color Background;
	int TabWidth = 0;

	public TextDisplay (Viewer v)
	{
		L = new ListClass();
		F = null;
		V = v;
		LineCount = 0;
		TopLineCount = 0;
		TopLine = null;
		I = null;
		W = H = 0;
		PageSize = 10;
		HW = new int[1024];
		addKeyListener(v);
		addComponentListener(this);
	}

	void init (Font f)
	{
		F = f;
		FM = getFontMetrics(F);
		Leading = FM.getLeading()
			+ Global.getParameter("fixedfont.spacing", -1);
		Height = FM.getHeight();
		Ascent = FM.getAscent();
		Descent = FM.getDescent();
		Widths = FM.getWidths();
		if (Global.Background != null)
			Background = Global.Background;
		else Background = SystemColor.window;
	}

	@Override
	public Color getBackground ()
	{
		if (Global.Background != null)
			return Global.Background;
		else return SystemColor.window;
	}

	int[] getwidth (char a[])
	{
		try
		{
			for (int i = 0; i < a.length; i++)
			{
				if (a[i] < 256)
					HW[i] = Widths[a[i]];
				else HW[i] = FM.charWidth(a[i]);
			}
		}
		catch (Exception e)
		{
			return HW;
		}
		return HW;
	}

	public synchronized void appendLine0 (String S)
	{
		appendLine0(S, Color.black);
	}

	public synchronized void appendLine0 (String S, Color c)
	{
		Line l;
		L.append(new ListElement(l = new Line(S, this, c)));
		LineCount++;
		if (LineCount == 1) TopLine = L.first();
		LineFinished = true;
		if (TabWidth > 0) l.expandTabs(TabWidth);
	}

	public synchronized void appendLine (String s)
	{
		appendLine0(s);
		V.setVerticalScrollbar();
	}

	public void append (String S, Color c)
	{
		append(S, c, true);
	}

	public void append (String S, Color c, boolean suddenupdate)
	{
		while (true)
		{
			int p = S.indexOf('\n');
			if (p < 0)
			{
				appendlast(S, c);
				LineFinished = false;
				break;
			}
			appendlast(S.substring(0, p), c);
			LineFinished = true;
			S = S.substring(p + 1);
			if (S.equals(""))
			{
				break;
			}
		}
		if (suddenupdate) doUpdate(true);
		repaint();
	}

	public void doUpdate (boolean showlast)
	{
		if (showlast)
		{
			long m = System.currentTimeMillis();
			if (m - LastScrollTime > 10000) showlast();
		}
		repaint();
		V.setVerticalScrollbar();
	}

	public void setText (String s)
	{
		TopLine = null;
		TopLineCount = 0;
		LineCount = 0;
		L = new ListClass();
		if ( !s.equals("")) append(s, Color.black);
		repaint();
	}

	public synchronized void appendlast (String s, Color c)
	{
		if (LineFinished || L.last() == null)
		{
			Line l;
			L.append(new ListElement(l = new Line(s, this, c)));
			LineCount++;
			if (LineCount == 1) TopLine = L.first();
			if (TabWidth > 0) l.expandTabs(TabWidth);
		}
		else
		{
			((Line)L.last().content()).append(s);
		}
	}

	public void showlast ()
	{
		ListElement e = L.last();
		if (e == null) return;
		TopLineCount = LineCount;
		for (int i = 0; i < PageSize - 1; i++)
		{
			if (e.previous() == null) break;
			e = e.previous();
			TopLineCount--;
		}
		TopLine = e;
		repaint();
	}

	public void makeimage ()
	{
		Dimension D = getSize();
		if (I == null || D.width != W || D.height != H)
		{
			I = createImage(W = D.width, H = D.height);
			IG = (Graphics2D)I.getGraphics();
			IG.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);

		}
		IG.setColor(Color.black);
		IG.clearRect(0, 0, W, H);
		IG.setFont(F);
		try
		{
			PageSize = H / (Height + Leading);
		}
		catch (Exception e)
		{}
	}

	@Override
	public synchronized void paint (Graphics g)
	{
		if (F == null) init(getFont());
		makeimage();
		ListElement e = TopLine;
		antialias(true);
		int h = Leading + Ascent;
		int totalh = getSize().height - Descent;
		if (Background == null) Background = getBackground();
		IG.setColor(Background);
		IG.fillRect(0, 0, W, H);
		int lines = 0;
		while (lines < PageSize && e != null)
		{
			Line l = (Line)e.content();
			l.draw(IG, 2, h);
			h += Leading + Height;
			e = e.next();
			lines++;
		}
		g.drawImage(I, 0, 0, this);
	}

	/**
	 * Set Anti-Aliasing on or off, if in Java 1.2 or better and the Parameter
	 * "font.smooth" is switched on.
	 * 
	 * @param flag
	 */
	public void antialias (boolean flag)
	{
		if (Global.getParameter("font.smooth", true))
		{
			IG.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				flag?RenderingHints.VALUE_TEXT_ANTIALIAS_ON
					:RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
	}

	public void showLine (ListElement line)
	{
		ListElement e = TopLine;
		int h = Leading + Ascent;
		int totalh = getSize().height - Descent;
		if (Background == null) Background = getBackground();
		int lines = 0;
		while (lines < PageSize && e != null)
		{
			if (e == line) return;
			h += Leading + Height;
			e = e.next();
			lines++;
		}
		if (e == line && TopLine.next() != null)
			TopLine = TopLine.next();
		else TopLine = line;
	}

	public ListElement getline (int y)
	{
		if (TopLine == null) return null;
		ListElement e = TopLine;
		int h = Leading + Height;
		if (h == 0) return null;
		h = y / h;
		for (int i = 0; i < h; i++)
		{
			if (e.next() == null) return e;
			e = e.next();
		}
		return e;
	}

	@Override
	public void update (Graphics g)
	{
		paint(g);
	}

	int computeVertical ()
	{
		if (LineCount > 0)
			return TopLineCount * 1000 / LineCount;
		else return 0;
	}

	public int setVertical (int v)
	{
		if (TopLine == null) return 0;
		int NewTop = LineCount * v / 1000;
		if (NewTop > TopLineCount)
		{
			for (int i = TopLineCount; i < NewTop; i++)
			{
				if (TopLine.next() == null) break;
				TopLine = TopLine.next();
				TopLineCount++;
			}
			repaint();
		}
		else if (NewTop < TopLineCount)
		{
			for (int i = TopLineCount; i > NewTop; i--)
			{
				if (TopLine.previous() == null) break;
				TopLine = TopLine.previous();
				TopLineCount--;
			}
			repaint();
		}
		LastScrollTime = System.currentTimeMillis();
		return v;
	}

	public void verticalUp ()
	{
		if (TopLine == null) return;
		if (TopLine.next() == null) return;
		TopLine = TopLine.next();
		TopLineCount++;
		repaint();
		LastScrollTime = System.currentTimeMillis();
	}

	public void verticalDown ()
	{
		if (TopLine == null) return;
		if (TopLine.previous() == null) return;
		TopLine = TopLine.previous();
		TopLineCount--;
		repaint();
		LastScrollTime = System.currentTimeMillis();
	}

	public void verticalPageUp ()
	{
		if (TopLine == null) return;
		for (int i = 0; i < PageSize - 1; i++)
		{
			if (TopLine.next() == null) break;
			TopLine = TopLine.next();
			TopLineCount++;
		}
		repaint();
		LastScrollTime = System.currentTimeMillis();
	}

	public void verticalPageDown ()
	{
		if (TopLine == null) return;
		for (int i = 0; i < PageSize - 1; i++)
		{
			if (TopLine.previous() == null) break;
			TopLine = TopLine.previous();
			TopLineCount--;
		}
		repaint();
		LastScrollTime = System.currentTimeMillis();
	}

	int computeVerticalSize ()
	{
		if (LineCount == 0) return 100;
		int h = PageSize * 2000 / LineCount;
		if (h < 10) h = 10;
		return h;
	}

	public int setHorizontal (int v)
	{
		Offset = v / 5;
		repaint();
		return v;
	}

	public void save (PrintWriter fo)
	{
		ListElement e = L.first();
		while (e != null)
		{
			fo.println(new String(((Line)e.content()).a));
			e = e.next();
		}
	}

	public TextPosition getposition (int x, int y)
	{
		if (L.first() == null) return null;
		if (y < 0) return new TextPosition(TopLine, TopLineCount, 0);
		if (TopLine == null) return null;
		ListElement e = TopLine;
		int h = Leading + Height;
		if (h == 0) return null;
		h = y / h;
		int i;
		for (i = 0; i < h; i++)
		{
			if (e.next() == null || i == PageSize - 1)
				return new TextPosition(e, TopLineCount + i,
					((Line)e.content()).length());
			e = e.next();
		}
		return new TextPosition(e, TopLineCount + i, ((Line)e.content())
			.getpos(x, 2));
	}

	public void unmark ()
	{
		ListElement e = L.first();
		while (e != null)
		{
			((Line)e.content()).block(0, Line.NONE);
			e = e.next();
		}
		repaint();
	}

	public void unmark (TextPosition Start, TextPosition End)
	{
		if (Start == null || End == null) return;
		TextPosition P1, P2;
		if (Start.before(End))
		{
			P1 = Start;
			P2 = End;
		}
		else if (End.before(Start))
		{
			P1 = End;
			P2 = Start;
		}
		else return;
		ListElement e = P1.L;
		while (e != null && e != P2.L)
		{
			((Line)e.content()).block(0, Line.NONE);
			e = e.next();
		}
		if (e != null) ((Line)e.content()).block(0, Line.NONE);
		repaint();
	}

	public void mark (TextPosition Start, TextPosition End)
	{
		if (Start == null || End == null) return;
		TextPosition P1, P2;
		if (Start.before(End))
		{
			P1 = Start;
			P2 = End;
		}
		else if (End.before(Start))
		{
			P1 = End;
			P2 = Start;
		}
		else return;
		ListElement e = P1.L;
		((Line)e.content()).block(P1.LPos, Line.START);
		if (e != P2.L) e = e.next();
		while (e != null && e != P2.L)
		{
			((Line)e.content()).block(0, Line.FULL);
			e = e.next();
		}
		if (e != null) ((Line)e.content()).block(P2.LPos, Line.END);
		repaint();
		requestFocus();
	}

	void copy (TextPosition Start, TextPosition End)
	{
		if (Start == null || End == null) return;
		TextPosition P1, P2;
		if (Start.before(End))
		{
			P1 = Start;
			P2 = End;
		}
		else if (End.before(Start))
		{
			P1 = End;
			P2 = Start;
		}
		else return;
		String s = "";
		ListElement e = P1.L;
		while (e != null && e != P2.L)
		{
			s = s + ((Line)e.content()).getblock() + "\n";
			e = e.next();
		}
		if (e != null) s = s + ((Line)e.content()).getblock();
		new ClipboardCopy(this, this, s);
	}

	public void showFirst ()
	{
		TopLine = L.first();
	}

	public void lostOwnership (Clipboard clip, Transferable cont)
	{}

	TextPosition lastpos ()
	{
		ListElement e = L.last();
		if (e == null) return null;
		Line l = (Line)e.content();
		return new TextPosition(e, LineCount, l.length());
	}

	public void setTabWidth (int t)
	{
		TabWidth = t;
	}

	@Override
	public boolean hasFocus ()
	{
		return V.hasFocus();
	}

	@Override
	public void setBackground (Color c)
	{
		Background = c;
		super.setBackground(c);
	}

	public void componentHidden (ComponentEvent e)
	{}

	public void componentMoved (ComponentEvent e)
	{}

	public void componentResized (ComponentEvent e)
	{
		V.resized();
	}

	public void componentShown (ComponentEvent e)
	{}
}
