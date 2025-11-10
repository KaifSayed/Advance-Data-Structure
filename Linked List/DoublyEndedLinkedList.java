class DoublyEndedLinkedList{
    // Node calss representing each element of Doubly linked list
	class Node {
        int data;
        Node next;
        Node prev;

        // Constructor to create a new node
        public Node(int data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }
    private  Node head, tail;

    public DoublyEndedLinkedList(){
        this.head = null;
        this.tail = null;
    }

    public void insertAtHead(int data){
        Node newNode = new Node(data);

        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
    }

    // Method to insert a node at the end of the list (tail)
    public void insertAtTail(int data){
        Node newNode = new Node(data);

        if (tail == null) {
            head = tail = newNode;
        }
        else{
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    // Method to delete a node from the front (head) of the list
    public void deleteFromHead(){
        if (head == null){
            System.out.println("List is empty. Cannot delete.");
	        return;
        }
        if (head == tail){
	        head = tail = null;
	    }
	    else{
            
	    }
    }
    
    public void deleteFromTail(){
        if (tail == null) {
            System.out.println("List is empty. Cannot delete.");
            return;
        }
        if (head == tail) {
            head = tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
    }

    public void displayFromHead(){
        if (head == null) {
            System.out.println("List is empty. Cannot delete");
	        return;
        }
        if (head == tail){
            head = tail = null;
        }
        else{
            head = head.next;
            head.prev = null;
        }

        Node current = head;
        while (current != null) { 
            System.out.print(current.data + " ");
            current = current.next;
        }
	System.out.println();
    }

    public void displayFromTail(){
	    if (tail == null){
            System.out.println("List is empty.");
            return;
	    }

        Node current = tail;
        while (current != null) { 
            System.out.print(current.data + " ");
            current = current.prev;
        }
        System.out.println();
    }
    public static void main(String[] args) {
        DoublyEndedLinkedList list = new DoublyEndedLinkedList();

        list.insertAtHead(10);
        list.insertAtHead(20);
        list.insertAtHead(30);
        list.insertAtHead(40);
        list.insertAtHead(50);

        System.out.println("List from head to tail: ");
        list.displayFromHead(); // Output: 30 20 10

        // Inserting nodes at the tail
        list.insertAtTail(60);
        list.insertAtTail(70);
        list.insertAtTail(80);
        list.insertAtTail(90);

        System.out.println("List from head to tail after inserting at tail: ");
        list.displayFromHead();

        // Deleting from head
        list.deleteFromHead();
        System.out.println("List from head to tail after deleting from head: ");
        list.displayFromHead();

        // Deleting from tail
        list.deleteFromTail();
        System.out.println("List after head to tail after delecting from tail: ");
        list.displayFromHead();
    }

}
