package jagoclient.partner;

import jagoclient.Dump;
import jagoclient.Global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JTextField;

import rene.viewer.Viewer;

/**
 * A thrad to expect input from a partner. The input is checked here for
 * commands (starting with @@).
 */

public class PartnerThread extends Thread
{
	BufferedReader In;
	PrintWriter Out;
	Viewer T;
	PartnerFrame PF;
	JTextField Input;

	public PartnerThread (BufferedReader in, PrintWriter out, JTextField input,
		Viewer t, PartnerFrame pf)
	{
		In = in;
		Out = out;
		T = t;
		PF = pf;
		Input = input;
	}

	@Override
	public void run ()
	{
		try
		{
			while (true)
			{
				String s = In.readLine();
				if (s == null || s.equals("@@@@end")) throw new IOException();
				Dump.println("From Partner: " + s);
				if (s.startsWith("@@busy"))
				{
					T.append(Global.resourceString("____Server_is_busy____"));
					return;
				}
				else if (s.startsWith("@@"))
					PF.interpret(s);
				else
				{
					T.append(s + "\n");
					Input.requestFocus();
				}
			}
		}
		catch (IOException e)
		{
			T.append(Global.resourceString("_____Connection_Error") + "\n");
		}
	}
}
