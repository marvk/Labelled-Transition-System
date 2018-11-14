package net.marvk.lts.compiler.lexer;

public class Token {
    private final TokenType type;
    private final String lexeme;
    private final DebugInfo debugInfo;

    public Token(final SymbolTokenType type, final DebugInfo debugInfo) {
        this(type, debugInfo, type.getLexeme());
    }

    public Token(final LexemeTokenType type, final DebugInfo debugInfo, final String lexeme) {
        this((TokenType) type, debugInfo, lexeme);
    }

    public Token(final OtherTokenType type, final DebugInfo debugInfo) {
        this(type, debugInfo, null);
    }

    private Token(final TokenType tokenType, final DebugInfo debugInfo, final String lexeme) {
        this.type = tokenType;
        this.lexeme = lexeme;
        this.debugInfo = debugInfo;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLength() {
        return lexeme.length();
    }

    public DebugInfo getDebugInfo() {
        return debugInfo;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", lexeme='" + lexeme + '\'' +
                ", debugInfo=" + debugInfo +
                '}';
    }
}
