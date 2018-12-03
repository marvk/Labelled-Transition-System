package net.marvk.lts.model.ctl.ctlLexer;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CTLSymbolTokenType implements CTLTokenType {
    ONE("1"),
    ZERO("0"),
    E("E"),
    X("X"),
    G("G"),
    U("U"),
    LEFT_BRACKET("["),
    RIGHT_BRACKET("]"),
    NOT("¬"),
    OR("∨"),
    AND("∧");

    private static final Map<Character, CTLSymbolTokenType> CHARACTER_CTL_TOKEN_TYPE_MAP =
            Arrays.stream(values())
                    .filter(t -> t.lexeme.length() == 1)
                    .collect(Collectors.toMap(
                            t -> t.lexeme.charAt(0),
                            Function.identity())
                    );


    private final String lexeme;

    CTLSymbolTokenType(final String s) {
        lexeme = s;
    }

    public String getLexeme() {
        return lexeme;
    }

    public static CTLSymbolTokenType singleCharTokenType(final char c) {
        return CHARACTER_CTL_TOKEN_TYPE_MAP.get(c);
    }

    public static boolean containsCharacter(final char c){
        return CHARACTER_CTL_TOKEN_TYPE_MAP.containsKey(c);
    }
    @Override
    public String lexeme() {
        return lexeme;
    }
}
