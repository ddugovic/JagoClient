/*
 * Created on 01.11.2004
 *
 */
package rene.dialogs;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import rene.gui.ButtonAction;
import rene.gui.CloseDialog;
import rene.gui.Global;
import rene.gui.MyChoice;
import rene.gui.MyLabel;
import rene.gui.MyPanel;
import rene.gui.Panel3D;
import rene.viewer.ExtendedViewer;

/**
 * Info class. Reads a file "info.txt" or "de_info.txt" etc. in its Base
 * directory. This file has the structure
 * 
 * .subject1 substitute1 substitute2 ... .related subject subject ... Header ...
 * 
 * .subject2 ...
 * 
 * and displays the text, starting from header, searching for a subject or any
 * of its substitutes. The headers of the related subjects are presented in a
 * choice list. The user can switch to any of it.
 * 
 * There is a history and a back button.
 * 
 * Moroever, there is a search button, that displays the first subject
 * containing a string and presents all other subjects containing the string in
 * the choice list.
 * 
 * This class is used in CloseDialog to provide help in dialogs. It is a
 * CloseDialog itself.
 * 
 * @see: rene.gui.CloseDialog
 */

public class InfoDialog extends CloseDialog implements ItemListener
{
	ExtendedViewer V;
	Button Close, Back;
	public static String Subject = "start";
	String Search = null;

	MyChoice L;
	Vector Other = null;
	Vector History = new Vector();
	public static String Base = ""; // path to your info.txt

	Frame F;

	/**
	 * Sets up the dialog with title, search bar and text area. The size will be
	 * remembered with "info" in the configuration (see: Global). The dialog
	 * will be centered on its frame.
	 * 
	 * @param f
	 */
	public InfoDialog (Frame f)
	{
		super(f, Global.name("info.title"), true);
		F = f;
		V = new ExtendedViewer();
		if (Global.Background != null) V.setBackground(Global.Background);
		V.setFont(Global.NormalFont);
		setLayout(new BorderLayout());
		Panel north = new MyPanel();
		north.setLayout(new GridLayout(0, 2));
		north.add(new MyLabel(Global.name("info.related")));
		L = new MyChoice();
		fill();
		north.add(L);
		add("North", north);
		add("Center", V);
		Panel p = new MyPanel();
		p.add(new ButtonAction(this, Global.name("info.start"), "Start"));
		p.add(new ButtonAction(this, Global.name("info.search"), "Search"));
		p.add(new ButtonAction(this, Global.name("info.back"), "Back"));
		p.add(new MyLabel(""));
		p.add(new ButtonAction(this, Global.name("close", "Close"), "Close"));
		add("South", new Panel3D(p));
		L.addItemListener(this);
		pack();
		setSize("info");
		center(f);
		setVisible(true);
	}

	/**
	 * A complicated function, that scans the current help file for topics,
	 * related to the current subject. The proper language (e.g. "de_") are read
	 * from the language property in Global. E.g., the file file is read from
	 * "de_info.txt". The proper encoding of the file (e.g. "CP1552) is read
	 * from the "codepage.help" property in Global.
	 */
	public void fill (String language)
	{
		L.removeAll();
		V.setText("");
		V.setVisible(false);
		boolean Found = false, Appending = false;
		Vector Related = null;
		Other = new Vector();
		String pair[] = null, lastpair[] = null;
		String lang = language;
		Vector SearchResults = new Vector();
		String SearchResult = "";
		String FoundTopic = null;
		boolean FirstRun = true, FoundHeader = false;

		String Search1 = Search;
		if (Search != null && Search.length() > 0)
		{
			Search1 = Search.substring(0, 1).toUpperCase()
					+ Search.substring(1);
		}

		read: while (true)
		{
			try
			{ // open the info file in the proper language and encoding.
				String cp = Global.name("codepage.help", ""); // get encoding.
				BufferedReader in = null;
				// System.out.println("Try "+Base+lang+"info.txt");
				if (cp.equals(""))
					in = new BufferedReader(new InputStreamReader(getClass()
							.getResourceAsStream(Base + lang + "info.txt")));
				else
				{
					try
					{
						in = new BufferedReader(new InputStreamReader(
								getClass().getResourceAsStream(
										Base + lang + "info.txt"), cp));
					}
					catch (Exception ex)
					{
						in = new BufferedReader(new InputStreamReader(
								getClass().getResourceAsStream(
										Base + lang + "info.txt")));
					}
				}
				// System.out.println("Opened "+Base+lang+"info.txt");
				// read through the file line by line:
				newline: while (true)
				{
					String s = in.readLine();
					if (s == null) break newline;
					s = clear(s);
					if ( !s.startsWith(".")
							&& Search != null
							&& (s.indexOf(Search) >= 0 || s.indexOf(Search1) >= 0))
					{
						if (lastpair != null && pair == null
								&& !SearchResult.equals(lastpair[0]))
						{
							SearchResults.addElement(lastpair);
							SearchResult = lastpair[0];
							if (FoundTopic == null) FoundTopic = lastpair[0];
						}
					}
					interpret: while (true)
					{
						if ( !Appending && s.startsWith(".")
								&& !s.startsWith(".related"))
						{
							if ( !Found)
							{
								if (s.startsWith("." + Subject))
								{
									Found = true;
									Appending = true;
									continue newline;
								}
								StringTokenizer t = new StringTokenizer(s);
								while (t.hasMoreElements())
								{
									String name = t.nextToken();
									if (name.equals(Subject))
									{
										Found = true;
										Appending = true;
										continue newline;
									}
								}
							}
							pair = new String[2];
							s = s.substring(1);
							int n = s.indexOf(' ');
							if (n > 0) s = s.substring(0, n);
							pair[0] = s;
							continue newline;
						}
						if (Appending)
						{
							if (s.startsWith(".related"))
							{
								s = s.substring(".related".length());
								Related = new Vector();
								StringTokenizer t = new StringTokenizer(s);
								while (t.hasMoreElements())
								{
									Related.addElement(t.nextToken());
								}
								continue newline;
							}
							if (s.startsWith("."))
							{
								Appending = false;
								continue interpret;
							}
							if (s.trim().equals(""))
							{
								V.newLine();
								V.appendLine("");
							}
							else
							{
								if (s.startsWith(" ")) V.newLine();
								V.append(s + " ");
							}
						}
						else if (pair != null && !s.startsWith("."))
						{
							pair[1] = s;
							Other.addElement(pair);
							lastpair = pair;
							pair = null;
							if (Search != null
									&& (s.indexOf(Search) >= 0 || s
											.indexOf(Search1) >= 0))
							{
								if ( !SearchResult.equals(lastpair[0]))
								{
									SearchResults.addElement(lastpair);
									SearchResult = lastpair[0];
									if ( !FoundHeader)
										FoundTopic = lastpair[0];
									FoundHeader = true;
								}
							}
						}
						continue newline;
					}
				}
				V.newLine();
				in.close();
			}
			catch (Exception e)
			{
				if ( !lang.equals(""))
				{
					lang = "";
					continue read;
				}
				else
				{
					V.appendLine(Global.name("help.error",
							"Could not find the help file!"));
				}
			}
			if (FoundTopic != null && FirstRun)
			{
				Subject = FoundTopic;
				SearchResults = new Vector();
				SearchResult = "";
				pair = null;
				lastpair = null;
				Found = false;
				V.setText("");
				FirstRun = false;
				continue read;
			}
			else if ( !Found && !lang.equals(""))
			{
				lang = "";
				continue read;
			}
			else break read;
		}

		if (Search != null)
		{
			if (SearchResults.size() > 0)
				L.add(Global.name("info.searchresults"));
			else L.add(Global.name("info.noresults"));
		}
		else L.add(Global.name("info.select"));

		if (Search == null && Related != null)
		{
			Enumeration e = Related.elements();
			while (e.hasMoreElements())
			{
				String topic = (String)e.nextElement();
				Enumeration ev = Other.elements();
				while (ev.hasMoreElements())
				{
					String s[] = (String[])ev.nextElement();
					if (s[0].equals(topic))
					{
						L.add(s[1]);
						break;
					}
				}
			}
		}

		if (Search != null)
		{
			Enumeration e = SearchResults.elements();
			while (e.hasMoreElements())
			{
				String s[] = (String[])e.nextElement();
				L.add(s[1]);
			}
		}

		History.addElement(Subject);
		V.update();
		V.setVisible(true);
		V.showFirst();
	}

	public String clear (String s)
	{
		s = s.replace('§', ' ');
		s = s.replaceAll("__", "");
		return s;
	}

	public void fill ()
	{
		fill(Global.name("language", ""));
	}

	@Override
	public void doAction (String o)
	{
		if (o.equals("Close"))
		{
			super.doAction("Close");
		}
		else if (o.equals("Back"))
		{
			int n = History.size();
			if (n < 2) return;
			History.removeElementAt(n - 1);
			Subject = (String)History.elementAt(n - 2);
			History.removeElementAt(n - 2);
			fill();
		}
		else if (o.equals("Start"))
		{
			Subject = "start";
			fill();
		}
		else if (o.equals("Search"))
		{
			GetParameter.InputLength = 50;
			GetParameter g = new GetParameter(F, Global.name("info.title"),
					Global.name("info.search"), Global
							.name("info.search", "ok"));
			g.center(F);
			g.setVisible(true);
			if ( !g.aborted()) Search = g.getResult();
			fill();
			Search = null;
		}
		else super.doAction(o);
	}

	public void itemStateChanged (ItemEvent e)
	{
		String s = L.getSelectedItem();
		Enumeration ev = Other.elements();
		while (ev.hasMoreElements())
		{
			String p[] = (String[])ev.nextElement();
			if (p[1].equals(s))
			{
				Subject = p[0];
				fill();
				break;
			}
		}
	}

	@Override
	public void doclose ()
	{
		noteSize("info");
		super.doclose();
	}
}
