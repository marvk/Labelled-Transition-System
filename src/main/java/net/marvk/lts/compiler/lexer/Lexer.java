package net.marvk.lts.compiler.lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
public class Lexer {
    private final List<String> input;
    private final List<String> debugInput;
    private int line;
    private int linePos;
    private char currentChar;

    private Lexer(final String input) {
        this.debugInput = input.lines().collect(Collectors.toList());
        this.input = debugInput.stream()
                               .map(String::strip)
                               .map(s -> s.concat("\n"))
                               .collect(Collectors.toList());

        this.line = 0;
        this.linePos = 0;
        this.currentChar = input.isEmpty()
                ? 0
                : input.charAt(0);
    }

    private void advance() {
        linePos += 1;

        if (linePos >= currentLine().length()) {
            linePos = 0;

            do {
                line += 1;
            } while (line < input.size() && currentLine().isEmpty());

            if (line >= input.size()) {
                currentChar = 0;
                return;
            }
        }

        currentChar = currentLine().charAt(linePos);
    }

    private Token scan() {
        while (isCurrentCharNotZero()) {
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }

            if (currentChar == '#') {
                skipComment();
                continue;
            }

            if (Character.isAlphabetic(currentChar)) {
                return parseIdentifier();
            }

            if ('-' == currentChar) {
                advance();

                if ('>' == currentChar) {
                    advance();
                    return new Token(SymbolTokenType.ARROW, debugInfo());
                }
            } else {
                final SymbolTokenType symbolTokenType = SymbolTokenType.singleCharTokenType(currentChar);

                if (symbolTokenType != null) {
                    advance();
                    return new Token(symbolTokenType, debugInfo());
                }
            }

            final Token result = new Token(LexemeTokenType.UNKNOWN, debugInfo(), Character.toString(currentChar));

            advance();

            return result;
        }

        return new Token(OtherTokenType.EOF, debugInfo());
    }

    private boolean isCurrentCharNotZero() {
        return currentChar != 0;
    }

    private void skipWhitespace() {
        do {
            advance();
        } while (isCurrentCharNotZero() && Character.isWhitespace(currentChar));
    }

    private void skipComment() {
        do {
            advance();
        } while (isCurrentCharNotZero() && currentChar != '\n');

        advance();
    }

    private Token parseIdentifier() {
        final StringBuilder result = new StringBuilder();

        do {
            result.append(currentChar);
            advance();
        } while (isCurrentCharNotZero() && (Character.isAlphabetic(currentChar) || Character.isDigit(currentChar)));

        return new Token(LexemeTokenType.IDENTIFIER, debugInfo(), result.toString());
    }

    private DebugInfo debugInfo() {
        return new DebugInfo(line, linePos, currentLineDebug());
    }

    private String currentLine() {
        return input.get(line);
    }

    private String currentLineDebug() {
        if (line >= debugInput.size()) {
            return "";
        }

        return debugInput.get(line);
    }

    public static Stream<Token> tokenize(final String s) {
        final Lexer lexer = new Lexer(s);

        return Stream.iterate(lexer.scan(), token -> token.getType() != OtherTokenType.EOF, unused -> lexer.scan());
    }

    public static void main(final String[] args) throws IOException {
        final Path path = Paths.get("src/main/resources/net/marvk/lts/compiler/test1.lts");

        final String input = String.join("\n", Files.readAllLines(path));
        Lexer.tokenize(input).forEach(System.out::println);
    }
}
