package rene.util.mail;

import java.io.*;
import java.net.*;

/**
This mail class sends a message via SMTP. For an applet, the smtp
server must be the web server for security reasons.
*/

public class SendMail implements Runnable
{   public String result = "";
    String lastline;
	BufferedReader in;
	String Mailhost, From, To;

	/**
	@param smtp The server name.
	@param to The email address of the recipient
	@param from The email address of the sender
	*/
    public SendMail (String smtp, String to, String from)
	{	Mailhost=smtp;
		From=from;
		To=to;
    }

	/**
	Send a message. The Mailcall back is called, when the message is
	delivered, or if they are problems.
	@param subject The message subject.
	@param message The message body.
	@param cb Anthing, that implements the MailCallback interface.
	*/ 
	public void send (String subject, String message, MailCallback cb) 
	{	Subject=subject; Message=message; CB=cb;
    	new Thread(this).start();
    }
    
	public void expect (String expected, String msg) throws Exception
    {	lastline = in.readLine();
        if (!lastline.startsWith(expected))
			throw new Exception(msg + ":" + lastline);
		while (lastline.startsWith(expected + "-")) lastline = in.readLine();
    }
    
    String Subject,Message;
    MailCallback CB;

    public void run ()
    {	Socket s = null;
        try
		{	String res;
	    	s = new Socket(Mailhost, 25);
	    	PrintStream p =
	    		new PrintStream(s.getOutputStream(),true);
	    	in = new BufferedReader(new InputStreamReader(s.getInputStream()));
	    	expect("220", "greetings");
	    	p.println("HELO " + "helohost");
	    	expect("250", "helo");
	    	int pos;
		    p.println("MAIL FROM: " + From);
		    expect("250", "mail from");
	    	p.println("RCPT TO: " + To);
		    expect("250", "rcpt to");
		    p.println("DATA");
	    	expect("354", "data");
		    p.println("Subject: " + Subject);
	    	BufferedReader is = new BufferedReader(new StringReader(Message));
		    try 
			{	while (true)
				{	String ln = is.readLine();
					if (ln==null) break;
					if (ln.equals("."))
				    ln = "..";
			    	p.println(ln);
			    }
		    }
		    catch (Exception e) {}
			p.println("");
		    p.println(".");
	    	expect("250","end of data");
	    	p.println("QUIT");
		    expect("221", "quit");
		} 
		catch(Exception e)
		{	result = e.getMessage();
			CB.result(false,"Send error!");
    		return;
		}
		finally
		{	try 
			{	if (s != null) s.close();
   			} 
			catch(Exception e)
			{	result = e.getMessage();
			}
		}
		CB.result(true,"Mail sent successfully!");
    }
}
