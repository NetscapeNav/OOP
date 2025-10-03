package org.example;

import org.expression.*;

public class ParseExpressions {
    private final String input;
    private int pos;

    public ParseExpressions(String input) {
        this.input = input;
        this.pos = 0;
    }

    public Expression parse() {
        Expression result = parseExpression();
        skipSpaces();
        if (pos < input.length()) {
            throw new RuntimeException("Unexpected characters at end of input: '" + input.substring(pos) + "'");
        }
        return result;
    }

    private Expression parseExpression() {
        /* 1. Пропускаем пробелы
         * 2. Проверяем символ
         *  2.1. Если цифра — Number. Считываем все цифры и преобразуем
         *  2.2. Если буква — Variable. Считываем все буквы и цифры
         *  2.3. Если скобка — сложное выражение
         *      2.3.1. Пропускаем (
         *      2.3.2. Рекурсивно разбираем левое выражение
         *      2.3.3. Считываем оператор
         *      2.3.4. Рекурсивно разбираем правое выражение
         *      2.3.5. Пропускаем )
         *      2.3.6. Возвращаем объект соответствующего класса
         */

        skipSpaces();
        if (pos >= input.length()) {
            throw new RuntimeException("Unexpected end of input");
        }
        char ch = input.charAt(pos);

        if (Character.isDigit(ch)) {
            return parseNumber();
        } else if (Character.isLetter(ch)) {
            return parseVariable();
        } else if (ch == '(') {
            return parseInternalExpression();
        } else {
            throw new RuntimeException("Unexpected character: '" + ch + "' at position " + pos);
        }
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

    private Expression parseInternalExpression() {
        pos++;
        skipSpaces();
        Expression left = parseExpression();
        skipSpaces();
        if (pos >= input.length()) {
            throw new RuntimeException("Unexpected end of input after left expression");
        }
        char op = input.charAt(pos);
        if (!isValidOperator(op)) {
            throw new RuntimeException("Expected operator (+, -, *, /) but found: '" + op + "' at position " + pos);
        }
        pos++;
        skipSpaces();
        Expression right = parseExpression();
        skipSpaces();
        if (pos >= input.length() || input.charAt(pos) != ')') {
            throw new RuntimeException("Expected ')' but found: " +
                    (pos < input.length() ? "'" + input.charAt(pos) + "'" : "end of input") + " at position " + pos);
        }
        pos++;

        switch (op) {
            case '+': return new Add(left, right);
            case '-': return new Sub(left, right);
            case '*': return new Mul(left, right);
            case '/': return new Div(left, right);
            default:  throw new RuntimeException("Unknown operator: " + op);
        }
    }

    private boolean isValidOperator(char op) {
        return op == '+' || op == '-' || op == '*' || op == '/';
    }

    private void skipSpaces() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }
}
