import java.util.Stack;

class InfixEvaluator {

    // Method to return precedence of operators
    private static int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return 0;
        }
    }

    // Method to apply operator to two operands
    private static int applyOp(int a, int b, char op) {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': return a / b;
        }
        return 0;
    }

    // Method to evaluate infix expression
    public static int evaluateInfix(String expression) {
        Stack<Integer> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            // Skip spaces
            if (ch == ' ') continue;

            // If digit, handle multi-digit numbers
            if (Character.isDigit(ch)) {
                int num = 0;
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    num = num * 10 + (expression.charAt(i) - '0');
                    i++;
                }
                i--; // adjust for loop increment
                values.push(num);
            }
            // If '(', push to ops stack
            else if (ch == '(') {
                ops.push(ch);
            }
            // If ')', solve entire bracket
            else if (ch == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(values.pop(), values.pop(), ops.pop()));
                }
                ops.pop(); // remove '('
            }
            // If operator
            else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(ch)) {
                    values.push(applyOp(values.pop(), values.pop(), ops.pop()));
                }
                ops.push(ch);
            }
        }

        // Apply remaining operators
        while (!ops.isEmpty()) {
            values.push(applyOp(values.pop(), values.pop(), ops.pop()));
        }

        return values.pop();
    }

    public static void main(String[] args) {
        String infixExpression = "(2*3)+(5*4)-9";
        int result = evaluateInfix(infixExpression);
        
        System.out.println("Result of Infix Expression: " + result);
    }
}

