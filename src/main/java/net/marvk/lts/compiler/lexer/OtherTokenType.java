package net.marvk.lts.compiler.lexer;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
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
