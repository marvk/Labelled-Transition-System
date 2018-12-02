package net.marvk.lts.model.ctl.ctlLexer;

public enum CTLLexemeTokenType implements CTLTokenType {
    IDENTIFIER("identifier"),
    UNKNOWN("unknown character");

    private final String lexeme;

    CTLLexemeTokenType(final String lexeme) {
        this.lexeme = lexeme;
    }

    @Override
    public String lexeme() {
        return lexeme;
    }
}
