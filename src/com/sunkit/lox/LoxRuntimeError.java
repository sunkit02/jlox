package com.sunkit.lox;

public class LoxRuntimeError extends RuntimeException{
    final Token token;
    LoxRuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
