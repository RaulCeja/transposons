package linked;


public class DNALinkedList 
{
	private Node<Character>			head;	// head.prev is always null
	private Node<Character>			tail;	// tail.next is always null
	
	
	public DNALinkedList(String s)
	{
		append(s);		
	}
	
	
	// Used by extraction methods. Not for public use.
	private DNALinkedList(Node<Character> head, Node<Character> tail)
	{ 
		this.head = head;
		head.setPrev(null);
		this.tail = tail;
		tail.setNext(null);
	}
	
	
	// Converts arg to nodes which are appended to end of this list.
	public void append(String s)
	{
		for (int i=0; i<s.length(); i++)
			append(s.charAt(i));
	}
	
	
	// Creates a node for ch and appends it to the linked list.
	
	public void append(char ch)
	{
		append(new Node<Character>(ch));
	}
	
	
	// Appends n to tail of this list.
	public void append(Node<Character> n)
	{
		// Corner case:   .
		if (tail == null)
		{
			n.setPrev(null);
			n.setNext(null);
			head = n;
			tail = n;
		}
		
		// normal case.
		else
		{
			tail.setNext(n);
			n.setPrev(tail);
			tail = n;
		}
	}
	
	
	public String toString()
	{
		String s = "DNALinkedList: ";
		if (head == null)
			s += "Empty";
		else
		{
			Node<Character> n = head;
			while (n != null)
			{
				s += n.getData();
				n = n.getNext();
			}
		}
		return s;
	}
	
	
	// Returns true if the nodes starting at startNode match the target string. For example,
	// if the linked list looks like this:
	// (A) -> (B) -> (C) -> (D) -> (E) -> (F)

	private boolean matches(Node<Character> startNode, String target)
	{
		Node<Character> sequence = startNode;
		if(startNode.getData() != target.charAt(0))//if the data of the startNode does not equal the start of the target
			return false;
		for(int i=0; i<target.length(); i++)//iterate through the target
		{
			if(sequence.getData() == target.charAt(i))//check if sequence data matches the character in the target
			{
				if(i==target.length()-1)//checks if it is the last character
				{
					return true;
				}
				sequence = sequence.getNext();	//updates sequence with the next one
			}
		}
		return false;	
	}
	
	
	public Node<Character> find(String target)//finds the target string
	{
		Node<Character> checkNode = this.head;
		while(!this.matches(checkNode, target) && checkNode!=this.tail)//loops while this matches checknode and target, and the checknode is not this tail
		{
			checkNode = checkNode.getNext();//updates checkNode with the next one
		}
		if(this.matches(checkNode, target))//this node matches checknode and target
		{
			return checkNode;
		}
		return null;
	}
	
	
	// Simple algorithm that assumes no corner cases: both args are in the list,
	// firstExtractedNode is not the head, and lastExtractedNode is not the tail.
	// ends of DNA molecules aren't involved in life processes, don't have transposons, and don't need to be
	// handled as corner cases.
	public DNALinkedList extract(Node<Character> firstExtractedNode, Node<Character> lastExtractedNode)
	{
		// Find nodes just before and just after the chain to be extracted. Assume these
		// aren't null.
		Node<Character> beforeFirst = firstExtractedNode.getPrev();
		Node<Character> afterLast = lastExtractedNode.getNext();
		
		// Connect beforeFirst to afterLast.
		beforeFirst.setNext(afterLast);
		afterLast.setPrev(beforeFirst);
		
		// Return a DNALinkedList containing the extracted chain.
		return new DNALinkedList(firstExtractedNode, lastExtractedNode);
	}

	
	// Reverses the order of the nodes.
	public void reverse()
	{
		// Swap next and prev of every node.
		Node<Character> n = head;
		while (n != null)
		{
			Node<Character> temp = n.getNext();
			n.setNext(n.getPrev());
			n.setPrev(temp);
			n=n.getPrev();
		}
		
		// Swap head and tail.
		Node<Character> temp = head;
		head = tail;
		tail = temp;
	}
	
	
	// Inserts insertMe into this list, at the node before insertionPoint. Assumes
	// insertionPoint is not the head or tail. 
	public void insert(DNALinkedList insertMe, Node<Character> insertionPoint)
	{
		// Find node immediately before insertion point.
		Node<Character> beforeInsertionPoint = insertionPoint.getPrev();
		
		// Connect node immediately before insertion point to head of insertMe.
		beforeInsertionPoint.setNext(insertMe.head);
		insertMe.head.setPrev(beforeInsertionPoint);
		
		// Connect tail of insertMe to insertionPoint node.
		insertMe.tail.setNext(insertionPoint);
		insertionPoint.setPrev(insertMe.tail);
	}
	

	// Removes sequence matching transposon, reverses it, and inserts it back into
	// this list immediately before target. Throws IllegalArgumentException if
	// can't find transposon or target.
	public void transpose(String transposon, String target) throws IllegalArgumentException
	{
		// Find starting node of transposon.
		Node<Character> firstNodeOfTransposon = this.find(transposon);
		if (firstNodeOfTransposon == null)
		{
			String err = "Can't find given transposon: " + transposon;
			IllegalArgumentException x = new IllegalArgumentException(err);
			throw x;
		}
		
		// Find starting node of target.
		Node<Character> firstNodeOfTarget = this.find(target);
		if (firstNodeOfTarget == null)
		{
			String err = "Can't find given target: " + target;
			IllegalArgumentException x = new IllegalArgumentException(err);
			throw x;
		}
		
		// Find last node of transposon. You'll need several lines. Set a variable to the first node
		// of the transposon, then do a loop where for every char in the transposon, you set the variable
		// to its "next".
		Node<Character> lastNodeOfTransposon = firstNodeOfTransposon;
		for(int i=0; i<transposon.length(); i++)
		{
			lastNodeOfTransposon = lastNodeOfTransposon.getNext();
		}
				
		// Extract the transposon.
		DNALinkedList transposonList = this.extract(firstNodeOfTransposon, lastNodeOfTransposon);
		
		// Reverse the transposon.
		transposonList.reverse();
		
		// Insert immediately before target.
		insert(transposonList, firstNodeOfTarget);
	}
		
	
	public static void main(String[] args) 
	{
		String chromosome = "CGTCCAGTAC";
		DNALinkedList list = new DNALinkedList(chromosome);
		System.out.println("original: " + list);
		String transposon = "GTC";
		String target = "AGT";
		list.transpose(transposon, target);
		System.out.println("transposed: " + list);
		
	}
}
