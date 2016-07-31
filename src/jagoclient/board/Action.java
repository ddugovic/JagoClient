package jagoclient.board;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rene.util.parser.StringParser;
import rene.util.xml.XmlWriter;

/** 
Has a type and arguments (as in SGF, e.g. B[ih] of type "B" and
Methods include the printing on a PrintWriter.
*/
public class Action
{	String Type; // the type
	List<String> Arguments; // the list of argument strings
	
	/**
	Initialize with type only
	*/
	public Action (String s)
	{	Type=s;
		Arguments=new ArrayList<String>();
	}

	/**
	Initialize with type and one argument to that type tag.
	*/
	public Action (String s, String arg)
	{	Type=s;
		Arguments=new ArrayList<String>();
		addargument(arg);
	}
	
	public void addargument (String s)
	// add an argument ot the list (at end)
	{	Arguments.add(s);
	}
	
	public void toggleargument (String s)
	// add an argument ot the list (at end)
	{
		Arguments.remove(s);
		Arguments.add(s);
	}

	/** Find an argument */	
	public boolean contains (String s)
	{
		return Arguments.contains(s);
	}
	
	public void print (PrintWriter o)
	// print the action
	{	if (Arguments.isEmpty() || (Arguments.size()==1 && Arguments.get(0).isEmpty()))
			return;
		o.println();
		o.print(Type);
		for (String s : Arguments)
		{	o.print("[");
			StringParser p=new StringParser(s);
			Vector v=p.wrapwords(60);
			for (int i=0; i<v.size(); i++)
			{	s=(String)v.elementAt(i);
				if (i>0) o.println();
				int k=s.indexOf(']');
				while (k>=0)
				{	if (k>0) o.print(s.substring(0,k));
					o.print("\\]");
					s=s.substring(k+1);
					k=s.indexOf(']');
				}
				o.print(s);
			}
			o.print("]");
		}
	}
	
	/**
	Print the node content in XML form.
	*/
	public void print (XmlWriter xml, int size, int number)
	{	if (Type.equals("C"))
		{	xml.startTagNewLine("Comment");
			printTextArgument(xml);
			xml.endTagNewLine("Comment");
		}
		else if (Type.equals("GN") 
			|| Type.equals("AP")
			|| Type.equals("FF")
			|| Type.equals("GM")
			|| Type.equals("N")
			|| Type.equals("SZ")
			|| Type.equals("PB")
			|| Type.equals("BR")
			|| Type.equals("PW")
			|| Type.equals("WR")
			|| Type.equals("HA")
			|| Type.equals("KM")
			|| Type.equals("RE")
			|| Type.equals("DT")
			|| Type.equals("TM")
			|| Type.equals("US")
			|| Type.equals("WL")
			|| Type.equals("BL")
			|| Type.equals("CP")
			)
		{
		}
		else if (Type.equals("B"))
		{	xml.startTagStart("Black");
			xml.printArg("number",""+number);
			xml.printArg("at",getXMLMove(size));
			xml.finishTagNewLine();
		}
		else if (Type.equals("W"))
		{	xml.startTagStart("White");
			xml.printArg("number",""+number);
			xml.printArg("at",getXMLMove(size));
			xml.finishTagNewLine();
		}
		else if (Type.equals("AB"))
		{	printAllFields(xml,size,"AddBlack");
		}
		else if (Type.equals("AW"))
		{	printAllFields(xml,size,"AddWhite");
		}
		else if (Type.equals("AE"))
		{	printAllFields(xml,size,"Delete");
		}
		else if (Type.equals(Field.Marker.CROSS.value))
		{	printAllFields(xml,size,"Mark");
		}
		else if (Type.equals("M"))
		{	printAllFields(xml,size,"Mark");
		}
		else if (Type.equals(Field.Marker.SQUARE.value))
		{	printAllFields(xml,size,"Mark","type","square");
		}
		else if (Type.equals(Field.Marker.CIRCLE.value))
		{	printAllFields(xml,size,"Mark","type","circle");
		}
		else if (Type.equals(Field.Marker.TRIANGLE.value))
		{	printAllFields(xml,size,"Mark","type","triangle");
		}
		else if (Type.equals("TB"))
		{	printAllFields(xml,size,"Mark","territory","black");
		}
		else if (Type.equals("TW"))
		{	printAllFields(xml,size,"Mark","territory","white");
		}
		else if (Type.equals("LB"))
		{	printAllSpecialFields(xml,size,"Mark","label");
		}
		else
		{	xml.startTag("SGF","type",Type);
			for (String argument : Arguments)
			{	xml.startTag("Arg");
				StringParser p=new StringParser(argument);
				Vector<String> v=p.wrapwords(60);
				for (int i=0; i<v.size(); i++)
				{	argument=v.elementAt(i);
					if (i>0) xml.println();
					xml.print(argument);
				}
				xml.endTag("Arg");
			}
			xml.endTagNewLine("SGF");
		}
	}

	/**
	Print the node content of a move in XML form and take care of times
	and names.
	*/
	public void printMove (XmlWriter xml, int size, int number, Node n)
	{	String s="";
		if (Type.equals("B")) s="Black";
		else if (Type.equals("W")) s="White";
		else return;
		xml.startTagStart(s);
		xml.printArg("number",""+number);
		if (n.contains("N")) xml.printArg("name",n.getaction("N"));
		if (s.equals("Black") && n.contains("BL"))
			xml.printArg("timeleft",n.getaction("BL"));
		if (s.equals("White") && n.contains("WL"))
			xml.printArg("timeleft",n.getaction("WL"));
		xml.printArg("at",getXMLMove(size));
		xml.finishTagNewLine();
	}

	/**
	Test, if this action contains printed information
	*/
	public boolean isRelevant ()
	{	if (Type.equals("GN") 
			|| Type.equals("AP")
			|| Type.equals("FF")
			|| Type.equals("GM")
			|| Type.equals("N")
			|| Type.equals("SZ")
			|| Type.equals("PB")
			|| Type.equals("BR")
			|| Type.equals("PW")
			|| Type.equals("WR")
			|| Type.equals("HA")
			|| Type.equals("KM")
			|| Type.equals("RE")
			|| Type.equals("DT")
			|| Type.equals("TM")
			|| Type.equals("US")
			|| Type.equals("CP")
			|| Type.equals("BL")
			|| Type.equals("WL")
			|| Type.equals("C")
			)
		return false;
		else return true;
	}

	/**
	Print all arguments as field positions with the specified tag.
	*/
	public void printAllFields (XmlWriter xml, int size, String tag)
	{
		for (String s : Arguments)
		{	xml.startTagStart(tag);
			xml.printArg("at",getXMLMove(s,size));
			xml.finishTagNewLine();
		}
	}
	
	public void printAllFields (XmlWriter xml, int size, String tag,
		String argument, String value)
	{
		for (String s : Arguments)
		{	xml.startTagStart(tag);
			xml.printArg(argument,value);
			xml.printArg("at",getXMLMove(s,size));
			xml.finishTagNewLine();
		}
	}

	public void printAllSpecialFields (XmlWriter xml, int size, String tag, String argument)
	{
		Pattern pattern = Pattern.compile("[^:]*:(\\w+).*");
		for (String s : Arguments)
		{	Matcher matcher = pattern.matcher(s);
			if (matcher.matches())
			{
				xml.startTagStart(tag);
				xml.printArg(argument,matcher.group(1));
				xml.printArg("at",getXMLMove(s,size));
				xml.finishTagNewLine();
			}
		}
	}

	/**
	@return The readable coordinate version (Q16) of a move,
	stored in first argument.
	*/
	public String getXMLMove (String s, int size)
	{	if (s==null) return "";
		int i=Field.i(s),j=Field.j(s);
		if (i<0 || i>=size || j<0 || j>=size) return "";
		return Field.coordinate(Field.i(s),Field.j(s),size);
	}
	
	public String getXMLMove (int size)
	{	return getXMLMove(Arguments.get(0),size);
	}

	public void printTextArgument (XmlWriter xml)
	{	if (Arguments.isEmpty()) return;
		xml.printParagraphs(Arguments.get(0),60);
	}

	// modifiers
	public void type (String s) { Type=s; }

	// access methods:
	public String type () { return Type; }
	public List<String> arguments () { return Arguments; }
	public String argument ()
	{	if (arguments()==null) return "";
		return arguments().get(0);
	}
}
