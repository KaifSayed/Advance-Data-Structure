class Stack{
	private Node top;

	//node structure
	private static class Node{
		int data;
		Node next;

        Node(int data) {
			this.data = data;
			this.next = null;
        }
	}

	public Stack(){
		this.top = null;
	}
	public void push(int data){
		Node newNode = new Node(data);
		newNode.next = top;
		top = newNode;
	}

	public int pop(){
		if (top == null) {
			System.out.println("Stack is empty. ");
			return -1;
		}

		int data = top.data;
		top = top.next;
		System.out.println("Popped: "+ data);
		return data;
	}
	public int peek(){
		if (top == null) {
			System.out.println("Stack is empty. ");
			return -1;
		}
		return top.data;
	}

	public boolean isEmpty(){
		return top == null;
	}

	// Display the element in the stack
	public void display(){
		if (top == null) {
			System.out.println("Stack is empty. ");
			return;
		}
		Node current = top;
		while (current != null) { 
			System.out.println(current.data + " ");
			current = current.next;
		}
		System.out.println();
	}
}
// Usage Example
public class LinkedListStack{
	public static void main(String[] args) {
		System.out.println("25MCA53 - Sayed Mohammed Kaif Talib");
		Stack stack = new Stack();
		stack.push(10);
		stack.push(20);
		stack.push(30);
		stack.push(40);
		stack.display();

		stack.pop();
		stack.display();

		System.out.println("Top elememt is : "+ stack.peek());
	}
}