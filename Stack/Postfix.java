import java.util.Stack;

class InfixToPostfix {

    // Method to check if a character is an operator
    private static boolean isOperator(char op) {
        return op == '+' || op == '-' || op == '*' || op == '/' || op == '^';
    }

    // Method to return the precedence of operators
    private static int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
            default:
                return 0;
        }
    }

    // Method to convert infix expression to postfix
    public static String infixToPostfix(String infix) {

        StringBuilder postfix = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        // Creating a Stack: Use Stack<Character> stack = new Stack<>();
	// to create a stack for characters.

        for (char c :  infix.toCharArray()) {

            // If operand, add it to output
            if (Character.isLetterOrDigit(c)) {
                postfix.append(c);
            }
            // If '(', push to stack
            else if (c == '(') {
                stack.push(c);
            }
            // If ')', pop until '('
            else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    postfix.append(stack.pop());
                }
                stack.pop(); // remove '(' from stack
            }
            else if (isOperator(c)) {
                while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(c)) {
                    postfix.append(stack.pop());
                }
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            postfix.append(stack.pop());
        }
        return postfix.toString();
    }

    public static void main(String[] args) {
	System.out.println("25MCA53 - Sayed Mohammed Kaif Talib");
        String infix1 = "a+b*(c^d-e)^(f+g*h)-i";
	String infix2 = "(x*y)+(z+((a+b-c)*d))-i*(j/k)";

	String postfix1 = infixToPostfix(infix1);
	String postfix2 = infixToPostfix(infix2);
  
        System.out.println("Postfix Expression 1: " + postfix1);
	System.out.println("Postfix Expression 2: " + postfix2);
    }
}
