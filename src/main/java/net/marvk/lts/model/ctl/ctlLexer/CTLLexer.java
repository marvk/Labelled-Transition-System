package net.marvk.lts.model.ctl.ctlLexer;


import java.util.LinkedList;
import java.util.List;

public class CTLLexer {


    public static List<CTLToken> tokenize(final String formula) {
        List<CTLToken> result = new LinkedList<>();

        formula.trim();

        int charPos = 0;

        while (charPos < formula.length()) {
            if (Character.isWhitespace(formula.charAt(charPos))) {
                charPos++;
                continue;
            }

            if (Character.isAlphabetic(formula.charAt(charPos))) {
                if (CTLSymbolTokenType.containsCharacter(formula.charAt(charPos))) {
                    result.add(new CTLToken(CTLSymbolTokenType.singleCharTokenType(formula.charAt(charPos))));
                    charPos++;
                    continue;
                } else {
                    StringBuilder identifier = new StringBuilder();

                    do {
                        identifier.append(formula.charAt(charPos));
                        charPos++;
                    }
                    while (charPos < formula.length() && (Character.isAlphabetic(formula.charAt(charPos)) || Character.isDigit(formula.charAt(charPos))));

                    result.add(new CTLToken(CTLLexemeTokenType.IDENTIFIER, identifier.toString()));

                    continue;
                }
            }

            if (CTLSymbolTokenType.containsCharacter(formula.charAt(charPos))){
                result.add(new CTLToken(CTLSymbolTokenType.singleCharTokenType(formula.charAt(charPos))));
                charPos++;
                continue;
            }

            result.add(new CTLToken(CTLLexemeTokenType.UNKNOWN, Character.toString(formula.charAt(charPos))));
            charPos++;
        }

        return result;
    }


}
