package rene.util.xml;

public class XmlTranslator
{	static StringBuilder H=new StringBuilder(10000);
	static String toXml (String s)
	{	int m=s.length();
		H.setLength(0);
		for (char c : s.toCharArray())
			switch(c)
			{	case '<' : H.append("&lt;"); break;
				case '>' : H.append("&gt;"); break;
				case '&' : H.append("&amp;"); break;
				case '\'' : H.append("&apos;"); break;
				case '\"' : H.append("&quot;"); break;
				default : H.append(c);
			}
		return H.toString();
	}
	static String toText (String s)
	{	int m=s.length();
		H.setLength(0);
		for (int i=0; i<m; i++)
		{	char c=s.charAt(i);
			if (c=='&')
			{	if (find(s,i,"&lt;"))
				{	H.append('<');
					i+=3;
				}
				else if (find(s,i,"&gt;"))
				{	H.append('>');
					i+=3;
				}
				else if (find(s,i,"&quot;"))
				{	H.append('\"');
					i+=5;
				}
				else if (find(s,i,"&apos;"))
				{	H.append('\'');
					i+=5;
				}
				else if (find(s,i,"&amp;"))
				{	H.append('&');
					i+=4;
				}
				else H.append(c);
			}
			else H.append(c);
		}
		return H.toString();
	}
	static boolean find (String s, int pos, String t)
	{	try
		{	for (int i=0; i<t.length(); i++)
			{	if (s.charAt(pos+i)!=t.charAt(i)) return false;
			}
			return true;
		}
		catch (Exception e)
		{	return false;
		}
	}
}
