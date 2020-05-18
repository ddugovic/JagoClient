package jagoclient.igs.who;

import jagoclient.Global;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import rene.util.parser.StringParser;

/**
This is needed for the Sorter class.
*/

public class WhoObject implements Comparable<WhoObject>
{	protected static final Pattern WORD_PATTERN = Pattern.compile("\\w+");
	String S,Name,Stat;
	public int V;
	boolean SortName;
	public WhoObject (String s, boolean sortname)
	{	S=s; SortName=sortname;
		if (s.length()<=30)
		{	V=-50; Name=""; Stat=""; return;
		}
		Stat=s.substring(0,5);
		Matcher matcher = WORD_PATTERN.matcher(s);
		String h = matcher.find(30) ? matcher.group() : null;
		StringParser p=new StringParser(h);
		if (p.isint())
		{	V=p.parseint();
			if (p.skip("k")) V=100-V;
			else if (p.skip("d")) V=100+V;
			else if (p.skip("p")) V+=200;
			else if (p.skip("NR")) V=0;
		}
		else V=-50;
		if (s.length()<14) Name="";
		else
		{	matcher = WORD_PATTERN.matcher(s);
			Name = matcher.find() ? matcher.group(12) : null;
		}
	}
	String who () { return S; }
	@Override
	public int compareTo (WhoObject g)
	{	if (SortName)
		{	return Name.compareTo(g.Name);
		}
		else
		{	if (V<g.V) return 1;
			else if (V>g.V) return -1;
			else return 0;
		}
	}
	public boolean looking ()
	{	return Stat.indexOf('!')>=0;
	}
	public boolean quiet ()
	{	return Stat.indexOf('Q')>=0;
	}
	public boolean silent ()
	{	return Stat.indexOf('X')>=0;
	}
	public boolean friend ()
	{	return Global.getParameter("friends","").indexOf(" "+Name)>=0;
	}
	public boolean marked ()
	{	return Global.getParameter("marked","").indexOf(" "+Name)>=0;
	}
}
