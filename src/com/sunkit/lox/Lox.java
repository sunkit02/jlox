package com.sunkit.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class Lox {
    private static final Interpreter interpreter = new Interpreter();
    private static boolean hadError = false;
    private static boolean hadRuntimeError = false;

    /**
     * Determine whether to output error messages to `stderr`
     */
    private static boolean reportError = true;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1 ) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String file) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(file));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code
        if (hadError) System.exit(65);
        if (hadRuntimeError) System.exit(70);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            runInteractive(line);
            // Reset error so not to kill the interactive prompt on error
            hadError = false;
            hadRuntimeError = false;
        }
    }

    private static void runInteractive(String src) {
        // Temporarily stop error reporting since we parse the user input
        // as an expression unchecked, and it could be a statement or malformed
        // input.
        reportError = false;

        Scanner scanner = new Scanner(src);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        Optional<Expr> expr = parser.tryParseExpression();

        if (expr.isEmpty()) {
            // Turn on error reporting for statement parsing to catch all the remaining
            // errors. This will catch all errors since statement parsing is implemented
            // as a superset of expression parsing.
            reportError = true;
            hadError = false;
            run(src);
            return;
        }

        try {
            Object value = interpreter.evaluate(expr.get());
            System.out.println(interpreter.stringify(value));
        } catch (LoxRuntimeError error) {
            System.err.println(error.getMessage());
        }
    }


    private static void run(String src) {
        Scanner scanner = new Scanner(src);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // Stop if there was a syntax error
        if (hadError) return;

        interpreter.interpret(statements);
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    private static void report(int line, String where, String message) {
        // Only report error when `reportError` flag is on
        if (reportError) {
            System.err.printf("[line %d] Error%s: %s%n", line, where, message);
        }
        hadError = true;
    }

    static void runtimeError(LoxRuntimeError error) {
        System.err.printf("%s\n[line %d]%n", error.getMessage(), error.token.line);
        hadRuntimeError = true;
    }
}
