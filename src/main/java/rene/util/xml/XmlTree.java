package rene.util.xml;

import java.util.Enumeration;

import rene.util.list.Tree;
import rene.util.parser.StringParser;

public class XmlTree extends Tree<XmlTag>
	implements Enumeration<Tree<XmlTag>>
{	public XmlTree (XmlTag t)
	{	super(t);
	}
	
	public XmlTag getTag ()
	{	return content();
	}
	
	public Tree<XmlTag> xmlFirstContent ()
	{	if (children.isEmpty()) return null;
		else return children.getFirst();
	}
	
	public boolean isText ()
	{	if (children.isEmpty()) return true;
		if (children.getFirst()!=children.getLast()) return false;
		XmlTree t=(XmlTree)firstchild();
		XmlTag tag=t.getTag();
		if (!(tag instanceof XmlTagText)) return false;
		return true;
	}
	
	public String getText ()
	{	if (children.isEmpty()) return "";
		XmlTree t=(XmlTree)children.getFirst();
		XmlTag tag=t.getTag();
		return ((XmlTagText)tag).getContent();
	}
	
	Tree<XmlTag> Current;

	public Enumeration<Tree<XmlTag>> getContent ()
	{	Current=children().peekFirst();
		return this;
	}

	@Override
	public boolean hasMoreElements ()
	{	return Current!=null;
	}

	@Override
	public Tree<XmlTag> nextElement ()
	{	if (Current==null) return null;
		Tree<XmlTag> c=Current;
		Current=Current.parent().nextchild(Current);
		return c;
	}

	public boolean isTag (String s)
	{	return getTag().name().equals(s);
	}
	
	public String parseComment ()
		throws XmlReaderException
	{	StringBuffer s=new StringBuffer();
		for (Tree<XmlTag> tree : children)
		{
			XmlTag tag=tree.content();
			if (tag.name().equals("P"))
			{	if (tree.children().isEmpty()) s.append("\n");
				else
				{	Tree<XmlTag> h=tree.children().getFirst();
					String k=((XmlTagText)h.content()).getContent();
					k=k.replace('\n',' ');
					StringParser p=new StringParser(k);
					for (String l : p.wraplines(1000))
					{	s.append(l).append("\n");
					}
				}
			}
			else if (tag instanceof XmlTagText)
			{	String k=((XmlTagText)tag).getContent();
				StringParser p=new StringParser(k);
				for (String l : p.wraplines(1000))
				{	s.append(l).append("\n");
				}
			}
			else
				throw new XmlReaderException("<"+tag.name()+"> not proper here.");
		}
		return s.toString();
	}
}
