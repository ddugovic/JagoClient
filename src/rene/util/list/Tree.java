package rene.util.list;

/**
 * A node with a list of children trees.
 */
public class Tree<E>
{	protected ListClass<Tree<E>> children; // list of children, each with Tree as content
	protected E content; // content
	protected ListElement<Tree<E>> element; // the listelement containing the tree
	protected Tree<E> parent; // the parent tree

	/** initialize with an object and no children */
	public Tree (E content)
	{	this.content=content;
		children=new ListClass<Tree<E>>();
		element=null; parent=null;
	}

	/** add a child tree */
	public void addchild (Tree<E> t)
	{	ListElement<Tree<E>> p=new ListElement<Tree<E>>(children, t);
		children.append(p);
		t.element=p; t.parent=this;
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
		ListElement<Tree<E>> p=new ListElement<Tree<E>>(children, t);
		children.append(p);
		t.element=p; t.parent=this;
		// fix the parents of all grandchildren
		for (ListElement<Tree<E>> le : t.children)
		{	Tree h=(Tree)(le.content());
			h.parent=t;
		}
	}

	/** remove the specific child tree (must be in the tree!!!) */
	public void remove (Tree<E> t)
	{	if (t.parent()!=this) return;
		children.remove(t.element);
	}

	/** remove all children */
	public void removeall ()
	{	children.clear();
	}

	// Access Methods:
	@Deprecated
	public boolean haschildren () { return !children.isEmpty(); }
	@Deprecated
	public Tree<E> firstchild () { return (Tree<E>)children.first().content(); }
	@Deprecated
	public Tree<E> lastchild () { return (Tree<E>)children.last().content(); }
	public Tree<E> parent () { return parent; }
	public ListClass<Tree<E>> children () { return children; }
	public E content () { return content; }
	public void content (E content) { this.content=content; }
	public ListElement<Tree<E>> listelement () { return element; }
}

