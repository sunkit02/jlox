package com.sunkit.lox;

import java.util.HashSet;
import java.util.List;

public class ClosureCapturer implements Stmt.Visitor<Void>, Expr.Visitor<Void> {
    // Keeps track of declarations that are done inside the function itself, which cannot
    // be evaluated until the function is called. Useful when you want to return a value
    // declared inside the function body (like another function).
    private final HashSet<String> localDeclarations = new HashSet<>();
    private Environment closure = new Environment();
    private Environment enclosing = null;

    public Environment capture(List<Token> params, List<Stmt> body, Environment enclosing) {
        this.enclosing = enclosing;
        for (Token param : params) {
            this.localDeclarations.add(param.lexeme);
        }

        for (Stmt stmt : body) {
            stmt.accept(this);
        }
        Environment captured = this.closure;

        // Reset the field in case the instance gets reused
        this.closure = new Environment();
        this.enclosing = null;
        this.localDeclarations.clear();;

        return captured;
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        for (Stmt statement : stmt.statements) {
            statement.accept(this);
        }
        return null;
    }

    @Override
    public Void visitLoopBodyStmt(Stmt.LoopBody stmt) {
        for (Stmt statement : stmt.statements) {
            statement.accept(this);
        }
        return null;
    }

    @Override
    public Void visitLoopControlStmt(Stmt.LoopControl stmt) {
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        stmt.expression.accept(this);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        this.localDeclarations.add(stmt.name.lexeme);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        stmt.condition.accept(this);
        stmt.thenBranch.accept(this);
        stmt.elseBranch.accept(this);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        stmt.expression.accept(this);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        stmt.expr.accept(this);
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        stmt.initializer.accept(this);
        this.localDeclarations.add(stmt.name.lexeme);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        stmt.condition.accept(this);
        stmt.body.accept(this);
        return null;
    }

    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        expr.value.accept(this);
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr) {
        for (Expr argument : expr.arguments) {
            argument.accept(this);
        }
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        expr.left.accept(this);
        expr.right.accept(this);
        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        expr.expression.accept(this);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        expr.left.accept(this);
        expr.right.accept(this);
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        expr.right.accept(this);
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!this.localDeclarations.contains(expr.name.lexeme)) {
            Object value = this.enclosing.get(expr.name);
            this.closure.define(expr.name.lexeme, value);
        }
        return null;
    }
}
