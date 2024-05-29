package com.sunkit.lox;

public class LoopControlException extends RuntimeException {
    Token keyword;
    LoopControlException(Token keyword) {
        this.keyword = keyword;
    }
}
