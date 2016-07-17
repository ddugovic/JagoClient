package jagoclient.igs.games;

import jagoclient.Global;
import java.util.regex.Pattern;
import rene.util.parser.StringParser;

/**
This is a SortObject for sorting games by W player rank.
@see jagoclient.sort.Sorter
*/

public class GamesObject implements Comparable<GamesObject>
{	String S;
	int V;
	String White,Black;
	public GamesObject (String s)
	{	S=s;
		if (S.indexOf(']')==3) S=" "+S;
		StringParser p=new StringParser(s);
		p.upto(']'); p.skip("]");
		White=p.upto('[').trim();
		p.skip("[");
		String h=p.parseword(']');
		p.upto('.'); p.skip(".");
		Black=p.upto('[').trim();
		if (p.error())
		{	V=-50; return;
		}
		p=new StringParser(h);
		if (p.isint())
		{	V=p.parseint();
			if (p.skip("k")) V=100-V;
			else if (p.skip("d")) V=100+V;
			else if (p.skip("p")) V+=200;
		}
		else V=0;
	}
	String game () { return S; }
	@Override
	public int compareTo (GamesObject g)
	{	return g.V-V;
	}
	
	public boolean friend ()
	{	String friends=Global.getParameter("friends","").trim();
		Pattern pattern=Pattern.compile("\\b("+friends.replaceAll(" ","|")+")\\b");
		return pattern.matcher(White).matches() || pattern.matcher(Black).matches();
	}
}
