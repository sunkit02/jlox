package com.sunkit.challenges;

import com.sunkit.lox.Expr;
import com.sunkit.lox.Token;
import com.sunkit.lox.TokenType;

/*
    Challenge of defining a visitor class for the syntax tree classes that converts
    an expression into its reversed Polish notation
 */
public class RpnExprPrinter implements Expr.Visitor<String> {

    public String convertRpn(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        String left = expr.left.accept(this);
        String right = expr.right.accept(this);
        return left + " " + right + " " + expr.operator.lexeme;
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return expr.expression.accept(this);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        return expr.value == null ? "nil" : expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return expr.operator.lexeme + expr.right;
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name.lexeme;
    }

    public static void main(String[] args) {
        // Expr representation of: (1 + 2) * (4 - 3)
        Expr expr = new Expr.Binary(
                new Expr.Grouping(
                        new Expr.Binary(
                                new Expr.Literal(1),
                                new Token(TokenType.PLUS, "+", null, 1),
                                new Expr.Literal(2))
                ),
                new Token(TokenType.PLUS, "*", null, 1),
                new Expr.Grouping(
                        new Expr.Binary(
                                new Expr.Literal(4),
                                new Token(TokenType.PLUS, "-", null, 1),
                                new Expr.Literal(3))
                )
        );

        System.out.println(new RpnExprPrinter().convertRpn(expr));
    }
}