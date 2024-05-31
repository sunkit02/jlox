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
        List<Token> tokens = tokenize(new String(bytes, Charset.defaultCharset()));
        run(tokens);

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

    /**
     * Used to run source code input from REPL (interactive) mode. First evaluates the input as an expression then tries to run
     * as statements when the expression evaluation fails. Parsing errors in the initial expression evaluation
     * phase will not be reported to users.
     * @param src Source code input as a String
     */
    // Implement direct expression evaluation in REPL mode (challenge 8.1)
    private static void runInteractive(String src) {
        // Temporarily stop error reporting since we parse the user input
        // as an expression unchecked, and it could be a statement or malformed
        // input.
        reportError = false;

        List<Token> tokens = tokenize(src);
        Parser parser = new Parser(tokens);

        Optional<Expr> expr = parser.tryParseExpression();

        if (expr.isEmpty()) {
            // Turn on error reporting for statement parsing to catch all the remaining
            // errors. This will catch all errors since statement parsing is implemented
            // as a superset of expression parsing.
            reportError = true;

            // Reset parsing error states to enter statement parsing in a clean slate
            hadError = false;

            // Pass the tokens to the run() method where statements are expected
            run(tokens);
            return;
        }

        try {
            Object value = interpreter.evaluate(expr.get());
            System.out.println(interpreter.stringify(value));
        } catch (LoxRuntimeError error) {
            System.err.println(error.getMessage());
        }
    }

    private static List<Token> tokenize(String src) {
        return new Scanner(src).scanTokens();
    }


    private static void run(List<Token> tokens) {
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();

        // Stop if there was a syntax error
        if (hadError) return;

        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);

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
