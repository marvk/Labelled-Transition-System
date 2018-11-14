package net.marvk.lts.compiler.lexer;

public enum OtherTokenType implements TokenType {
    EOF("end of file");

    private final String lexeme;

    OtherTokenType(final String lexeme) {
        this.lexeme = lexeme;
    }

    @Override
    public String lexeme() {
        return lexeme;
    }
}
