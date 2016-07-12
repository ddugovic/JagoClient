package rene.util.sound;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Panel;
import java.awt.Toolkit;

import rene.util.list.ListClass;
import rene.util.list.ListElement;

/**
This is a Sound class to play and store sounds from resources. The class
keeps a list of loaded sounds.
*/
public class SoundList extends ListClass<Sound> implements Runnable
{
	Thread T;
	boolean Busy;
	String Name,Queued;
	
	public SoundList ()
	{
		T=new Thread(this);
		T.start();
		try { Thread.sleep(0); } catch (Exception e) {}
	}
	public void run ()
	{	Busy=false;
		while (true)
		{	try 
			{	synchronized(this) { wait(); }
			}
			catch (Exception e)
			{	System.out.println(e);
			}
			Busy=true;
			while (Name!=null)
			{	playNow(Name);
				synchronized (this)
				{	Name=Queued;
					Queued=null;
				}
			}
			Busy=false;
		}
	}
	static synchronized public void beep ()
	{	Toolkit.getDefaultToolkit().beep();
	}
	public Sound find (String name)
	{
		for (ListElement<Sound> se : this)
		{	if (se.content().getName().equals(name))
			{	return se.content();
			}
		}
		return null;
	}
	public Sound append (String name)
	{	Sound e=new Sound(name);
		this.append(e);
		return e;
	}
	public void playNow (String name)
	{	Sound e=find(name);
		if (e==null) e=append(name);
		e.start();
	}
	public synchronized void play (String name)
	{	if (busy())
		{	synchronized(this) { Queued=name; }
			return;
		}
		Name=name;
		synchronized(this) { notify(); }
	}
	public boolean busy ()
	{	return Busy;
	}
	static SoundList L=new SoundList();
	static public void main (String args[])
	{	System.out.println("Java Version "+System.getProperty("java.version"));
		String Sounds[]={"high","message","click","stone","wip","yourmove","game"};
		rene.gui.CloseFrame F=new rene.gui.CloseFrame()
			{	public void doAction (String o)
				{
					L.play("/au/"+o+".wav");
				}
				public void doclose ()
				{	System.exit(0);
				}
			};
		F.setLayout(new BorderLayout());
		Panel p=new Panel();
		F.add("Center",p);
		for (int i=0; i<Sounds.length; i++)
		{	Button b=new Button(Sounds[i]);
			b.addActionListener(F);
			p.add(b);
		}
		F.pack();
		F.setVisible(true);
	}
}

