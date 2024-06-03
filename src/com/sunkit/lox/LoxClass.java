package com.sunkit.lox;

import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable {
    private final String name;
    private final Map<String, LoxFunction> methods;

    LoxClass(String name, Map<String, LoxFunction> methods) {
        this.name = name;
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public LoxFunction findMethod(String name) {
        return methods.get(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        return instance;
    }
}
