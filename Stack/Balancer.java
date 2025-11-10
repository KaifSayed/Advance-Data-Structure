import java.util.Stack;

class ParenthesesBalancer {

    // Method to check if the parentheses are balanced
    public static boolean areParenthesesBalanced(String expression) {
        Stack<Character> stack = new Stack<>();

        for (char ch : expression.toCharArray()) {
            // Push opening brackets to stack
            if (ch == '(' || ch == '{' || ch == '[') {
                stack.push(ch);
            } 
            // Check for closing brackets
            else if (ch == ')' || ch == '}' || ch == ']') {
                if (stack.isEmpty() || !isMatchingPair(stack.pop(), ch)) {
                    return false;
                }
            }
        }

        // If stack is empty, all brackets were matched
        return stack.isEmpty();
    }

    // Method to check if the opening and closing brackets were match
    private static boolean isMatchingPair(char open, char close){
		return (open == '(' && close == ')') ||
		       (open == '{' && close == '}') ||
		       (open == '[' && close == ']');
	}

    public static void main(String[] args) {
        String expr1 = "{[()]}";
		if (areParenthesesBalanced(expr1)) {
			System.out.println("The parentheses are balanced. ");
		} else {
			System.out.println("The parentheses are not balanced");
		}
		System.out.println(expr1 + " -> " + areParenthesesBalanced(expr1)); // true
		System.out.println();

        String expr2 = "({a+b*c/([d+e-(f+g)*(a+b)])*(f-g+b)}/(d-e-(a+b)))";
		if (areParenthesesBalanced(expr2)) {
			System.out.println("The parentheses are balanced. ");
		} else {
			System.out.println("The parentheses are not balanced");
		}
		System.out.println(expr2 + " -> " + areParenthesesBalanced(expr2)); // true
    }
}

