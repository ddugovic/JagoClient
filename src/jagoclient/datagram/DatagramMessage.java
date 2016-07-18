package jagoclient.datagram;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
This class may be used to send a datagram to some internet
address or to receive datagrams from some internet address.
The datagram contains text lines, which are converted to
and from byte arrays.
*/

public class DatagramMessage 
{	byte B[];
	final int MAX=4096;
	int Used;
	public DatagramMessage ()
	{	B=new byte[MAX];
		Used=0;
	}
	/**
	Add a new line to the datagram.
	*/
	public void add (String s)
	{	try
		{	byte B1[]=s.getBytes();
		    for (int i=0; i<s.length(); i++) B[Used+i]=B1[i];
			Used+=s.length();
			B[Used]=0; Used++;
		}
		catch (Exception ex)
		{
			Logger.getLogger(DatagramMessage.class.getName()).log(Level.WARNING, null, ex);
		}
	}
	/**
	Send the datagram to the specified address and port.
	*/
	public void send (String adr, int port)
	{	if (Used==0) return;
		try
		{	InetAddress ia=InetAddress.getByName(adr);
			DatagramPacket dp=new DatagramPacket(B,Used,ia,port);
			DatagramSocket ds=new DatagramSocket();
			ds.send(dp);
			ds.close();
		}
		catch (Exception ex)
		{
			Logger.getLogger(DatagramMessage.class.getName()).log(Level.WARNING, null, ex);
		}
	}
	/**
	@return a vector of lines, containing the received datagram.
	*/
	public Vector<String> receive (int port)
	{	Vector<String> v=new Vector<String>();
		try
		{	DatagramPacket dp=new DatagramPacket(B,MAX);
			DatagramSocket ds=new DatagramSocket(port);
			ds.receive(dp);
			int l=dp.getLength(),i=0;
			while (i<l)
			{	int j=i; while (B[i]!=0) i++;
				if (i>j) v.addElement(new String(B,j,i-j));
				else v.addElement("");
				i++;
			}
			ds.close();
		}
		catch (Exception ex)
		{
			Logger.getLogger(DatagramMessage.class.getName()).log(Level.WARNING, null, ex);
		}
		return v;
	}
}
