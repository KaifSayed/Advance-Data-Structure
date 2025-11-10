class Node {
    int data;   // Data part of the node
    Node next;  // Pointer to the next node
    Node prev;  // Pointer to the previous node

    // Constructor to create a new node
    public Node(int data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}

class DoublyLinkedList {
    Node head; // Head node of the doubly linked list
    Node tail; // Tail node of the doubly linked list
    // Constructor to initialize the list
    public DoublyLinkedList() {
        head = null;
        tail = null;
    }

    // Method to insert a node at the beginning
    public void insertAtBeginning(int data) {
        Node newNode = new Node(data);

        if (head == null) { 
            head = tail = newNode;
	    // If list is empty, head and tail are the same
        } else {
            newNode.next = head;  // Set new node's next to current head
            head.prev = newNode;  // Set current head's prev to new node 
            head = newNode;       // Move head to point to the new node 
        }
    }

    // Method to insert a node at the end
    public void insertAtEnd(int data) 
    {
        Node newNode = new Node(data);
        if (head == null) 
        {
            head = tail = newNode;  // If the list is empty, head and tail are the same
        } 
        else 
        {
            tail.next = newNode;    // Set current tail's next to new node
            newNode.prev = tail;    // Set new node's prev to current tail
            tail = newNode;         // Move tail to point to the new node
        }
    }

    // Method to delete the first node
    public void deleteFirst() 
    {
        if (head == null) 
        {
            System.out.println("The list is empty.");
            return;
        }
        if (head == tail) 
        {
            head = tail = null;  // If there's only one node, set both head and tail to null
        }
        else 
        {
            head = head.next;    // Move head to the next node
            head.prev = null;    // Set the previous pointer of the new head to null
        }
    }

    // Method to delete the last node
    public void deleteLast() 
    {
        if (head == null) 
        {
            System.out.println("The list is empty.");
            return;
        }
        if (head == tail) 
        {
            head = tail = null;  // If there's only one node, set both head and tail to null
        } 
        else 
        {
            tail = tail.prev;    // Move tail to the previous node
            tail.next = null;     // Set the next pointer of the new tail to null
        }
    }

    // Method to delete a node with a specific value
    public void delete(int value) 
    {
        if (head == null) 
        {
            System.out.println("The list is empty.");
            return;
        }

        // Traverse the list to find the node to delete
        Node temp = head;
        while (temp != null) 
        {
            if (temp.data == value) 
            {
                if (temp == head) 
                   {
                    deleteFirst();  // If the node to delete is the head
                   } 
                else if (temp == tail) 
                   {
                    deleteLast();   // If the node to delete is the tail
                   } 
                else 
                   {
                    temp.prev.next = temp.next;  // Skip the current node
                    if (temp.next != null) 
                    {
                        temp.next.prev = temp.prev;  // Set the previous pointer of the next node
                    }
                }
                return;  // Node found and deleted, exit the method
            }
            temp = temp.next;  // Move to the next node
        }
        System.out.println("Node with value " + value + " not found.");
    }

    // Method to display the list from beginning to end
    public void displayForward() 
    {
        if (head == null) 
        {
            System.out.println("The list is empty.");
            return;
        }

        Node temp = head;
        while (temp != null) 
        {
            System.out.print(temp.data + " ");
            temp = temp.next;
        }
        System.out.println();
    }

    // Method to display the list from end to beginning
    public void displayBackward() {
	if (tail == null) 
        {
            System.out.println("The list is empty.");
            return;
        }
        Node temp = tail;
        while (temp != null) {
            System.out.print(temp.data + " ");
            temp = temp.prev;
        }
        System.out.println();
    }

    // Main method to test the class
    public static void main(String[] args) {
        DoublyLinkedList list = new DoublyLinkedList();

        System.out.println("List (Forward):");
        list.displayForward();

        System.out.println("List (Backward):");
        list.displayBackward();
		System.out.println("Inserting at Beginning:");
        list.insertAtBeginning(10);
        list.insertAtBeginning(20);
        list.insertAtBeginning(30);
        list.displayForward(); // 30 20 10

        // Insert elements at the end
        System.out.println("\nInserting at End:");
        list.insertAtEnd(40);
        list.insertAtEnd(50);
        list.displayForward(); // 30 20 10 40 50

        // Display backward
        System.out.println("\nList (Backward):");
        list.displayBackward(); // 50 40 10 20 30

        // Delete the first node
        System.out.println("\nDeleting First Node:");
        list.deleteFirst();
        list.displayForward(); // 20 10 40 50

        // Delete the last node
        System.out.println("\nDeleting Last Node:");
        list.deleteLast();
        list.displayForward(); // 20 10 40

        // Delete a specific node
        System.out.println("Deleting Node with value 10:");
        list.delete(10);
        list.displayForward(); // 20 40

        // Display the final list in both directions
        System.out.println("Final List (Forward):");
        list.displayForward();

        System.out.println("Final List (Backward):");
        list.displayBackward();
    }
}

