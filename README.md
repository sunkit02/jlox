# JLox

An implementation of the Lox programming language base on
Robert Nystrom's book [Crafting Interpreters](https://craftinginterpreters.com) in Java.

## Chapters Completed

- [x] Chapter 1
- [x] Chapter 2
- [x] Chapter 3
- [x] Chapter 4
- [x] Chapter 5
- [x] Chapter 6
- [x] Chapter 7
- [ ] Chapter 8
- [ ] Chapter 9
- [ ] Chapter 11
- [ ] Chapter 12
- [ ] Chapter 13
- [ ] Chapter 14
- [ ] Chapter 15
- [ ] Chapter 16
- [ ] Chapter 17
- [ ] Chapter 18
- [ ] Chapter 19
- [ ] Chapter 20
- [ ] Chapter 21
- [ ] Chapter 22
- [ ] Chapter 23
- [ ] Chapter 24
- [ ] Chapter 25
- [ ] Chapter 26
- [ ] Chapter 27
- [ ] Chapter 28
- [ ] Chapter 29
- [ ] Chapter 30

## Grammar of Current Implementation

Expressions

```
expression  -> literal
             | unary
             | binary
             | grouping ;
             
literal     -> NUMBER | STRING | "true" | "false" | "nil" ;

grouping    -> "(" expression ")" ;

unary       -> ( "-" | "!" ) expression ;

binary      -> expression operator expression ;

operator    -> "==" | "!=" | "<" | "<=" | ">" | ">="
             | "+"  | "-"  | "*" | "/" ;

# For variable access
primary     -> "true" | "false | "nil" 
             | NUMBER | STRING
             | "(" expression ")"
             | IDENTIFIER ;
```

Statements

```
program         -> declaration* EOF ;

declaration     -> varDecl
                 | statement ;
                 
varDecl         -> "var" IDENTIFIER ( "=" expression )? ";" ;

statement       -> exprStmt
                 | printStmt ;
                 
exprStmt        -> expression ";" ;

printStmt       -> "print" expression ";" ;
```

Lexical Grammar

```
NUMBER          -> DIGIT+ ( "." DIGIT* )? ;
STRING          -> "\"" <any char except "\"">* "\"" ;
IDENTIFIER      -> ALPHA ( ALPHA | DIGIT )* ;
ALPHA           -> "a" ... "z" | "A" ... "Z" | "_" ;
DIGIT           -> "0" ... "9" ;
```

## Challenges Implemented
- 7.2 Implement implicit type conversion during string concatenation
