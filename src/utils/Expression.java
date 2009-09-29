/*
 * Expression.java
 * This file is part of HandyGeocaching.
 *
 * HandyGeocaching is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * (read more at: http://www.gnu.org/licenses/gpl.html)
 */
package utils;

import java.util.Stack;

/**
 * Tato trida vypocita zadany vyraz vcetne zavorek atd. Pouziva se k vypoctu vzorcu MultiSolveru
 * Expression.java - this program coverts an infix expression to a postfix expression, and evaluates it. Infix expressions must be typed with spaces between all numbers and operators.
 * Author: Alexis Dekle
 */
public class Expression
{
    private static Stack operators = new Stack();
    private static Stack operands = new Stack();
    
    public Expression()
    {
    }
    
    /**
     * Prevede vyraz ve tvaru infix na tvar postfix, ktery uz se pak jednoduse vypocita
     */
    public static String toPostfix(String infix)
    //converts an infix expression to postfix
    {
        //smazani mezer
        infix = removeSpaces(infix);
        //pridani mezer
        int pos = 0;
        StringBuffer temp = new StringBuffer();
        while(pos < infix.length())
        {
            temp.append(infix.charAt(pos));
            if (Character.isDigit(infix.charAt(pos)))
            {
                while(true)
                {
                    if ((pos+1)<infix.length() && Character.isDigit(infix.charAt(pos+1)))
                    {
                      pos++;
                      temp.append(infix.charAt(pos));
                    }
                    else
                        break;
                }
            }
            if (pos != infix.length()-1)
                temp.append(' ');
            pos++;
        }
        String[] tokens = tokenize(temp.toString());
        //divides the input into tokens
        String symbol, postfix = "";
        for (int i=0;i<tokens.length;i++)
        {
            symbol = tokens[i];
            //if it's a number, add it to the string
            if (Character.isDigit(symbol.charAt(0)))
                postfix = postfix + " " + (Integer.parseInt(symbol));
            else if (symbol.equals("("))
                //push (
            {
                Character operator = new Character('(');
                operators.push(operator);
            }
            else if (symbol.equals(")"))
                //push everything back to (
            {
                while (((Character)operators.peek()).charValue() != '(')
                {
                    postfix = postfix + " " + operators.pop();
                }
                operators.pop();
            }
            else
                //print operators occurring before it that have greater precedence
            {
                while (!operators.empty() && !(operators.peek()).equals("(") && prec(symbol.charAt(0)) <= prec(((Character)operators.peek()).charValue()))
                    postfix = postfix + " " + operators.pop();
                Character operator = new Character(symbol.charAt(0));
                operators.push(operator);
            }
        }
        while (!operators.empty())
            postfix = postfix + " " + operators.pop();
        return postfix;
    }
    
    /**
     * Tato metoda provadi samotny vypocet
     */
    public static int evaluate(String expression)
    {
        String postfix = toPostfix(expression);
        String[] tokens = tokenize(postfix.trim());
        //divides the input into tokens
        int value;
        String symbol;
        for (int i=0;i<tokens.length;i++)
        {
            symbol = tokens[i];
            if (Character.isDigit(symbol.charAt(0)))
                //if it's a number, push it
            {
                Integer operand = new Integer(Integer.parseInt(symbol));
                operands.push(operand);
            }
            else //if it's an operator, operate on the previous two operands
            {
                int op2 = ((Integer)operands.pop()).intValue();
                int op1 = ((Integer)operands.pop()).intValue();
                int result = 0;
                switch(symbol.charAt(0))
                {
                    case '*':
                    {result = op1 * op2; break;}
                    case '+':
                    {result = op1 + op2; break;}
                    case '-':
                    {result = op1 - op2; break;}
                    case '/':
                    {result = op1 / op2; break;}
                    case '%':
                    {result = op1 % op2; break;}
                    case '^':
                    {result = pow(op1,op2); break;}
                }
                Integer operand = new Integer(result);
                operands.push(operand);
            }
        }
        value = ((Integer)operands.pop()).intValue();
        return value;
    }
    
    /**
     * Vrati prioritu daneho operatoru
     */
    public static int prec(char x)
    {
        if (x == '+' || x == '-')
            return 1;
        if (x == '*' || x == '/' || x == '%')
            return 2;
        if (x == '^')
            return 3;
        return 0;
    }
    
    /**
     * Tato metoda nahrazuje StringTokenizer, rozdeli dany retezec obsahujici mezery do pole stringu
     */
    public static String[] tokenize(String s)
    {
        //rozparsovani do pole
        int length = 0;
        for (int i=0;i<s.length();i++)
        {
            if (s.charAt(i)==' ')
                length++;
        }
        String[] array = new String[length+1];
        int index = 0;
        for (int i=0;i<s.length();i++)
        {
            if (s.charAt(i)!=' ')
            {
                int j;
                for (j=i;j<s.length();j++)
                    if (s.charAt(j)==' ')
                        break;
                array[index] = s.substring(i,j);
                i=j;
                index++;
            }
        }
        return array;
    }
    
    /**
     * Odstrani z daneho retezce vsechny mezery
     */
    private static String removeSpaces(String s)
    {
        StringBuffer temp = new StringBuffer();
        int i = 0;
        for(;i<s.length();i++)
        {
            if(s.charAt(i) == ' ')
                continue;
            temp.append(s.charAt(i));
        }
        return temp.toString();
    }
    
    /**
     * J2ME nema mocneni, zde je jeho implementace
     */
    public static int pow(int x, int y)
    {
        int result = 1;
        for (int i=0;i<y;i++)
        {
            result = result * x;
        }
        return result;
    }
}