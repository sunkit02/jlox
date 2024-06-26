package com.sunkit.lox;

import java.util.List;
import java.util.Map;

public class LoxClass extends LoxInstance implements LoxCallable {
    private final String name;
    private final LoxClass superclass;
    private final Map<String, LoxFunction> methods;

    LoxClass(String name, LoxClass superclass, Map<String, LoxFunction> methods) {
        super(null);
        this.name = name;
        this.superclass = superclass;
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public LoxFunction findMethod(String name) {
        LoxFunction method = methods.get(name);
        if (method == null && superclass != null) {
            method = superclass.findMethod(name);
        }

        return method;
    }

    public LoxFunction findStaticMethod(Token name) {
        Object value = super.get(name);
        if (!(value instanceof LoxFunction method)) {
            Lox.error(name, "Can only call static methods on a Class.");
            return null;
        }

        return method;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int arity() {
        LoxFunction initializer = findMethod("init");
        if (initializer == null) return 0;
        return initializer.arity();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        LoxFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }

        return instance;
    }
}
