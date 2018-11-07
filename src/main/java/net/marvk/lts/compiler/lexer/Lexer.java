package net.marvk.lts.compiler.lexer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
public final class Lexer {
    private final List<String> input;
    private final List<String> debugInput;
    private int line;
    private int linePos;
    private char currentChar;

    private boolean reachedEndOfFile;

    private Lexer(final String input) {
        this.debugInput = input.lines().collect(Collectors.toList());
        this.input = debugInput.stream()
                               .map(s -> s.concat("\n"))
                               .collect(Collectors.toList());

        this.line = 0;
        this.linePos = 0;
        this.currentChar = input.isEmpty()
                ? 0
                : input.charAt(0);

        this.reachedEndOfFile = false;
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
        if (reachedEndOfFile) {
            return null;
        }

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
                    final Token result = new Token(SymbolTokenType.ARROW, debugInfo());
                    advance();
                    return result;
                }
            } else {
                final SymbolTokenType symbolTokenType = SymbolTokenType.singleCharTokenType(currentChar);

                if (symbolTokenType != null) {
                    final Token result = new Token(symbolTokenType, debugInfo());
                    advance();
                    return result;
                }
            }

            final Token result = new Token(LexemeTokenType.UNKNOWN, debugInfo(), Character.toString(currentChar));
            advance();
            return result;
        }

        this.reachedEndOfFile = true;

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

        return new Token(LexemeTokenType.IDENTIFIER, debugInfo(result.toString()), result.toString());
    }

    private DebugInfo debugInfo(final String lexeme) {
        return new DebugInfo(line, linePos - lexeme.length(), currentLineDebug());
    }

    private DebugInfo debugInfo() {
        return debugInfo("");
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

        return Stream.iterate(lexer.scan(), Objects::nonNull, unused -> lexer.scan());
    }
}
