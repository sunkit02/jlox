package com.sunkit.lox;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    Environment() {
        this.enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null) return enclosing.get(name);

        throw new LoxRuntimeError(name, String.format("Undefined variable '%s'.", name.lexeme));
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new LoxRuntimeError(name, String.format("Undefined variable '%s'.", name.lexeme));
    }

    public Environment getEnclosing() {
        return enclosing;
    }

    public void setEnclosing(Environment enclosing) {
        this.enclosing = enclosing;
    }
}
