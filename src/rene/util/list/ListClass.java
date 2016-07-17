package rene.util.list;

import java.util.LinkedList;

/**
 * A linked list of ListElements.
 *
 * @see rene.list.ListElement
 * @deprecated Use LinkedList instead
 */
public class ListClass<E> extends LinkedList<ListElement<E>>
{
	/**
	 * Prepend a node to the list
	 * @deprecated
	 */
	public void prepend (E content)
	{
		super.addFirst(new ListElement<E>(this, content));
	}

	/**
	 * Append a node to the list
	 * @deprecated
	 */
	public void append (E content)
	{
		super.addLast(new ListElement<E>(this, content));
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
	 * Prints a String representation of this instance.
	 */
	@Override
	public String toString ()
	{
		StringBuilder sb = new StringBuilder();
		for (ListElement<E> e : this)
		{
			sb.append(e.content().toString()).append(", ");
		}
		return sb.toString();
	}
}
