import java.util.Stack;

class InfixToPrefix {

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

    // Helper: reverse a string and swap '(' and ')'
    private static String reverseAndSwap(String expr) {
        StringBuilder reversed = new StringBuilder();
        for (int i = expr.length() - 1; i >= 0; i--) {
            char c = expr.charAt(i);
            if (c == '(')
                reversed.append(')');
            else if (c == ')')
                reversed.append('(');
            else
                reversed.append(c);
        }
        return reversed.toString();
    }

    // Convert infix expression to postfix (used internally)
    private static String infixToPostfix(String infix) {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (char c : infix.toCharArray()) {
            // If operand, add to output
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
                stack.pop(); // remove '('
            }
            // If operator
            else if (isOperator(c)) {
                while (!stack.isEmpty() && precedence(stack.peek()) > precedence(c)) {
                    postfix.append(stack.pop());
                }
                stack.push(c);
            }
        }

        // Pop remaining operators
        while (!stack.isEmpty()) {
            postfix.append(stack.pop());
        }
        return postfix.toString();
    }

    // Convert infix expression to prefix
    public static String infixToPrefix(String infix) {
        // Step 1: Reverse and swap brackets
        String reversed = reverseAndSwap(infix);

        // Step 2: Convert to postfix
        String postfix = infixToPostfix(reversed);

        // Step 3: Reverse postfix to get prefix
        return new StringBuilder(postfix).reverse().toString();
    }

    public static void main(String[] args) {
        String infix = "a+b*(c^d-e)^(f+g*h)-i";
        String prefix = infixToPrefix(infix);
        
        System.out.println("Prefix Expression: " + prefix);
    }
}

