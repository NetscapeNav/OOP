package org.example;

import org.example.exception.ParsingException;
import org.expression.*;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class ExpressionParser {
    private String input;
    private int pos;

    private static final Map<Character, Class<? extends Expression>> OPERATOR_MAP = Map.of(
            '+', Add.class,
            '-', Sub.class,
            '*', Mul.class,
            '/', Div.class
    );

    public Expression parse(String input) throws ParsingException {
        this.input = input;
        this.pos = 0;
        Expression result = parseExpression();
        skipSpaces();
        if (pos < input.length()) {
            throw new ParsingException("Unexpected characters at end of input: '" + input.substring(pos) + "'");
        }
        return result;
    }

    private Expression parseExpression() throws ParsingException {
        Expression left = parseTerm();
        skipSpaces();
        while (pos < input.length()) {
            char op = input.charAt(pos);
            if (op == '+' || op == '-') {
                pos++;
                Expression right = parseTerm();
                if (op == '+') {
                    left = new Add(left, right);
                } else {
                    left = new Sub(left, right);
                }
            } else {
                break;
            }
        }
        return left;
    }

    private Expression parseTerm() throws ParsingException {
        Expression left = parseFactor();
        skipSpaces();
        while (pos < input.length()) {
            char op = input.charAt(pos);
            if (op == '*' || op == '/') {
                pos++;
                Expression right = parseFactor();
                if (op == '*') {
                    left = new Mul(left, right);
                } else {
                    left = new Div(left, right);
                }
            } else {
                break;
            }
        }

        return left;
    }

    private Expression parseFactor() throws ParsingException {
        skipSpaces();
        char ch = input.charAt(pos);
        if (Character.isDigit(ch)) return parseNumber();
        if (Character.isLetter(ch)) return parseVariable();
        if (ch == '(') {
            pos++;
            Expression result = parseExpression();
            skipSpaces();
            if (pos >= input.length() || input.charAt(pos) != ')') {
                if (pos < input.length()) {
                    throw new ParsingException("Expected operator (+, -, *, /) but found: '" + input.charAt(pos) + "' at position " + pos);
                }
                throw new ParsingException("Expected ')'");
            }
            pos++;
            return result;
        }

        throw new ParsingException("Unexpected character: '" + ch + "' at position " + pos);
    }

    private Expression parseNumber() {
        int start = pos;
        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            pos++;
        }
        int value = Integer.parseInt(input.substring(start, pos));
        return new org.expression.Number(value);
    }

    private Expression parseVariable() {
        int start = pos;
        while (pos < input.length() && Character.isLetterOrDigit(input.charAt(pos))) {
            pos++;
        }
        String value = input.substring(start, pos);
        return new Variable(value);
    }

    private void skipSpaces() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }
}
