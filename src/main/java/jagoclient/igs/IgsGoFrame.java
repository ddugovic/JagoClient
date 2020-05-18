package jagoclient.igs;

import jagoclient.Global;
import jagoclient.board.ConnectedGoFrame;
import jagoclient.board.GoTimer;
import jagoclient.board.OutputFormatter;
import jagoclient.board.TimedBoard;
import jagoclient.gui.CheckboxMenuItemAction;
import jagoclient.sound.JagoSound;

import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import rene.gui.IconBar;
import rene.util.parser.StringParser;

/**
 * This is a ConnectedGoFrame, which is used to display boards on the server
 * (status, observing or playing). It takes care of the timer and interprets
 * menu actions (like send etc.)
 * <P>
 * The board is connected to a distributor (e.g. PlayDistributor), which
 * communicates with the IgsStream. The distributor will normally invoke a
 * second object, which parses server input and sends it to this frame.
 * <p>
 * Note that there is a timer to count down the remaining seconds.
 */

public class IgsGoFrame extends ConnectedGoFrame implements TimedBoard,
	OutputListener, KeyListener
{
	Distributor Dis; // Distributor for this frame
	String BlackName = "?", WhiteName = "?";
	int BlackTime = 0, WhiteTime = 0, BlackMoves, WhiteMoves;
	int BlackRun = 0, WhiteRun = 0;
	int GameNumber;
	Thread Timer;
	long CurrentTime;
	public CheckboxMenuItem Playing, Terminal, ShortLines;
	public ConnectionFrame CF;
	String Title = "";
	boolean HaveTime = false;

	public IgsGoFrame (ConnectionFrame f, String s)
	{
		super(s, 19, Global.resourceString("Remove_groups"), Global
			.resourceString("Send_done"), true, true);
		Dis = null;
		CF = f;
		Timer = new Thread(new GoTimer(this, 1000));
		FileMenu.addSeparator();
		FileMenu.add(Playing = new CheckboxMenuItemAction(this, Global
			.resourceString("Play")));
		Options.addSeparator();
		Options.add(Terminal = new CheckboxMenuItemAction(this, Global
			.resourceString("Display_Terminal_Output")));
		// Fix for Linux Java 1.5:
		try
		{
			Terminal.setState(Global.getParameter("getterminal", true));
		}
		catch (Exception e)
		{}
		setterminal();
		Options.add(ShortLines = new CheckboxMenuItemAction(this, Global
			.resourceString("Short_Lines_only")));
		try
		{
			ShortLines.setState(Global.getParameter("shortlinesonly", true));
		}
		catch (Exception e)
		{}
		addKeyListener(this);
		B.addKeyListener(this);
		if (ExtraSendField)
		{
			SendField.addKeyListener(this);
			SendField.loadHistory("sendfield.history");
		}
	}

	@Override
	public void addSendForward (IconBar I)
	{
		I.addLeft("sendforward");
	}

	@Override
	public void iconPressed (String s)
	{
		if (s.equals("sendforward") && Dis != null)
		{
			Dis.out(">");
		}
		else super.iconPressed(s);
	}

	@Override
	public void doAction (String o)
	{
		if (Global.resourceString("Send").equals(o) && Dis != null)
		{
			new SendQuestion(this, Dis);
		}
		else if (ExtraSendField && "SendField".equals(o) && Dis != null)
		{
			String s = SendField.getText();
			addComment("---> " + s);
			Dis.out(s);
			SendField.remember(s);
			SendField.setText("");
		}
		else if (Global.resourceString("Refresh").equals(o) && Dis != null)
		{
			B.deltree();
			Dis.refresh();
		}
		else if (Global.resourceString("Remove_groups").equals(o))
		{
			B.score();
		}
		else if (Global.resourceString("Send_done").equals(o))
		{
			if (B.canfinish() && Dis != null && Dis.wantsmove())
			{
				Dis.out("done");
				addComment("--> done <--");
			}
		}
		else if (Global.resourceString("Undo").equals(o))
		{
			B.undo();
		}
		else if (Global.resourceString("Load_Teaching_Game").equals(o))
		{
			if (Teaching.getState())
				super.doAction(Global.resourceString("Load"));
		}
		else super.doAction(o);
	}

	@Override
	public void itemAction (String o, boolean flag)
	{
		if (Global.resourceString("Play").equals(o))
		{
			Dis.Playing = flag;
		}
		else if (Global.resourceString("Display_Terminal_Output").equals(o))
		{
			setterminal();
		}
		else if (Global.resourceString("Short_Lines_only").equals(o))
		{
			Global.setParameter("shortlinesonly", flag);
		}
		super.itemAction(o, flag);
	}

	public void setterminal ()
	{
		if (Terminal.getState())
			CF.addOutputListener(this);
		else CF.removeOutputListener(this);
		Global.setParameter("getterminal", Terminal.getState());
	}

	/** an IgsGoFrame is blocked, when there is not Distributor left */
	@Override
	public boolean blocked ()
	{
		if (Dis != null)
			return Dis.blocked();
		else return false;
	}

	@Override
	public boolean wantsmove ()
	{
		if (Dis != null)
			return Dis.wantsmove();
		else return false;
	}

	@Override
	public boolean moveset (int i, int j)
	{
		if (Dis != null)
		{
			if (B.maincolor() > 0)
				Dis.set(i, j, BlackRun);
			else Dis.set(i, j, WhiteRun);
		}
		return true;
	}

	@Override
	public void movepass ()
	{
		if (Dis != null) Dis.pass();
	}

	@Override
	public void undo ()
	{
		if (Dis != null) Dis.out("undo");
	}

	public void distributor (Distributor o)
	{
		Dis = o;
		try
		{
			if (Dis != null) Playing.setState(Dis.Playing);
		}
		catch (Exception e)
		{}
	}

	@Override
	public void doclose ()
	{
		if (Dis != null) Dis.remove();
		CF.removeOutputListener(this);
		if (Timer != null && Timer.isAlive()) Timer.interrupt();
		if (ExtraSendField) SendField.saveHistory("sendfield.history");
		super.doclose();
	}

	/**
	 * This is called by Player to determine the time from the move information.
	 * 
	 * @see jagoclient.igs.Player
	 */
	public void settime (String s)
	{
		StringParser p = new StringParser(s);
		if ( !p.skip("Game")) return;
		int g = p.parseint();
		if (p.error()) return;
		p.skipblanks();
		if ( !p.skip("I:")) return;
		String w = p.parseword();
		p.skipblanks();
		if ( !p.skip("(")) return;
		int w1 = p.parseint();
		int w2 = p.parseint();
		int w3 = p.parseint(')');
		p.skip(")");
		if (p.error()) return;
		p.skipblanks();
		if ( !p.skip("vs")) return;
		String b = p.parseword();
		p.skipblanks();
		if ( !p.skip("(")) return;
		int b1 = p.parseint();
		int b2 = p.parseint();
		int b3 = p.parseint(')');
		if ( !p.skip(")")) return;
		BlackName = b;
		WhiteName = w;
		BlackTime = b2;
		BlackMoves = b3;
		WhiteTime = w2;
		WhiteMoves = w3;
		GameNumber = g;
		BlackRun = 0;
		WhiteRun = 0;
		CurrentTime = System.currentTimeMillis();
		HaveTime = true;
		settitle1();
	}

	/** called by Player to set the game title */
	void settitle ()
	{
		HaveTime = true;
		settitle1();
	}

	String OldS = "";

	void settitle1 ()
	{
		String S;
		if (BigTimer)
			S = Global.resourceString("Game_") + GameNumber + ": " + WhiteName
				+ " " + formmoves(WhiteMoves) + " - " + BlackName + " "
				+ formmoves(BlackMoves);
		else S = Global.resourceString("Game_") + GameNumber + ": " + WhiteName
			+ " " + formtime(WhiteTime - WhiteRun) + " "
			+ formmoves(WhiteMoves) + " - " + BlackName + " "
			+ formtime(BlackTime - BlackRun) + " " + formmoves(BlackMoves);
		if (Global.getParameter("extrainformation", true))
			S = S + " " + B.extraInformation();
		if ( !S.equals(OldS))
		{
			if ( !TimerInTitle)
				TL.setText(S);
			else setTitle(S);
			OldS = S;
		}
		if (BigTimer && HaveTime)
		{
			BL.setTime(WhiteTime - WhiteRun, BlackTime - BlackRun, WhiteMoves,
				BlackMoves, B.MyColor);
			BL.repaint();
		}
	}

	void settitle (String s)
	{
		Title = s;
		setTitle(s);
	}

	char form[] = new char[32];

	String formmoves (int m)
	{
		if (m < 0) return "";
		form[0] = '(';
		int n = OutputFormatter.formint(form, 1, m);
		form[n++] = ')';
		return new String(form, 0, n);
	}

	String formtime (int sec)
	{
		int n = OutputFormatter.formtime(form, sec);
		return new String(form, 0, n);
	}

	int lastbeep = 0;

	public void beep (int s)
	{
		if (s < 0 || !Global.getParameter("warning", true))
			return;
		else if (s < 31 && s != lastbeep)
		{
			if (s % 10 == 0)
			{
				getToolkit().beep();
				lastbeep = s;
			}
		}
	}

	public void alarm ()
	{
		long now = System.currentTimeMillis();
		if (B.maincolor() > 0)
		{
			BlackRun = (int)((now - CurrentTime) / 1000);
			if (B.MyColor > 0) beep(BlackTime - BlackRun);
		}
		else
		{
			WhiteRun = (int)((now - CurrentTime) / 1000);
			if (B.MyColor < 0) beep(WhiteTime - WhiteRun);
		}
		settitle1();
	}

	/** called from the board to sound an alarm */
	@Override
	public void yourMove (boolean notinpos)
	{
		if (Dis == null || !Dis.started()) return;
		if (notinpos)
		{
			if (Dis.wantsmove())
				JagoSound.play("yourmove", "stone", true);
			else JagoSound.play("stone", "click", false);
		}
		else if (Global.getParameter("sound.everymove", true))
			JagoSound.play("stone", "click", true);
		else if (Dis.Playing || Dis.newmove())
			JagoSound.play("click", "", false);
	}

	public void append (String s)
	{
		if (s
			.startsWith("Board is restored to what it was when you started scoring"))
		{
			B.clearremovals();
			s = Global.resourceString("Opponent_undid_removals_");
		}
		if (ShortLines.getState() && s.length() > 100) return;
		if (s.startsWith("*"))
			addComment(s);
		else addtoallcomments(s);
	}

	public void append (String s, Color c)
	{
		append(s);
	}

	@Override
	public void keyPressed (KeyEvent e)
	{}

	@Override
	public void keyTyped (KeyEvent e)
	{}

	@Override
	public void keyReleased (KeyEvent e)
	{
		String s;
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			s = "";
		else
		{
			s = Global.getFunctionKey(e.getKeyCode());
			if (s.equals("") || !ExtraSendField) return;
		}
		SendField.setText(s);
	}

	@Override
	public void windowOpened (WindowEvent e)
	{
		if (SendField != null) SendField.requestFocus();
	}

	/**
	 * Called from player to set the board information. This is passed to the
	 * board, which stores this information in the SGF tree (root node).
	 */
	public void setinformation (String black, String blackrank, String white,
		String whiterank, String komi, String handicap)
	{
		B.setinformation(black, blackrank, white, whiterank, komi, handicap);
	}
}
