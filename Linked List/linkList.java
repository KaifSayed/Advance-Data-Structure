class Node
{
	int data;
	Node next;

	// Contructor to initialize a new code 
	public Node(int data)
	{
		this.data = data;
		this.next = null; //initially, the next node is null
	}
}

class LinkedList
{
	Node head;

	// Contructor to initialize the list
	public LinkedList()
	{
		head = null;
	}
	// 	Method to insert a new node
	public void insert(int data)
	{
		Node newNode = new Node(data);
		if(head == null)
		{
			head = newNode;
		}
		else
		{
			Node temp = head;
			// Traverse to the last node in the list
			while(temp.next != null)
			{
				temp = temp.next;
			}
			// attach the new node to the end od the list
			temp.next = newNode;
		}
	}
	// Method to display the entire Linked List
	public void display()
	{
		if (head == null)
		{
			System.out.println("The list is empty. ");
			return;
		}
		Node temp = head;
		while (temp != null)
		{
			System.out.println(temp.data + " "); 
			temp = temp.next;
		}
		System.out.println();
	}
	
	// Method to delete the first node (head) of the list
	public void deleteFirst()
	{
		if(head == null)
		{
			System.out.println("The list is empty. No node to delete. ");
			return;
		}
		head = head.next; // move the pointer
	}
	// Method to delete the last node
	public void deleteLast()
	{
		if (head == null)
		{
			System.out.println("The list is empty. No node to delete. ");
			return;
		}
		// if there is only one node in the list
		if (head.next == null)
		{
			head = null;
			return;
		}
		// Traverse to find the second list node
		Node temp = head;
		while (temp.next != null && temp.next.next != null)
		{
			temp = temp.next;
		}
		temp.next = null;
	}
	// Method to delete a node by value
	public void delete(int value)
	{
		if (head == null)
		{
			System.out.println("The list is empty. No node to delete. ");
			return;
		}
		// if the node to delete is the head node
		if(head.data == value)
		{
			head = head.next;
			return;
		}
		// Traverse the list to find the node to delete 
		Node temp = head;
		while ( temp.next != null)
		{
			if (temp.next.data == value)
			{
				temp.next = temp.next.next;
				return;
			}
			temp = temp.next;
		}
		System.out.println("Node with value " + value + " not found. ");
	} 
}
public class linkList
{
	public static void main(String[] args)
	{
		// Crete a new linked list
		LinkedList list = new LinkedList();

		// Insert elements inro the linked list
		list.insert(10);
		list.insert(20);
		list.insert(30);
		list.insert(40);

		// Display the list
		System.out.println("Linked list: ");
		list.display(); // Output: 10 20 30 40

		// Display the first Element
		System.out.println("Deleting the first node...  ");
		list.deleteFirst();
		list.display(); // Output: 20 30 40

		// Display the last Element
		System.out.println("Deleting the last node...  ");
		list.deleteLast();
		list.display(); // Output: 20 30

		// Display a spicific element
		System.out.println("Deleting 20... ");
		list.delete(20);
		list.display(); // Output: 30

		// Attempting to delete a value not exist in the list
		System.out.println("Deleting the node with thr value 50... ");
		list.delete(50); // Output: Node with value 50 not found.
	} 
}

