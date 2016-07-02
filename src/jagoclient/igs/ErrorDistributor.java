package jagoclient.igs;

import jagoclient.Global;
import jagoclient.dialogs.Message;

import java.awt.Color;
import java.io.PrintWriter;

/**
This distributor receives and handles error messages from the server.
It will always open a new dialog box (a Message) to display the error.
@see jagoclient.igs.Message
*/

public class ErrorDistributor extends Distributor
{	ConnectionFrame CF;
	PrintWriter Out;
	String S;
	public ErrorDistributor
		(ConnectionFrame cf, IgsStream in, PrintWriter out)
	{	super(in,5,0,false);
		CF=cf; Out=out;
		S=new String("");
	}
	public void send (String C)
	{	if (S.equals("")) S=S+C;
		else S=S+"\n"+C;
	}
	public void allsended ()
	{	if (Global.blocks(S)==0 && CF.wantserrors()) 
			new Message(CF,Global.resourceString("Error:\n")+S);
		CF.append("Error\n"+S,Color.red.darker());
		S="";
	}
}

