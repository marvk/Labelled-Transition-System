package net.marvk.lts.model.ctl.ctlLexer;

import net.marvk.lts.compiler.lexer.Token;

public class CTLToken {
    private final CTLTokenType type;
    private String lexeme;

    public CTLToken(final CTLTokenType tokenType, final String lexeme) {
        this.type = tokenType;
        this.lexeme = lexeme;
    }

    public CTLToken(final CTLSymbolTokenType tokenType){
        this(tokenType, tokenType.lexeme());
    }

    public CTLToken(final CTLLexemeTokenType tokenType, final String lexeme) {
        this.type = tokenType;
        this.lexeme = lexeme;
    }

    public CTLToken(final CTLLexemeTokenType tokenType){
        this(tokenType, tokenType.lexeme());
    }

    public CTLTokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLength() {
        return lexeme.length();
    }

    @Override
    public String toString() {
        return "CTLToken{" +
                "type=" + type +
                ", lexeme='" + lexeme + '\'' +
                '}';
    }
}
