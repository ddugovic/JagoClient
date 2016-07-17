package rene.util.list;

import java.util.LinkedList;

/**
 * A node with a list of children trees.
 */
public class Tree<E>
{	protected LinkedList<Tree<E>> children; // list of children, each with Tree as content
	protected E content; // content
	protected Tree<E> parent; // the parent tree

	/** initialize with an object and no children */
	public Tree (E content)
	{	this.content=content;
		children=new LinkedList<Tree<E>>();
		parent=null;
	}

	/** add a child tree */
	public void addchild (Tree<E> t)
	{	children.addLast(t);
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
		children=new LinkedList<Tree<E>>();
		children.addLast(t);
		t.parent=this;
		// fix the parents of all grandchildren
		for (Tree<E> h : t.children)
		{	h.parent=t;
		}
	}

	/** remove the specific child tree (must be in the tree!!!) */
	public void remove (Tree<E> t)
	{	if (t.parent()!=this) return;
		children.remove(t);
	}

	/** remove all children */
	public void removeall ()
	{	children.clear();
	}

	// Access Methods:
	@Deprecated
	public boolean haschildren () { return !children.isEmpty(); }
	@Deprecated
	public Tree<E> firstchild () { return children.peekFirst(); }
	public Tree<E> previouschild (Tree<E> t) { return children.get(children.indexOf(t)-1); }
	public Tree<E> nextchild (Tree<E> t) { return children.get(children.indexOf(t)+1); }
	@Deprecated
	public Tree<E> lastchild () { return children.peekLast(); }
	public Tree<E> parent () { return parent; }
	public LinkedList<Tree<E>> children () { return children; }
	public E content () { return content; }
	public void content (E content) { this.content=content; }
}
