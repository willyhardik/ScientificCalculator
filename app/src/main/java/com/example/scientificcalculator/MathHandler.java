package com.example.scientificcalculator;

import java.util.Arrays;
import java.util.Stack;

public class MathHandler {

    public static final double PI=Math.PI;
    public static final double E=Math.E;

    public static double add(double x, double y) {
        try {
            return x+y;
        }
        catch(Exception e) {

        }
        return -1;
    }

    public static double sub(double x, double y) {
        try {
            return x-y;
        }
        catch(Exception e) {

        }
        return -1;
    }

    public static double mul(double x, double y) {
        try {
            return x*y;
        }
        catch(Exception e) {

        }
        return -1;
    }

    public static double mod(double x, double y) {
        try {
            return x%y;
        }
        catch(Exception e) {

        }
        return -1;
    }


    public static double div(double x, double y) {
        try {
            return x/y;
        }
        catch(Exception e) {

        }
        return -1;
    }


    public static double pow(double x, double y) {
        try {
            return Math.pow(x,y);
        }
        catch(Exception e) {

        }
        return -1;
    }

    public static double sin(double x) {
        try {
            return Math.sin(x);
        }
        catch(Exception e) {

        }
        return -1;
    }

    public static double cos(double x) {
        try {
            return Math.cos(x);
        }
        catch(Exception e) {

        }
        return -1;
    }

    public static double tan(double x) {
        try {
            return Math.tan(x);
        }
        catch(Exception e) {

        }
        return -1;
    }


    public static double asin(double x) {
        try {
            return Math.asin(x);
        }
        catch(Exception e) {

        }
        return -1;
    }

    public static double acos(double x) {
        try {
            return Math.acos(x);
        }
        catch(Exception e) {

        }
        return -1;
    }

    public static double atan(double x) {
        try {
            return Math.atan(x);
        }
        catch(Exception e) {

        }
        return -1;
    }

    public static double rand() {
        try {
            return Math.random();
        }
        catch(Exception e) {

        }
        return -1;
    }

    public static double log(double x, double y) {
        try {
            if(x<0) {
                //Generate Error
                return -1;
            }
            return Math.log(x)/Math.log(y);
        }
        catch(Exception e) {

        }
        return -1;
    }

    public static long fact(long x) {
        try {
            if(x<0) {
                //Generate Error
                return -1;
            }
            if(x==0) {
                return 1;
            }
            long fact=1;
            for(int i=1;i<=x;i++) {
                fact=fact*i;
            }
            return fact;
        }
        catch(Exception e) {

        }
        return -1;
    }



    public static double evaluate(String expression)
    {
        char[] tokens = expression.toCharArray();

        System.out.println(Arrays.toString(tokens));

        // Stack for numbers: 'values'
        Stack<Double> values = new Stack<>();

        // Stack for Operators: 'ops'
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < tokens.length; i++)
        {
            // Current token is a whitespace, skip it
            if (tokens[i] == ' ')
                continue;

            // Current token is a number, push it to stack for numbers
            if ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.')
            {
                StringBuffer sbuf = new StringBuffer();
                // There may be more than one digits in number
                while (i < tokens.length && ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.')) {
                    sbuf.append(tokens[i++]);
                }
                values.push(Double.parseDouble(sbuf.toString()));
            }

            // Current token is an opening brace, push it to 'ops'
            else if (tokens[i] == '(')
                ops.push(tokens[i]);

                // Closing brace encountered, solve entire brace
            else if (tokens[i] == ')')
            {
                while (ops.peek() != '(')
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                ops.pop();
            }

            // Current token is an operator.
            else if (tokens[i] == '+' || tokens[i] == '-' ||
                    tokens[i] == '*' || tokens[i] == '/')
            {
                // While top of 'ops' has same or greater precedence to current
                // token, which is an operator. Apply operator on top of 'ops'
                // to top two elements in values stack
                while (!ops.empty() && hasPrecedence(tokens[i], ops.peek()))
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));

                // Push current token to 'ops'.
                ops.push(tokens[i]);
            }
        }

        // Entire expression has been parsed at this point, apply remaining
        // ops to remaining values
        while (!ops.empty())
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));

        // Top of 'values' contains result, return it
        return values.pop();
    }

    // Returns true if 'op2' has higher or same precedence as 'op1',
    // otherwise returns false.
    public static boolean hasPrecedence(char op1, char op2)
    {
        if (op2 == '(' || op2 == ')')
            return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        else
            return true;
    }

    // A utility method to apply an operator 'op' on operands 'a'
    // and 'b'. Return the result.
    public static double applyOp(char op, double b, double a)
    {
        switch (op)
        {
            case '+':
                return MathHandler.add(a,b);
            case '-':
                return MathHandler.sub(a,b);
            case '*':
                return MathHandler.mul(a,b);
            case '/':
                return MathHandler.div(a,b);
        }
        return 0;
    }



}
