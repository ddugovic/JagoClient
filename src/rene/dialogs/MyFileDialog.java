package rene.dialogs;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Enumeration;

import rene.gui.ButtonAction;
import rene.gui.CloseDialog;
import rene.gui.CloseFrame;
import rene.gui.DoActionListener;
import rene.gui.Global;
import rene.gui.HistoryTextField;
import rene.gui.HistoryTextFieldChoice;
import rene.gui.MyLabel;
import rene.gui.MyPanel;
import rene.gui.Panel3D;
import rene.gui.TextFieldAction;
import rene.util.FileList;
import rene.util.FileName;
import rene.util.MyVector;
import rene.lister.*;

class DirFieldListener 
	implements DoActionListener
{	MyFileDialog T;
	
	public DirFieldListener (MyFileDialog t)
	{	T=t;
	}

	public void doAction (String o) 
	{	T.setFile(o);
	}

	public void itemAction(String o, boolean flag) {
	}
}

/**
This is a file dialog. It is easy to handle, remembers its position
and size, and performs pattern matching. The calls needs the
rene.viewer.* class, unless you replace Lister with List everywhere.
Moreover it needs the rene.gui.Global class to store field histories
and determine the background color. Finally, it uses the FileList
class to get the list of files.
<p>
The dialog does never check for files to exists. This must be done by
the application.
<p>
There is a static main method, which demonstrates everything.
*/

public class MyFileDialog extends CloseDialog
	implements ItemListener,FilenameFilter,MouseListener
{	//java.awt.List Dirs,Files;
	Lister Dirs,Files;
	HistoryTextField DirField,FileField,PatternField;
	HistoryTextFieldChoice DirHistory,FileHistory;
	TextField Chosen;
	String CurrentDir=".";
	boolean Aborted=true;
	String DirAppend="",PatternAppend="",FileAppend="";
	Button Home;
	Frame F;

	/**
	@param title The dialog title.
	@param action The button string for the main action (e.g. Load)
	@param saving True, if this is a saving dialog.
	*/	
	public MyFileDialog (Frame f, 
		String title, String action, boolean saving, boolean help)
	{	super(f,title,true);
		F=f;
		setLayout(new BorderLayout());
		
		// title prompt
		add("North",new Panel3D(Chosen=new TextFieldAction(this,"")));
		Chosen.setEditable(false);
		
		// center panels
		Panel center=new MyPanel();
		center.setLayout(new GridLayout(1,2,5,0));
		Dirs=new Lister();
		if (Global.NormalFont!=null) Dirs.setFont(Global.NormalFont);
		Dirs.addActionListener(this);
		Dirs.setMode(false,false,false,false);
		center.add(Dirs);
		Files=new Lister();
		if (Global.NormalFont!=null) Files.setFont(Global.NormalFont);
		Files.addActionListener(this);
		Files.setMode(false,false,true,false);
		center.add(Files);
		add("Center",new Panel3D(center));		
		
		// south panel
		Panel south=new MyPanel();
		south.setLayout(new BorderLayout());
		
		Panel px=new MyPanel();
		px.setLayout(new BorderLayout());
		
		Panel p0=new MyPanel();
		p0.setLayout(new GridLayout(0,1));
		
		Panel p1=new MyPanel();
		p1.setLayout(new BorderLayout());
		p1.add("North",linePanel(
				new MyLabel(Global.name("myfiledialog.dir")),
				DirField=
					new HistoryTextField(this,"Dir",32)
						{	public boolean filterHistory (String name)
							// avoid a Windows bug with C: instead of c:
							{	if (name.length()<2) return true;
								if (name.charAt(1)==':' && 
									Character.isUpperCase(name.charAt(0)))
										return false;
								return true;
							}
						}
				));
		DirField.setText(".");
		p1.add("South",linePanel(
				new MyLabel(Global.name("myfiledialog.olddirs","")),
				DirHistory=new HistoryTextFieldChoice(DirField)
				));
		p0.add(new Panel3D(p1));
		
		Panel p2=new MyPanel();
		p2.setLayout(new BorderLayout());
		p2.add("North",linePanel(
				new MyLabel(Global.name("myfiledialog.file")),
				FileField=new HistoryTextField(this,"File")
				));
		p2.add("South",linePanel(
				new MyLabel(Global.name("myfiledialog.oldfiles","")),
				FileHistory=new HistoryTextFieldChoice(FileField)
				));
		p0.add(new Panel3D(p2));
		
		px.add("Center",p0);
		
		px.add("South",new Panel3D(linePanel(
				new MyLabel(Global.name("myfiledialog.pattern")),
				PatternField=new HistoryTextField(this,"Pattern")
				)));
		PatternField.setText("*");
		
		south.add("Center",px);
		
		Panel buttons=new MyPanel();
		buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttons.add(Home=new ButtonAction(this,Global.name("myfiledialog.home","Home"),"Home"));
		buttons.add(new ButtonAction(this,Global.name("myfiledialog.mkdir","Create Directory"),"Create"));
		buttons.add(new ButtonAction(this,Global.name("myfiledialog.back","Back"),"Back"));
		buttons.add(new MyLabel(""));
		buttons.add(new ButtonAction(this,action,"Action"));
		buttons.add(new ButtonAction(this,Global.name("abort"),"Close"));
		if (help)
		{	addHelp(buttons,"filedialog");
		}
		
		south.add("South",buttons);
		
		add("South",new Panel3D(south));
		
		// set sizes
		pack();
		setSize("myfiledialog");
		addKeyListener(this);
		DirField.addKeyListener(this);
		DirField.setTrigger(true);
		FileHistory.setDoActionListener(new DirFieldListener(this));
		PatternField.addKeyListener(this);
		PatternField.setTrigger(true);
		FileField.addKeyListener(this);
		FileField.setTrigger(true);
		Home.addMouseListener(this);
	}
	
	Panel linePanel (Component x, Component y)
	{	Panel p=new MyPanel();
		p.setLayout(new GridLayout(1,0));
		p.add(x); p.add(y);
		return p;
	}
	
	public MyFileDialog (Frame f, 
		String title, String action, boolean saving)
	{	this(f,title,action,saving,false);
	}

	FileDialog FD;

	public MyFileDialog (Frame f,
		String title, boolean saving)
	{	super(f,"",true);
		FD=new FileDialog(f,title,
			saving?FileDialog.SAVE:FileDialog.LOAD);
	}

	boolean HomeShiftControl=false;

	public void mousePressed (MouseEvent e)
	{	HomeShiftControl = e.isShiftDown() && e.isControlDown();
	}
	public void mouseReleased (MouseEvent e) {}
	public void mouseClicked (MouseEvent e) {}
	public void mouseEntered (MouseEvent e) {}
	public void mouseExited (MouseEvent e) {}
	
	public void doAction (String o)
	{	if (o.equals("Dir") || o.equals("Pattern"))
		{	if (updateDir()) updateFiles();
			PatternField.remember(PatternField.getText());
		}
		else if (o.equals("File") || o.equals("Action"))
		{	if (FileField.getText().equals("")) return;
			leave();
		}
		else if (o.equals("Home"))
		{	if (HomeShiftControl)
			{	String s=Global.getParameter("myfiledialog.homedir","");
				if (s.equals(""))
					Global.setParameter("myfiledialog.homedir",DirField.getText());
				else
					Global.setParameter("myfiledialog.homedir","");				
			}
			try
			{	String s=Global.getParameter("myfiledialog.homedir","");
				if (s.equals(""))
				{	String s1=System.getProperty("user.home");
					String s2=Global.name("myfiledialog.windowshome","");
					String s3=Global.name("myfiledialog.homedir","");
					String s4=Global.name("Documents");
					String sep=System.getProperty("file.separator");
					if (new File(s1+sep+s4+sep+s3).exists())
						DirField.setText(s1+sep+s4+sep+s3);
					else if (new File(s1+sep+s2+sep+s3).exists())
						DirField.setText(s1+sep+s2+sep+s3);
					else if (new File(s1+sep+s4).exists())
						DirField.setText(s1+sep+s4);
					else if (new File(s1+sep+s2).exists())
						DirField.setText(s1+sep+s2);
					else if (new File(s1+sep+s3).exists())
						DirField.setText(s1+sep+s3);
					else
						DirField.setText(s1);
				}
				else DirField.setText(s);
				updateDir();
				updateFiles();
			}
			catch (Exception e) {}
		}
		else if (o.equals("Create"))
		{	try
			{	File f=new File(DirField.getText());
				if (!f.exists())
				{	f.mkdir();
				}
				updateDir();
				updateFiles();
			}
			catch (Exception e){}
		}
		else if (o.equals("Back"))
		{	String dir=getUndo();
			if (!dir.equals(""))
			{	DirField.setText(dir);
				updateDir();
				updateFiles();
			}
		}
		else super.doAction(o);
	}
	
	public void actionPerformed (ActionEvent e)
	{	if (e.getSource()==Dirs)
		{	String s=Dirs.getSelectedItem();
			if (s==null) return;
			if (s.equals("..")) dirup();
			else dirdown(s);
		}
		if (e.getSource()==Files)
		{	if (e instanceof ListerMouseEvent)
			{	ListerMouseEvent em=(ListerMouseEvent)e;
				if (em.clickCount()>=2) leave();
				else
				{	String s=Files.getSelectedItem();
					if (s!=null) FileField.setText(s);
				}
			}
		}
		else super.actionPerformed(e);
	}
	
	public void setFile (String s)
	{	DirField.setText(FileName.path(s));
		FileField.setText(FileName.filename(s));
		// System.out.println(s);
		update(false);
	}
	
	public void dirup ()
	{	DirField.setText(FileName.path(CurrentDir));
		if (DirField.getText().equals(""))
			DirField.setText(""+File.separatorChar);
		if (updateDir()) updateFiles();		
	}
	
	public void dirdown (String subdir)
	{	DirField.setText(CurrentDir+File.separatorChar+subdir);
		if (updateDir()) updateFiles();
	}
	
	/**
	Leave the dialog and remember settings.
	*/
	void leave ()
	{	if (FD!=null) return;
		if (!FileField.getText().equals("")) Aborted=false;
		if (!Aborted)
		{	noteSize("myfiledialog");
			DirField.remember(DirField.getText());
			DirField.saveHistory("myfiledialog.dir.history"+DirAppend);
			PatternField.saveHistory("myfiledialog.pattern.history"+PatternAppend);
			FileField.remember(getFilePath());
			FileField.saveHistory("myfiledialog.file.history"+FileAppend);
		}
		doclose();
	}

	/**
	Update the directory list.
	@return if the current content of DirField is indeed a directory.
	*/
	public boolean updateDir ()
	{	if (FD!=null) return true;
		File dir=new File(DirField.getText()+File.separatorChar);
		if (!dir.isDirectory()) return false;
		try
		{	String s=FileName.canonical(dir.getCanonicalPath());
			addUndo(s);
			DirField.setText(s);
			Chosen.setText(
				FileName.chop(16,
				DirField.getText()+File.separatorChar+
					PatternField.getText(),48));
		}
		catch (Exception e) { return false; }
		return true;
	}
	
	MyVector Undo=new MyVector();
	
	/**
	Note the directory in a history list.
	*/
	public void addUndo (String dir)
	{	if (Undo.size()>0 && 
			((String)Undo.elementAt(Undo.size()-1)).equals(dir)) return;
		Undo.addElement(dir);
	}
	
	/**
	Get the undo directory and remove it.
	*/
	public String getUndo ()
	{	if (Undo.size()<2) return "";
		String s=(String)Undo.elementAt(Undo.size()-2);
		Undo.truncate(Undo.size()-1);
		return s;
	}
	
	/**
	Update the file list.
	*/
	public void updateFiles ()
	{	if (FD!=null) return;
		File dir=new File(DirField.getText());
		if (!dir.isDirectory()) return;
		CurrentDir=DirField.getText();
		if (PatternField.getText().equals("")) PatternField.setText("*");
		try
		{	Files.clear();
			Dirs.clear();
			FileList l=new FileList(DirField.getText(),
				PatternField.getText(),false);
			l.setCase(Global.getParameter("filedialog.usecaps",false));
			l.search();
			l.sort();
			Enumeration e=l.files();
			while (e.hasMoreElements())
			{	File f=(File)e.nextElement();
				Files.addElement(FileName.filename(f.getCanonicalPath()));
			}
			Dirs.addElement("..");
			e=l.dirs();
			while (e.hasMoreElements())
			{	File f=(File)e.nextElement();
				Dirs.addElement(FileName.filename(f.getCanonicalPath()));
			}
		}
		catch (Exception e) {}
		Dirs.updateDisplay();
		Files.updateDisplay();
		Files.requestFocus();
	}
	
	public void setDirectory (String dir)
	{	if (FD!=null) FD.setDirectory(dir);
		else DirField.setText(dir);
	}
	
	public void setPattern (String pattern)
	{	if (FD!=null)
		{	FD.setFilenameFilter(this); // does not work
			String s=pattern.replace(' ',';');
			FD.setFile(s);
		}
		else PatternField.setText(pattern);
	}
	
	public void setFilePath (String file)
	{	if (FD!=null)
		{	FD.setFile(file);
			return;
		}
		String dir=FileName.path(file);
		if (!dir.equals(""))
		{	DirField.setText(dir);
			FileField.setText(FileName.filename(file));
		}
		else FileField.setText(file);
	}
	
	/**
	Check, if the dialog was aborted.
	*/
	public boolean isAborted ()
	{	if (FD!=null) return FD.getFile()==null || FD.getFile().equals("");
		else return Aborted;
	}
	
	/**
	@return The file plus its path.
	*/
	public String getFilePath ()
	{	if (FD!=null) 
		{	if (FD.getFile()!=null) return FD.getDirectory()+FD.getFile();
			else return "";
		}
		String file=FileField.getText();
		if (!FileName.path(file).equals("")) return file;
		else return CurrentDir+File.separatorChar+FileField.getText();
	}

	/**
	This should be called at the start.
	*/	
	public void update (boolean recent)
	{	if (FD!=null) return;
		loadHistories(recent);
		setFilePath(FileField.getText());
		if (updateDir()) updateFiles();
		Aborted=true;
	}
	public void update ()
	{	update(true);	
	}
	
	public void setVisible (boolean flag)
	{	if (FD!=null) FD.setVisible(flag);
		else super.setVisible(flag);
	}
	
	public void center (Frame f)
	{	if (FD!=null) CloseDialog.center(f,FD);
		else super.center(f);
	}

	public static void main (String args[])
	{	Frame f=new CloseFrame()
		{	public void doclose ()
			{	System.exit(0);
			}
		};
		f.setSize(500,500);
		f.setLocation(400,400);
		f.setVisible(true);
		MyFileDialog d=new MyFileDialog(f,"Title","Save",false);
		d.center(f);
		d.update();
		d.setVisible(true);
	}

	public void focusGained (FocusEvent e)
	{	FileField.requestFocus();
	}
	
	/**
	 * Can be overwritten by instances to accept only some files.
	 */
	public boolean accept (File dir, String file)
	{	return true;
	}
	
	public void loadHistories (boolean recent)
	{	if (FD!=null) return;
		DirField.loadHistory("myfiledialog.dir.history"+DirAppend);
		DirHistory.update();
		if (recent) setDirectory(DirHistory.getRecent());
		if (updateDir()) updateFiles();		
		PatternField.loadHistory("myfiledialog.pattern.history"+PatternAppend);
		FileField.loadHistory("myfiledialog.file.history"+FileAppend);
		FileHistory.update();
	}
	public void loadHistories ()
	{	loadHistories(true);
	}

	/**
	 * Loads the histories from the configuration file. If you want a
	 * unique history for your instance, you need to give a string unique
	 * for your instance. There are three types of histories.
	 * @param dir
	 * @param pattern
	 * @param file
	 * @see loadHistories
	 */
	public void loadHistories (String dir, String pattern, String file)
	{	setAppend(dir,pattern,file);
		loadHistories();
	}
	
	/**
	 * Histories are used for the directories, the files and the patterns.
	 * The dialog can use different histories for each instance of this class.
	 * If you want that, you need to determine the history for the instance
	 * with a string, unique for the instance. If a string is empty,
	 * "default" is used.
	 * @param dir
	 * @param pattern
	 * @param file
	 */
	public void setAppend (String dir, String pattern, String file)
	{	if (FD!=null) return;
		if (!dir.equals("")) DirAppend="."+dir;
		else DirAppend=".default";
		if (!pattern.equals("")) PatternAppend="."+pattern;
		else PatternAppend=".default";
		if (!file.equals("")) FileAppend="."+file;
		else FileAppend=".default";
	}

	public void itemStateChanged(ItemEvent e) {
	}
}
