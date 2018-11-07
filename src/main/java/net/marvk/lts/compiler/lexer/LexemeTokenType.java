package net.marvk.lts.compiler.lexer;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
public enum LexemeTokenType implements TokenType {
    IDENTIFIER("identifier"),
    UNKNOWN("unknown character");

    private final String lexeme;

    LexemeTokenType(final String lexeme) {
        this.lexeme = lexeme;
    }

    @Override
    public String lexeme() {
        return lexeme;
    }
}
