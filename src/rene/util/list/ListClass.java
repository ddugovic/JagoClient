package rene.util.list;

import java.util.LinkedList;

/**
 * A linked list of ListElements which reference this; i.e., a tree.
 * 
 * @see rene.list.ListElement
 */
public class ListClass<E> extends LinkedList<ListElement<E>>
{
	/**
	 * Append a node to the list
	 * @deprecated
	 */
	public void append (ListElement<E> l)
	{
		super.addLast(l);
	}
	public void append (E content)
	{
		super.addLast(new ListElement<E>(this, content));
	}

	/**
	 * Prepend a node to the list
	 * @deprecated
	 */
	public void prepend (ListElement<E> l)
	{
		super.addFirst(l);
	}
	public void prepend (E content)
	{
		super.addFirst(new ListElement<E>(this, content));
	}

	/*
	 * @param l ListElement to be inserted.
	 * 
	 * @param after If null, it works like prepend.
	 */
	public void insert (ListElement<E> l, ListElement<E> after)
	{
		super.add(indexOf(after) + 1, l);
	}

	/**
	 * @return First ListElement.
	 * @deprecated
	 */
	public ListElement<E> first ()
	{
		return super.peekFirst();
	}

	/**
	 * @return Last ListElement.
	 * @deprecated
	 */
	public ListElement<E> last ()
	{
		return super.peekLast();
	}

	/**
	 * Prints the class
	 */
	@Override
	public String toString ()
	{
		StringBuilder sb = new StringBuilder();
		for (ListElement<E> e : this)
		{
			sb.append(e.content().toString() + ", ");
		}
		return sb.toString();
	}
}
