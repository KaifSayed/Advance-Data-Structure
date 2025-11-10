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

class HeaderLinkedList {
    Node header; // The header node (dummy node)
    Node tail; // The Tail node of the list

    // Constructor to initialize the linked list with a header node
    public HeaderLinkedList() {
        header = new Node(-1);
	    tail = null;
        header.next = null;
        header.prev = null;
    }
    // Method to insert a node at the beginning o dthe list ()after the header node)
    public void insertAtBeginning(int data){
        Node newNode = new Node(data);

        if (header.next == null){
            header.next = newNode;
            newNode.prev = header;
            tail = newNode;
        }
        else{
            newNode.next= header.next;
            header.next.prev = newNode;
            header.next = newNode;
            newNode.prev = header;
        }
    }
    public void insertAtEnd(int data){
        Node newNode = new Node(data);

        if (header.next == null){
            header.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        else{
            // Insert at the End
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    public void deleteFirst(){
        if (header.next == null) {
            System.out.println("The list is empty. ");
            return;
        }

        Node firstNode = header.next;
        header.next = firstNode.next;
        if (header.next != null) {
            header.next.prev = header;
        } else {
            tail= null;
        }
    }

    public void deleteLast(){
        if (header.next == null){
            System.out.println("The list is empty.");
            return;
        }

        if (header.next == tail) {
            header.next = null;
            tail = null;
            return;
        }
        // Delete the last node
        tail = tail.prev;
        tail.next = null;
    }
    // Method to display the last from the beginning 
    public void display(){
        if (header.next == null) {
            System.out.println("The list is empty. ");
            return;
        }
        Node temp = header.next;
        while (temp != null) { 
            System.out.println(temp.data + " ");
            temp = temp.next;
        }
        System.out.println();
    }

    public boolean search(int value){
        Node temp = header.next;
        while (temp != null) { 
            if (temp.data == value) {
                return true;
            }
            temp = temp.next;
        }
        return false;
    }
}
class HeaderLL{
    public static void main(String[] args) {
        HeaderLinkedList list = new HeaderLinkedList();

        // Insert element at the beginning and end
        list.insertAtBeginning(10);
        list.insertAtBeginning(20);
        list.insertAtEnd(30);
        list.insertAtEnd(40);

        System.out.println("List after insertions: ");
        list.display();
        // Delete the fist node
        System.out.println("Deleting the first node...");
        list.deleteLast();
        list.display();

        // Search for a value in the list
        System.out.println("Searching for 30 in the list... ");
        System.out.println("Found 30: " + list.search(30));

        System.out.println("Searching for 100 in the list... ");
        System.out.println("Found 100: " + list.search(100));
        
    }

}
