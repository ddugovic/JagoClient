package jagoclient.board;

/**
 * A timer for the goboard. It will call the alarm method of the board in
 * regular time intervals. This is used to update the timer.
 * 
 * @see jagoclient.board.TimedBoard
 */

public class GoTimer implements Runnable
{
	public long Interval;
	TimedBoard B;

	public GoTimer (TimedBoard b, long i)
	{
		Interval = i;
		B = b;
	}

	@Override
	public void run ()
	{
		try
		{
			while ( !Thread.interrupted())
			{
				Thread.sleep(Interval);
				B.alarm();
			}
		}
		catch (Exception e)
		{}
	}
}
