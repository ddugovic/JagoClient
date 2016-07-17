package jagoclient.board;

import java.io.PrintWriter;

import rene.util.list.ListClass;
import rene.util.list.ListElement;
import rene.util.list.Tree;
import rene.util.xml.XmlWriter;

/**
A node has 
<UL>
<LI> a list of actions and a number counter (the number is the number
of the next expected move in the game tree),
<LI> a flag, if the node is in the main game tree,
<LI> a list of changes in this node to be able to undo the node,
<LI> the changes in the prisoner count in this node.
</UL>
@see jagoclient.board.Action
@see jagoclient.board.Change
*/

public class Node
{	ListClass<Action> Actions; // actions and variations
	int N; // next exptected number
	boolean Main; // belongs to main variation
	ListClass<Change> Changes;
	public int Pw,Pb; // changes in prisoners in this node
	
	/** initialize with the expected number */
	public Node (int n)
	{	Actions=new ListClass<Action>();
		N=n;
		Main=false;
		Changes=new ListClass<Change>();
		Pw=Pb=0;
	}
	
	/** add an action (at end) */
	public void addaction (Action a)
	{	Actions.append(a);
	}

	/** expand an action of the same type as a, else generate a new action */
	public void expandaction (Action a)
	{	ListElement p=find(a.type());
		if (p==null) addaction(a);
		else
		{	Action pa=(Action)p.content();
			pa.addargument(a.argument());
		}
	}

	/**
	Expand an action of the same type as a, else generate a new action.
	If the action is already present with the same argument, delete
	that argument from the action.
	*/
	public void toggleaction (Action a)
	{	ListElement p=find(a.type());
		if (p==null) addaction(a);
		else
		{	Action pa=(Action)p.content();
			pa.toggleargument(a.argument());
		}
	}

	/** find the list element containing the action of type s */
	ListElement find (String s)
	{	
		for (ListElement<Action> p : Actions)
		{	Action a=p.content();
			if (a.type().equals(s)) return p;
		}
		return null;
	}
	
	/** find the action and a specified tag */
	public boolean contains (String s, String argument)
	{	ListElement p=find(s);
		if (p==null) return false;
		Action a=(Action)p.content();
		return a.contains(argument);
	}
	
	/** see if the list contains an action of type s */
	public boolean contains (String s)
	{	return find(s)!=null;
	}

	/** add an action (at front) */
	public void prependaction (Action a)
	{	Actions.prepend(a);
	}
	
	/** 
	Insert an action after p.
	p <b>must</b> have content type action.
	*/
	public void insertaction (Action a, ListElement<Action> p)
	{	Actions.insert(new ListElement<Action>(Actions, a), p);
	}

	/** remove an action */
	public void removeaction (ListElement<Action> la)
	{	Actions.remove(la);
	}

	/**
	If there is an action of the type:
	Remove it, if arg is "", else set its argument to arg.
	Else add a new action in front (if it is true)
	*/
	public void setaction (String type, String arg, boolean front)
	{	
		for (ListElement<Action> l : Actions)
		{	Action a=l.content();
			if (a.type().equals(type))
			{	if (arg.equals(""))
				{	Actions.remove(l);
					return;
				}
				else
				{	ListElement<String> la=a.arguments().first();
					if (la!=null) la.content(arg);
					else a.addargument(arg);
				}
				return;
			}
		}
		if (front) prependaction(new Action(type,arg));
		else addaction(new Action(type,arg));
	}

	/** set the action of this type to this argument */
	public void setaction (String type, String arg)
	{	setaction(type,arg,false);
	}

	/** get the argument of this action (or "") */
	public String getaction (String type)
	{
		for (ListElement<Action> l : Actions)
		{	Action a=l.content();
			if (a.type().equals(type))
			{	ListElement la=a.arguments().first();
				if (la!=null) return (String)la.content();
				else return "";
			}
		}
		return "";
	}
	
	/** 
	Print the node in SGF.
	@see jagoclient.board.Action#print
	*/
	public void print (PrintWriter o)
	{	o.print(";");
		for (ListElement<Action> p : Actions)
		{	Action a=p.content();
			a.print(o);
		}
		o.println("");
	}
	
	public void print (XmlWriter xml, int size)
	{	int count=0;
		Action ra=null,a;
		for (ListElement<Action> p : Actions)
		{	a=p.content();
			if (a.isRelevant())
			{	count++;
				ra=a;
			}
		}
		if (count==0 && !contains("C"))
		{	xml.finishTagNewLine("Node");
			return;
		}
		int number=N-1;
		if (count==1)
		{	if (ra.type().equals("B") || ra.type().equals("W"))
			{	ra.printMove(xml,size,number,this);
				number++;
				if (contains("C"))
				{	a=((Action)find("C").content());
					a.print(xml,size,number);
				}
				return;
			}
		}
		xml.startTagStart("Node");
		if (contains("N")) xml.printArg("name",getaction("N"));
		if (contains("BL")) xml.printArg("blacktime",getaction("BL"));
		if (contains("WL")) xml.printArg("whitetime",getaction("WL"));
		xml.startTagEndNewLine();
		for (ListElement<Action> p : Actions)
		{	a=p.content();
			a.print(xml,size,number);
			if (a.type().equals("B") || a.type().equals("W")) number++;
		}
		xml.endTagNewLine("Node");
	}

	/** remove all actions */
	public void removeactions ()
	{	Actions=new ListClass();
	}

	/** add a new change to this node */
	public void addchange (Change c)
	{	Changes.append(c);
	}

	/** clear the list of changes */
	public void clearchanges ()
	{	Changes.clear();
	}	

	// modification methods:
	public void main (boolean m) { Main=m; }
	/** 
	Set the Main flag
	@param Tree is the tree, which contains this node on root.
	*/
	public void main (Tree p)
	{   Main=false;
		try
		{	if (((Node)p.content()).main())
			{	Main=(this==((Node)p.firstchild().content()));
			}
			else if (p.parent()==null) Main=true;
		}
		catch (Exception e) {}
	}
	public void number (int n) { N=n; }
	
	/**
	Copy an action from another node.
	*/
	public void copyAction (Node n, String action)
	{	if (n.contains(action))
		{	expandaction(new Action(action,n.getaction(action)));
		}
	}

	// access methods:
	public ListClass<Action> actions () { return Actions; }
	@Deprecated
	public ListElement<Action> lastaction () { return Actions.last(); }
	public ListClass<Change> changes () { return Changes; }
	@Deprecated
	public ListElement<Change> lastchange () { return Changes.last(); }
	public int number () { return N; }
	public boolean main () { return Main; }
}
