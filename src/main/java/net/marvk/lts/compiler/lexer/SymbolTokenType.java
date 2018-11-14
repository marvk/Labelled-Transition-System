package net.marvk.lts.compiler.lexer;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SymbolTokenType implements TokenType {
    EQUALS("="),
    ARROW("->"),
    COMMA(","),
    SEMICOLON(";"),
    LEFT_PARENTHESIS("("),
    RIGHT_PARENTHESIS(")"),
    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),
    PIPE("|");

    private static final Map<Character, SymbolTokenType> CHARACTER_TYPE_MAP =
            Arrays.stream(values())
                  .filter(t -> t.lexeme.length() == 1)
                  .collect(Collectors.toMap(
                          t -> t.lexeme.charAt(0),
                          Function.identity())
                  );


    private final String lexeme;

    SymbolTokenType(final String lexeme) {
        this.lexeme = lexeme;
    }

    public String getLexeme() {
        return lexeme;
    }

    public static SymbolTokenType singleCharTokenType(final char c) {
        return CHARACTER_TYPE_MAP.get(c);
    }

    @Override
    public String lexeme() {
        return lexeme;
    }
}
