import java.util.Stack;

class PostfixEvaluator {
    public static int evaluatePostfix(String expression) {
        Stack<Integer> stack = new Stack<>();

        for (char ch : expression.toCharArray()) {
            if (Character.isDigit(ch)) {
                stack.push(ch - '0'); // convert char digit to int
            } else {
                int b = stack.pop();
                int a = stack.pop();
                int result = 0;

                switch (ch) {
                    case '+':
                        result = a + b;
                        break;
                    case '-':
                        result = a - b;
                        break;
                    case '*':
                        result = a * b;
                        break;
                    case '/':
                        result = a / b;
                        break;
                }
		// push the result back to the stack
                stack.push(result);
            }
        }
	// The final result will be the only element in the stack
        return stack.pop();
    }

    public static void main(String[] args) {
        String postfixExpression = "23*54*+9-"; 
		// Example: (2 * 3) + (5 * 4) - 9
        int result = evaluatePostfix(postfixExpression);
        System.out.println("The result of the postfix expression is: " + result);
    }
}

