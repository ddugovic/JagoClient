package rene.util.xml;

import java.util.Enumeration;
import rene.util.list.ListElement;

import rene.util.list.Tree;
import rene.util.parser.StringParser;

public class XmlTree extends Tree<XmlTag>
	implements Enumeration<Tree<XmlTag>>
{	public XmlTree (XmlTag t)
	{	super(t);
	}
	
	public XmlTag getTag ()
	{	return (XmlTag)content();
	}
	
	public Tree<XmlTag> xmlFirstContent ()
	{	if (children.isEmpty()) return null;
		else return children.getFirst().content();
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
		XmlTree t=(XmlTree)children.getFirst().content();
		XmlTag tag=t.getTag();
		return ((XmlTagText)tag).getContent();
	}
	
	ListElement<Tree<XmlTag>> Current;

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
		Tree<XmlTag> c=Current.content();
		Current=Current.next();
		return c;
	}

	public boolean isTag (String s)
	{	return getTag().name().equals(s);
	}
	
	public String parseComment ()
		throws XmlReaderException
	{	StringBuffer s=new StringBuffer();
		for (ListElement<Tree<XmlTag>> tree : children)
		{
			XmlTag tag=tree.content().content();
			if (tag.name().equals("P"))
			{	if (tree.content().children().isEmpty()) s.append("\n");
				else
				{	Tree<XmlTag> h=tree.content().children().getFirst().content();
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
