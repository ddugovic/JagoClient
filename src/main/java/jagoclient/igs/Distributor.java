package jagoclient.igs;


/**
This class takes messages from IgsStream and handles it.
Most of the time, it has a client to send the message to.
Sometimes it will open a new client window.
<P>
The distributor has a number N, which is the command number it
is waiting for. It has a second number G, which is used to store
additional information. E.g. PlayDistributor will store the
game number there. IgsStream can ask the distributor for these
numbers and determine, if it should send the message to this
distributor.
@see jagoclient.igs.IgsStream
*/

public abstract class Distributor
{	int N; // number to expect
	int G; // game number, if applicable
	SizeTask ST; // single-input distributor
	Task T; // single-input distributor
	IgsStream In;
	public boolean Playing;
	public Distributor (IgsStream in, int n, int game, SizeTask sizetask, Task task)
	{	N=n; G=game;
		in.append(this);
		ST=sizetask;
		T=task;
		In=in;
		Playing=false;
	}
	public final int number () { return N; }
	public final int game () { return G; }
	public void game (int n) {}
	public final SizeTask sizetask () { return ST; }
	public final Task task () { return T; }
	public abstract void send (String s);
	public void unchain ()
	{	In.unchain(this);
	}
	public void remove () {} // dispose client (called from IgsStream at connection end)
	public boolean blocked () { return false; }
	public boolean wantsmove () { return Playing; }
	public void set (int i, int j, int sec) {}
	public void pass () {}
	public void out (String s) { In.out(s); }
	public void allsended () {}
	public void refresh() {}
	public boolean started () { return false; }
	public boolean newmove () { return false; }

	public interface SizeTask
	{
		public void sizefinished ();
	}
	public interface Task
	{
		public void finished ();
	}
}

