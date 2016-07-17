package rene.util.list;

/**
 * A node with a list of children trees.
 */
public class Tree<E>
{	protected ListClass<Tree<E>> children; // list of children, each with Tree as content
	protected E content; // content
	protected Tree<E> parent; // the parent tree

	/** initialize with an object and no children */
	public Tree (E content)
	{	this.content=content;
		children=new ListClass<Tree<E>>();
		parent=null;
	}

	/** add a child tree */
	public void addchild (Tree<E> t)
	{	children.append(t);
		t.parent=this;
	}

	/** insert a child tree */
	public void insertchild (Tree<E> t)
	{	if (!haschildren()) // simple case
		{	addchild(t); return;
		}
		// give t my children
		t.children=children;
		// make t my only child
		children=new ListClass<Tree<E>>();
		children.append(t);
		t.parent=this;
		// fix the parents of all grandchildren
		for (ListElement<Tree<E>> le : t.children)
		{	Tree h=(Tree)(le.content());
			h.parent=t;
		}
	}

	/** remove the specific child tree (must be in the tree!!!) */
	public void remove (Tree<E> t)
	{	if (t.parent()!=this) return;
		children.removeIf((ListElement<Tree<E>> e) -> e.content() == t);
	}

	/** remove all children */
	public void removeall ()
	{	children.clear();
	}

	// Access Methods:
	@Deprecated
	public boolean haschildren () { return !children.isEmpty(); }
	@Deprecated
	public Tree<E> firstchild () { return children.first().content(); }
	public Tree<E> previouschild (Tree<E> t)
	{	ListElement<Tree<E>> e = children.get(children.indexOf(t.listelement())-1);
		return e == null ? null : e.content();
	}
	public Tree<E> nextchild (Tree<E> t)
	{	ListElement<Tree<E>> e = children.get(children.indexOf(t.listelement())+1);
		return e == null ? null : e.content();
	}
	@Deprecated
	public Tree<E> lastchild () { return children.last().content(); }
	public Tree<E> parent () { return parent; }
	public ListClass<Tree<E>> children () { return children; }
	public E content () { return content; }
	public void content (E content) { this.content=content; }
	@Deprecated
	public ListElement<Tree<E>> listelement ()
	{	return children.stream().filter((ListElement<Tree<E>> e) -> e.content() == this).findFirst().get();
	}
}
