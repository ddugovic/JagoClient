package rene.util.list;

/**
The nodes of a list.
*/

public class ListElement<C>
// A list node with reference to ListClass container
// and with a content of type Object.
{	protected C content; // the content of the node
	protected ListClass<C> list; // Belongs to this list

	public ListElement (ListClass<C> list, C content)
	// get a new Element with the content and null pointers
	{	this.content=content;
		this.list=list;
	}

	// access methods:
	public C content ()
	{	return content;
	}
	@Deprecated
	public ListElement<C> next () { return list.getLast()==this ? null : list.get(list.indexOf(this)+1); }
	@Deprecated
	public ListElement<C> previous () { return list.getFirst()==this ? null : list.get(list.indexOf(this)-1); }

	// modifying methods:
	public void content (C content) { this.content=content; }
	public void content (ListClass<C> list, C content) { this.list=list; content(content); }
}


