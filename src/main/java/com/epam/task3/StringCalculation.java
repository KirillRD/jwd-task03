package com.epam.task3;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

/**
 * Library that calculates the value of an expression in a string
 * String with an expression can contain only such characters as: '+', '-', '*', '/', '(', ')', '.' and digits in decimal form
 * Numbers can be rational
 * The minus number can be without brackets only at the beginning of the line
 * If you try to divide by 0, there will be
 * @throws ArithmeticException
 * If something else goes wrong, there will be
 * @throws IllegalArgumentException
 * @author Kirill Ryabov
 */
public final class StringCalculation {
    /**
     * Default constructor
     */
    StringCalculation() {}

    /**
     * The main library method that counts the expression
     * @param expression string variable containing the expression
     * @return value of the expression
     */
    public final double evaluate(String expression) {
        try {
            expression = expression.replaceAll("\\s+", "");
            if (!Pattern.matches("[+\\-*/.()\\d]+" , expression)) {
                throw new IllegalArgumentException();
            }
            char[] tokens = expression.toCharArray();
            Deque<Double> values = new ArrayDeque<>();
            Deque<Character> ops = new ArrayDeque<>();

            for (int i = 0; i < tokens.length; i++) {
                if ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.') {
                    StringBuilder stringBuilder = new StringBuilder();

                    if ((i == 1 && tokens[0] == '-') || (i > 1 && tokens[i-2] == '(' && tokens[i-1] == '-')) {
                        stringBuilder.append(ops.pop());
                    }
                    if (tokens[i] >= '0' && tokens[i] <= '9') {
                        while (i < tokens.length && tokens[i] >= '0' && tokens[i] <= '9') {
                            stringBuilder.append(tokens[i++]);
                        }
                    }
                    if (i < tokens.length && tokens[i] == '.') {
                        stringBuilder.append(tokens[i++]);
                        if (i < tokens.length && tokens[i] >= '0' && tokens[i] <= '9') {
                            while (i < tokens.length && tokens[i] >= '0' && tokens[i] <= '9') {
                                stringBuilder.append(tokens[i++]);
                            }
                        }
                    }

                    if (stringBuilder.toString().equals(".")) {
                        throw new IllegalArgumentException();
                    }
                    values.push(Double.parseDouble(stringBuilder.toString()));
                    i--;
                } else if (tokens[i] == '(') {
                    ops.push(tokens[i]);
                } else if (tokens[i] == ')') {
                    while (ops.peek() != '(') {
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                    }
                    ops.pop();
                    if (!ops.isEmpty() && ops.peek() == '-') {
                        ops.pop();
                        if (!ops.isEmpty() && ops.peek() == '(') {
                            ops.pop();
                            values.push(values.pop()*(-1));
                        } else {
                            ops.push('-');
                        }
                    }

                } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                    while (!ops.isEmpty() && hasPrecedence(tokens[i], ops.peek())) {
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                    }
                    ops.push(tokens[i]);
                }
            }
            while (!ops.isEmpty()) {
                values.push(applyOp(ops.pop(), values.pop(), values.pop()));
            }

            double result = values.pop();

            if (values.isEmpty()) {
                return result;
            } else {
                throw new IllegalArgumentException();
            }
        } catch (NullPointerException | NoSuchElementException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid string");
        }
    }

    /**
     * Method that checks an arithmetic operations in an expression
     * @param op1 character from a string
     * @param op2 character from the stack with expressions
     */
    private boolean hasPrecedence(char op1, char op2) {
        if ((op2 == '(' || op2 == ')') || ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Method that calculates a value from two variables
     * @param op arithmetic operation
     * @param b second number
     * @param a first number
     * @return value from calculation
     */
    private double applyOp(char op, double b, double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("/ by zero");
                }
                return a / b;
            default:
                return 0;
        }
    }
}
