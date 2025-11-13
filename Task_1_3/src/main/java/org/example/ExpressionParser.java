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
            throw new ParsingException("Unexpected end of input");
        }
        char ch = input.charAt(pos);

        if (Character.isDigit(ch)) {
            return parseNumber();
        } else if (Character.isLetter(ch)) {
            return parseVariable();
        } else if (ch == '(') {
            return parseInternalExpression();
        } else {
            throw new ParsingException("Unexpected character: '" + ch + "' at position " + pos);
        }
    }

    private Expression parseInternalExpression() throws ParsingException {
        pos++;
        skipSpaces();
        Expression left = parseExpression();
        skipSpaces();
        if (pos >= input.length()) {
            throw new ParsingException("Unexpected end of input after left expression");
        }
        char op = input.charAt(pos);
        if (!OPERATOR_MAP.containsKey(op)) {
            throw new ParsingException("Expected operator (+, -, *, /) but found: '" + op + "' at position " + pos);
        }
        pos++;
        skipSpaces();
        Expression right = parseExpression();
        skipSpaces();
        if (pos >= input.length() || input.charAt(pos) != ')') {
            throw new ParsingException("Expected ')' but found: " +
                    (pos < input.length() ? "'" + input.charAt(pos) + "'" : "end of input") + " at position " + pos);
        }
        pos++;

        return createExpression(op, left, right);
    }

    private Expression createExpression(char op, Expression left, Expression right) throws ParsingException {
        Class<? extends Expression> expressionClass = OPERATOR_MAP.get(op);
        if (expressionClass == null) {
            throw new ParsingException("Unknown operator: " + op);
        }
        try {
            Constructor<? extends Expression> constructor =
                    expressionClass.getConstructor(Expression.class, Expression.class);
            return constructor.newInstance(left, right);
        } catch (Exception e) {
            throw new ParsingException("Failed to create expression for operator '" + op + "': " + e.getMessage());
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

    private void skipSpaces() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }
}
