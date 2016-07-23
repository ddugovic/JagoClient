package jagoclient.gmp;

import jagoclient.Global;
import jagoclient.board.*;
import jagoclient.dialogs.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GMPGoFrame extends ConnectedGoFrame implements TimedBoard
{	private static final Logger LOG = Logger.getLogger(GMPGoFrame.class.getName());
	GMPConnection C;
	boolean WantMove=true;
	int BlackTime=0,WhiteTime=0;
	long CurrentTime;
	Thread Timer=null;
	int MyColor;
	
	public GMPGoFrame (GMPConnection c, int size, int color)
	{	super(Global.resourceString("Play_Go"),size,
			Global.resourceString("Remove_groups"),Global.resourceString(""),false,false);
		MyColor=color;
		C=c;
		Timer=new Thread(new GoTimer(this,1000));
		CurrentTime=System.currentTimeMillis();
		setVisible(true);
		repaint();
	}

	@Override
	public boolean wantsmove ()
	{	return WantMove;
	}
	
	public void gotMove (int color, int i, int j)
	{	synchronized (B)
		{	if (B.maincolor()==color) return;
			updateTime();
			if (color==GMPConnector.WHITE) white(i,j);
			else black(i,j);
			B.showinformation();
			B.copy();
		}
	}
	
	public void gotSet (int color, int i, int j)
	{	updateTime();
		if (color==GMPConnector.WHITE) setwhite(i,j);
		else setblack(i,j);
	}
	
	public void gotPass (int color)
	{	updateTime();
		LOG.info("Opponent passed");
		B.setpass();
		new Message(this,Global.resourceString("Pass")).setVisible(true);
	}

	@Override
	public void notepass ()
	{	if (B.maincolor()==MyColor) return;
		updateTime();
		LOG.info("I pass");
		C.pass();
		B.setpass();
	}

	@Override
	public boolean moveset (int i, int j)
	{	if (B.maincolor()==MyColor) return false;
		updateTime();
		LOG.log(Level.INFO, "Move at {0} {1}", new Object[]{i, j});
		C.moveset(i,j);
		updateTime();
		return true;
	}

	@Override
	public void color (int c)
	{	if (c==GMPConnector.WHITE) super.color(-1);
		else super.color(1);
	}

	@Override
	public void undo ()
	{	if (B.maincolor()==MyColor) return;
		C.undo();
		doundo(2);
	}

	public void doundo (int n)
	{	B.undo(n);
	}

	@Override
	public void doAction (String o)	
	{	if (Global.resourceString("Remove_groups").equals(o))
		{	WantMove=false;
			B.score();
		}
		else super.doAction(o);
	}

	@Override
	public void doclose ()
	{	C.doclose();
		if (Timer!=null && Timer.isAlive()) Timer.interrupt();
		super.doclose();
	}

	@Override
	public void alarm ()
	{	long now=System.currentTimeMillis();
		int BlackRun=BlackTime;
		int WhiteRun=WhiteTime;
		if (B.maincolor()>0)
		{	BlackRun+=(int)((now-CurrentTime)/1000);
		}
		else
		{	WhiteRun+=(int)((now-CurrentTime)/1000);
		}
		if (BigTimer)
		{	BL.setTime(WhiteRun,BlackRun,0,0,0);
			BL.repaint();
		}
	}

	public void updateTime ()
	{	long now=System.currentTimeMillis();
		if (B.maincolor()>0)
		{	BlackTime+=(int)((now-CurrentTime)/1000);
		}
		else
		{	WhiteTime+=(int)((now-CurrentTime)/1000);
		}
		CurrentTime=now;
		alarm();
	}

}
