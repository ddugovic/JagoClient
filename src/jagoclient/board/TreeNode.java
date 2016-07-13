package jagoclient.board;

import rene.util.list.Tree;

/**
This is a child class of Tree, with some help functions for
the content type Node.
@see jagoclient.list.Tree
@see jagoclient.board.Node
*/

public class TreeNode extends Tree<Node>
{	/** initialize with an empty node with the specified number */
	public TreeNode ()
	{	super(new Node(1));
	}
	/** initialize with a given Node */
	public TreeNode (Node n)
	{	super(n);
	}
	/**
	Set the action type in the node to the string s.
	@param flag determines, if the action is to be added, even of s is emtpy.
	*/
	public void setaction (String type, String s, boolean flag)
	{	content().setaction(type,s,flag);
	}
	public void setaction (String type, String s)
	{	content().setaction(type,s);
	}
	/** add this action to the node */
	public void addaction (Action a)
	{	content().addaction(a);
	}
	/** @return the value of the action of this type */
	public String getaction (String type)
	{	return content().getaction(type);
	}
}
