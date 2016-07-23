package jagoclient.partner;

import jagoclient.CloseConnection;
import jagoclient.Global;
import jagoclient.dialogs.Help;
import jagoclient.dialogs.Message;
import jagoclient.gui.ButtonAction;
import jagoclient.gui.HistoryTextField;
import jagoclient.gui.MenuItemAction;
import jagoclient.gui.MyMenu;
import jagoclient.gui.MyPanel;
import jagoclient.gui.Panel3D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Menu;
import java.awt.MenuBar;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import rene.dialogs.Question;
import rene.gui.CloseFrame;
import rene.util.parser.StringParser;
import rene.viewer.SystemViewer;
import rene.viewer.Viewer;

class PartnerMove
{
	public String Type;
	public int P1, P2, P3, P4, P5, P6;

	public PartnerMove (String type, int p1, int p2, int p3, int p4, int p5,
		int p6)
	{
		Type = type;
		P1 = p1;
		P2 = p2;
		P3 = p3;
		P4 = p4;
		P5 = p5;
		P6 = p6;
	}

	public PartnerMove (String type, int p1, int p2, int p3, int p4)
	{
		Type = type;
		P1 = p1;
		P2 = p2;
		P3 = p3;
		P4 = p4;
	}
}


/**
 * The partner frame contains a simple chat dialog and a button to start a game
 * or restore an old game. This class contains an interpreters for the partner
 * commands.
 */

public class PartnerFrame extends CloseFrame
{
	private static final Logger LOG = Logger.getLogger(PartnerFrame.class.getName());
	BufferedReader In;
	PrintWriter Out;
	Viewer Output;
	HistoryTextField Input;
	Socket Server;
	Thread PartnerListenerThread;
	public PartnerGoFrame PGF;
	boolean Serving;
	boolean Block;
	List<PartnerMove> Moves;
	String Dir;

	public PartnerFrame (String name, boolean serving)
	{
		super(name);
		JPanel p = new MyPanel(new BorderLayout());
		Serving = serving;
		MenuBar menu = new MenuBar();
		setMenuBar(menu);
		Menu help = new MyMenu(Global.resourceString("Help"));
		help.add(new MenuItemAction(this, Global.resourceString("Partner_Connection")));
		menu.setHelpMenu(help);
		p.add("Center", Output = Global.getParameter("systemviewer", false) ? new SystemViewer() : new Viewer());
		Output.setFont(Global.Monospaced);
		p.add("South", Input = new HistoryTextField(this, "Input"));
		add("Center", p);
		JPanel p1 = new MyPanel();
		p1.add(new ButtonAction(this, Global.resourceString("Game")));
		p1.add(new ButtonAction(this, Global.resourceString("Restore_Game")));
		add("South", new Panel3D(p1));
		PGF = null;
		Block = false;
		Dir = "";
		Moves = new ArrayList<PartnerMove>();
		seticon("iconn.gif");
	}

	public boolean connect (String s, int p)
	{
		LOG.info("Starting partner connection");
		try
		{
			Server = new Socket(s, p);
			Out = new PrintWriter(
				new DataOutputStream(Server.getOutputStream()), true);
			In = new BufferedReader(new InputStreamReader(new DataInputStream(
				Server.getInputStream())));
		}
		catch (IOException ex)
		{
			LOG.log(Level.WARNING, null, ex);
			return false;
		}
		PartnerListenerThread = new Thread(new PartnerInputListener(In, Out, Input, Output, this));
		PartnerListenerThread.start();
		Out.println("@@name " + Global.getParameter("yourname", "No Name"));
		setVisible(true);
		return true;
	}

	public boolean connectvia (String server, int port, String relayserver, int relayport)
	{
		try
		{
			Server = new Socket(relayserver, relayport);
			Out = new PrintWriter(
				new DataOutputStream(Server.getOutputStream()), true);
			In = new BufferedReader(new InputStreamReader(new DataInputStream(
				Server.getInputStream())));
		}
		catch (Exception e)
		{
			return false;
		}
		Out.println(server);
		Out.println("" + port);
		PartnerListenerThread = new Thread(new PartnerInputListener(In, Out, Input, Output, this));
		PartnerListenerThread.start();
		Out.println("@@name " + Global.getParameter("yourname", "No Name"));
		setVisible(true);
		return true;
	}

	public void open (Socket server)
	{
		LOG.info("Starting partner server");
		Server = server;
		try
		{
			Out = new PrintWriter(
				new DataOutputStream(Server.getOutputStream()), true);
			In = new BufferedReader(new InputStreamReader(new DataInputStream(
				Server.getInputStream())));
		}
		catch (Exception e)
		{
			LOG.warning("---> no connection");
			new Message(this, Global.resourceString("Got_no_Connection_")).setVisible(true);
			return;
		}
		PartnerListenerThread = new Thread(new PartnerInputListener(In, Out, Input, Output, this));
		PartnerListenerThread.start();
	}

	@Override
	public void doAction (String o)
	{
		if ("Input".equals(o))
		{
			Out.println(Input.getText());
			Input.remember(Input.getText());
			Output.append(Input.getText() + "\n", Color.green.darker());
			Input.setText("");
		}
		else if (Global.resourceString("Game").equals(o))
		{
			new GameQuestion(this).setVisible(true);
		}
		else if (Global.resourceString("Restore_Game").equals(o))
		{
			if ( !Block)
			{
				if (PGF == null)
				{
					Out.println("@@restore");
					Block = true;
				}
				else
				{
					new Message(this, Global.resourceString("You_have_already_a_game_")).setVisible(true);
				}
			}

		}
		else if (Global.resourceString("Partner_Connection").equals(o))
		{
			try
			{
				new Help("partner").display();
			}
			catch (IOException ex)
			{
				new Message(Global.frame(), ex.getMessage()).setVisible(true);
			}
		}
		else super.doAction(o);
	}

	@Override
	public void doclose ()
	{
		Global.notewindow(this, "partner");
		Out.println("@@@@end");
		Out.close();
		new Thread(new CloseConnection(Server, In)).start();
		super.doclose();
	}

	@Override
	public boolean close ()
	{
		if (PartnerListenerThread.isAlive())
		{
			new ClosePartnerQuestion(this).setVisible(true);
			return false;
		}
		return true;
	}

	/**
	 * The interpreter for the partner commands (all start with @@).
	 */
	public void interpret (String s)
	{
		if (s.startsWith("@@name"))
		{
			StringParser p = new StringParser(s);
			p.skip("@@name");
			setTitle(Global.resourceString("Connection_to_") + p.upto('!'));
		}
		else if (s.startsWith("@@board"))
		{
			if (PGF != null) return;
			StringParser p = new StringParser(s);
			p.skip("@@board");
			String color = p.parseword();
			int C;
			if (color.equals("b"))
				C = 1;
			else C = -1;
			int Size = p.parseint();
			int TotalTime = p.parseint();
			int ExtraTime = p.parseint();
			int ExtraMoves = p.parseint();
			int Handicap = p.parseint();
			new BoardQuestion(this, Size, color, Handicap, TotalTime,
				ExtraTime, ExtraMoves);
		}
		else if (s.startsWith("@@!board"))
		{
			if (PGF != null) return;
			StringParser p = new StringParser(s);
			p.skip("@@!board");
			String color = p.parseword();
			int C;
			if (color.equals("b"))
				C = 1;
			else C = -1;
			int Size = p.parseint();
			int TotalTime = p.parseint();
			int ExtraTime = p.parseint();
			int ExtraMoves = p.parseint();
			int Handicap = p.parseint();
			PGF = new PartnerGoFrame(this, Global
				.resourceString("Partner_Game"), C, Size, TotalTime * 60,
				ExtraTime * 60, ExtraMoves, Handicap);
			Out.println("@@start");
			Block = false;
			Moves = new ArrayList<PartnerMove>();
			Moves.add(new PartnerMove("board", C, Size, TotalTime, ExtraTime, ExtraMoves, Handicap));
		}
		else if (s.startsWith("@@-board"))
		{
			new Message(this, Global
				.resourceString("Partner_declines_the_game_")).setVisible(true);
			Block = false;
		}
		else if (s.startsWith("@@start"))
		{
			if (PGF == null) return;
			PGF.start();
			Out.println("@@!start");
		}
		else if (s.startsWith("@@!start"))
		{
			if (PGF == null) return;
			PGF.start();
		}
		else if (s.startsWith("@@move"))
		{
			if (PGF == null) return;
			StringParser p = new StringParser(s);
			p.skip("@@move");
			String color = p.parseword();
			int i = p.parseint(), j = p.parseint();
			int bt = p.parseint(), bm = p.parseint();
			int wt = p.parseint(), wm = p.parseint();
			LOG.log(Level.INFO, "Move of {0} at {1},{2}", new Object[]{color, i, j});
			if (color.equals("b"))
			{
				if (PGF.maincolor() < 0) return;
				PGF.black(i, j);
				Moves.add(new PartnerMove("b", i, j, bt, bm, wt, wm));
			}
			else
			{
				if (PGF.maincolor() > 0) return;
				PGF.white(i, j);
				Moves.add(new PartnerMove("w", i, j, bt, bm, wt, wm));
			}
			PGF.settimes(bt, bm, wt, wm);
			Out.println("@@!move " + color + " " + i + " " + j + " " + bt + " "
				+ bm + " " + wt + " " + wm);
		}
		else if (s.startsWith("@@!move"))
		{
			if (PGF == null) return;
			StringParser p = new StringParser(s);
			p.skip("@@!move");
			String color = p.parseword();
			int i = p.parseint(), j = p.parseint();
			int bt = p.parseint(), bm = p.parseint();
			int wt = p.parseint(), wm = p.parseint();
			LOG.log(Level.INFO, "Move of {0} at {1},{2}", new Object[]{color, i, j});
			if (color.equals("b"))
			{
				if (PGF.maincolor() < 0) return;
				PGF.black(i, j);
				Moves.add(new PartnerMove("b", i, j, bt, bm, wt, wm));
			}
			else
			{
				if (PGF.maincolor() > 0) return;
				PGF.white(i, j);
				Moves.add(new PartnerMove("w", i, j, bt, bm, wt, wm));
			}
			PGF.settimes(bt, bm, wt, wm);
		}
		else if (s.startsWith("@@pass"))
		{
			if (PGF == null) return;
			StringParser p = new StringParser(s);
			p.skip("@@pass");
			int bt = p.parseint(), bm = p.parseint();
			int wt = p.parseint(), wm = p.parseint();
			LOG.info("Pass");
			PGF.dopass();
			PGF.settimes(bt, bm, wt, wm);
			Moves.add(new PartnerMove("pass", bt, bm, wt, wm));
			Out.println("@@!pass " + bt + " " + bm + " " + wt + " " + wm);
		}
		else if (s.startsWith("@@!pass"))
		{
			if (PGF == null) return;
			StringParser p = new StringParser(s);
			p.skip("@@!pass");
			int bt = p.parseint(), bm = p.parseint();
			int wt = p.parseint(), wm = p.parseint();
			LOG.info("Pass");
			PGF.dopass();
			Moves.add(new PartnerMove("pass", bt, bm, wt, wm));
			PGF.settimes(bt, bm, wt, wm);
		}
		else if (s.startsWith("@@endgame"))
		{
			if (PGF == null) return;
			new EndGameQuestion(this);
			Block = true;
		}
		else if (s.startsWith("@@!endgame"))
		{
			if (PGF == null) return;
			PGF.doscore();
			Block = false;
		}
		else if (s.startsWith("@@-endgame"))
		{
			if (PGF == null) return;
			new Message(this, "Partner declines game end!").setVisible(true);
			Block = false;
		}
		else if (s.startsWith("@@result"))
		{
			if (PGF == null) return;
			StringParser p = new StringParser(s);
			p.skip("@@result");
			int b = p.parseint();
			int w = p.parseint();
			LOG.log(Level.INFO, "Result {0} {1}", new Object[]{b, w});
			new ResultQuestion(this, Global.resourceString("Accept_result__B_")
				+ b + Global.resourceString("__W_") + w + "?", b, w);
			Block = true;
		}
		else if (s.startsWith("@@!result"))
		{
			if (PGF == null) return;
			StringParser p = new StringParser(s);
			p.skip("@@!result");
			int b = p.parseint();
			int w = p.parseint();
			LOG.info("Result " + b + " " + w);
			Output.append(Global.resourceString("Game_Result__B_") + b
				+ Global.resourceString("__W_") + w + "\n", Color.green
				.darker());
			new Message(this, Global.resourceString("Result__B_") + b
				+ Global.resourceString("__W_") + w + " was accepted!").setVisible(true);
			Block = false;
		}
		else if (s.startsWith("@@-result"))
		{
			if (PGF == null) return;
			new Message(this, Global.resourceString("Partner_declines_result_")).setVisible(true);
			Block = false;
		}
		else if (s.startsWith("@@undo"))
		{
			if (PGF == null) return;
			new UndoQuestion(this);
			Block = true;
		}
		else if (s.startsWith("@@-undo"))
		{
			if (PGF == null) return;
			new Message(this, Global.resourceString("Partner_declines_undo_")).setVisible(true);
			Block = false;
		}
		else if (s.startsWith("@@!undo"))
		{
			if (PGF == null) return;
			PGF.undo(2);
			Moves.remove(Moves.size()-1);
			Moves.remove(Moves.size()-1);
			PGF.addothertime(30);
			Block = false;
		}
		else if (s.startsWith("@@adjourn"))
		{
			adjourn();
		}
		else if (s.startsWith("@@restore"))
		{
			Question Q = new Question(this, Global
				.resourceString("Your_partner_wants_to_restore_a_game_"),
				Global.resourceString("Accept"), true);
			Q.setVisible(true);
			if (Q.result())
				acceptrestore();
			else declinerestore();
		}
		else if (s.startsWith("@@-restore"))
		{
			new Message(this, Global
				.resourceString("Partner_declines_restore_")).setVisible(true);
			Block = false;
		}
		else if (s.startsWith("@@!restore"))
		{
			dorestore();
			Block = false;
		}
		else if (s.startsWith("@@@"))
		{
			moveinterpret(s.substring(3), false);
		}
	}

	public boolean moveset (String c, int i, int j, int bt, int bm, int wt,
		int wm)
	{
		if (Block) return false;
		Out.println("@@move " + c + " " + i + " " + j + " " + bt + " " + bm
			+ " " + wt + " " + wm);
		return true;
	}

	public void out (String s)
	{
		Out.println(s);
	}

	public void refresh ()
	{}

	public void set (int i, int j)
	{}

	public void pass (int bt, int bm, int wt, int wm)
	{
		Out.println("@@pass " + bt + " " + bm + " " + wt + " " + wm);
	}

	public void endgame ()
	{
		if (Block) return;
		Block = true;
		Out.println("@@endgame");
	}

	public void doendgame ()
	{
		Out.println("@@!endgame");
		PGF.doscore();
		Block = false;
	}

	public void declineendgame ()
	{
		Out.println("@@-endgame");
		Block = false;
	}

	public void doresult (int b, int w)
	{
		Out.println("@@!result " + b + " " + w);
		Output.append(Global.resourceString("Game_Result__B_") + b
			+ Global.resourceString("__W_") + w + "\n", Color.green.darker());
		Block = false;
	}

	public void declineresult ()
	{
		Out.println("@@-result");
		Block = false;
	}

	public void undo ()
	{
		if (Block) return;
		Block = true;
		Out.println("@@undo");
	}

	public void doundo ()
	{
		Out.println("@@!undo");
		PGF.undo(2);
		Moves.remove(Moves.size()-1);
		Moves.remove(Moves.size()-1);
		Block = false;
		PGF.addtime(30);
	}

	public void declineundo ()
	{
		Out.println("@@-undo");
		Block = false;
	}

	public void dorequest (int s, String c, int h, int tt, int et, int em)
	{
		Out.println("@@board " + c + " " + s + " " + tt + " " + et + " " + em
			+ " " + h);
		Block = true;
	}

	public void doboard (int Size, String C, int Handicap, int TotalTime,
		int ExtraTime, int ExtraMoves)
	{
		PGF = new PartnerGoFrame(this, "Partner Game", C.equals("b")? -1:1,
			Size, TotalTime * 60, ExtraTime * 60, ExtraMoves, Handicap);
		if (C.equals("b"))
			Out.println("@@!board b" + " " + Size + " " + TotalTime + " "
				+ ExtraTime + " " + ExtraMoves + " " + Handicap);
		else Out.println("@@!board w" + " " + Size + " " + TotalTime + " "
			+ ExtraTime + " " + ExtraMoves + " " + Handicap);
		Moves = new ArrayList<PartnerMove>();
		Moves.add(new PartnerMove("board", C.equals("b")? -1:1, Size, TotalTime, ExtraTime, ExtraMoves, Handicap));
	}

	public void declineboard ()
	{
		Out.println("@@-board");
	}

	public void boardclosed (PartnerGoFrame pgf)
	{
		if (PGF == pgf)
		{
			Out.println("@@adjourn");
			savemoves();
		}
	}

	public void adjourn ()
	{
		new Message(this, Global
			.resourceString("Your_Partner_closed_the_board_")).setVisible(true);
		savemoves();
		PGF = null;
	}

	public void savemoves ()
	{
		Question Q = new Question(this, Global
			.resourceString("Save_this_game_for_reload_"), Global
			.resourceString("Yes"), true);
		Q.setVisible(true);
		if (Q.result()) dosave();
	}

	public void dosave ()
	{
		FileDialog fd = new FileDialog(this,
			Global.resourceString("Save_Game"), FileDialog.SAVE);
		if ( !Dir.equals("")) fd.setDirectory(Dir);
		fd.setFile("*.sto");
		fd.setVisible(true);
		String fn = fd.getFile();
		if (fn == null) return;
		Dir = fd.getDirectory();
		if (fn.endsWith(".*.*")) // Windows 95 JDK bug
		{
			fn = fn.substring(0, fn.length() - 4);
		}
		try
		// print out using the board class
		{
			PrintWriter fo = new PrintWriter(new FileOutputStream(fd.getDirectory() + fn), true);
			for (PartnerMove m : Moves)
			{
				fo.println(m.Type + " " + m.P1 + " " + m.P2 + " " + m.P3 + " "
					+ m.P4 + " " + m.P5 + " " + m.P6);
			}
			fo.close();
		}
		catch (IOException ex)
		{}
	}

	void acceptrestore ()
	{
		Out.println("@@!restore");
	}

	void declinerestore ()
	{
		Out.println("@@-restore");
	}

	void dorestore ()
	{
		if (PGF != null) return;
		FileDialog fd = new FileDialog(this,
			Global.resourceString("Load_Game"), FileDialog.LOAD);
		if ( !Dir.equals("")) fd.setDirectory(Dir);
		fd.setFile("*.sto");
		fd.setVisible(true);
		String fn = fd.getFile();
		if (fn == null) return;
		Dir = fd.getDirectory();
		if (fn.endsWith(".*.*")) // Windows 95 JDK bug
		{
			fn = fn.substring(0, fn.length() - 4);
		}
		try
		// print out using the board class
		{
			BufferedReader fi = new BufferedReader(
				new InputStreamReader(new DataInputStream(new FileInputStream(
					fd.getDirectory() + fn))));
			Moves = new ArrayList<PartnerMove>();
			while (true)
			{
				String s = fi.readLine();
				if (s == null) break;
				StringParser p = new StringParser(s);
				Out.println("@@@" + s);
				moveinterpret(s, true);
			}
			if (PGF != null) Out.println("@@start");
			fi.close();
		}
		catch (IOException ex)
		{}
	}

	void moveinterpret (String s, boolean fromhere)
	{
		StringParser p = new StringParser(s);
		String c = p.parseword();
		int p1 = p.parseint();
		int p2 = p.parseint();
		int p3 = p.parseint();
		int p4 = p.parseint();
		int p5 = p.parseint();
		int p6 = p.parseint();
		if (c.equals("board"))
		{
			PGF = new PartnerGoFrame(this, Global
				.resourceString("Partner_Game"), fromhere?p1: -p1, p2, p3 * 60,
				p4 * 60, p5, p6);
			PGF.setHandicap();
		}
		else if (PGF != null && c.equals("b"))
		{
			PGF.black(p1, p2);
			PGF.settimes(p3, p4, p5, p6);
		}
		else if (PGF != null && c.equals("w"))
		{
			PGF.white(p1, p2);
			PGF.settimes(p3, p4, p5, p6);
		}
		else if (PGF != null && c.equals("pass"))
		{
			PGF.pass();
			PGF.settimes(p1, p2, p3, p4);
		}
		Moves.add(new PartnerMove(c, p1, p2, p3, p4, p5, p6));
	}
}
