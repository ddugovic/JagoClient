package jagoclient.partner;

import jagoclient.Global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextField;

import rene.viewer.Viewer;

/**
 * A thread to expect input from a partner. The input is checked here for
 * commands (starting with @@).
 */

public class PartnerInputListener implements Runnable
{
	private static final Logger LOG = Logger.getLogger(PartnerInputListener.class.getName());
	BufferedReader In;
	PrintWriter Out;
	Viewer T;
	PartnerFrame PF;
	JTextField Input;

	public PartnerInputListener (BufferedReader in, PrintWriter out, JTextField input,
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
				LOG.log(Level.INFO, "From Partner: {0}", s);
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
