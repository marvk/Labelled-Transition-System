package net.marvk.lts.compiler.parser;

import net.marvk.lts.compiler.lexer.DebugInfo;
import net.marvk.lts.compiler.lexer.Token;
import net.marvk.lts.compiler.lexer.TokenType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParseException extends Exception {
    public ParseException(final Token token, final TokenType... expected) {
        this(token, Arrays.asList(expected));
    }

    public ParseException(final Token token, final TokenType expected) {
        super(getMessage(token, expected.lexeme()));
    }

    public ParseException(final Token peek, final List<TokenType> expected) {
        super(getMessage(peek, expected.stream().map(TokenType::lexeme).collect(Collectors.joining(", "))));
    }

    private static String getMessage(final Token token, final String expected) {
        final DebugInfo debugInfo = token.getDebugInfo();

        return "\nParsing error in line " + debugInfo.getLine() + ":\n" +
                debugInfo.getContent() + "\n" +
                " ".repeat(debugInfo.getPos()) + "~".repeat(token.getLength()) + "\n" +
                "expected " + expected + " and found " + token.getLexeme();
    }
}
