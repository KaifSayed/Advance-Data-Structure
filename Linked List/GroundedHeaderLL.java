class Node {
    int data;
    Node next;

    public Node(int data) {
        this.data = data;
        this.next = null;
    }
}

class HeaderLinkedList {
    Node header;

    public HeaderLinkedList() {
        header = new Node(-1);
    }

    public void insert(int data) {
        Node newNode = new Node(data);
        Node current = header;

        while (current.next != null) {
            current = current.next;
        }
        current.next = newNode;
    }

    public void delete(int data) {
        Node current = header;

        while (current.next != null && current.next.data != data) {
            current = current.next;
        }

        if (current.next != null) {
            current.next = current.next.next;
            System.out.println("Deleted: " + data);
        } else {
            System.out.println("Element " + data + " not found!");
        }
    }

    public void display() {
        Node current = header.next;

        if (current == null) {
            System.out.println("The List is Empty.");
            return;
        }

        while (current != null) {
            System.out.print(current.data + " ");
            current = current.next;
        }
        System.out.println();
    }

    public boolean search(int value) {
        Node current = header.next;

        while (current != null) {
            if (current.data == value) {
                return true;
            }
            current = current.next;
        }
        return false;
    }
}

// Main class to test the HeaderLinkedList
class HeaderLL2 {
    public static void main(String[] args) {
        HeaderLinkedList list = new HeaderLinkedList();

        list.insert(10);
        list.insert(20);
        list.insert(30);

        System.out.print("List after insertion: ");
        list.display();

        list.delete(20);

        System.out.print("List after deleting 20: ");
        list.display();

        System.out.println("Is 30 present in list: " + list.search(30));
        System.out.println("Is 50 present in list: " + list.search(50));
    }
}

