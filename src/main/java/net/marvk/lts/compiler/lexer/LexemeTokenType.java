package net.marvk.lts.compiler.lexer;

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
