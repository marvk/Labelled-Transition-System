package net.marvk.lts.model.ctl.ctlParser;

import net.marvk.lts.model.ctl.ctlLexer.CTLLexemeTokenType;
import net.marvk.lts.model.ctl.ctlLexer.CTLSymbolTokenType;
import net.marvk.lts.model.ctl.ctlLexer.CTLToken;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CTLParser {


    public static CTLNode startParsing(List<CTLToken> ctlTokens) {
        return recursionParsing(null, ctlTokens);
    }

    private static CTLNode recursionParsing(CTLNode prevNode, List<CTLToken> tokenList) {

        if (tokenList.isEmpty() && prevNode.getToken().getType().equals( CTLSymbolTokenType.E)) {
            throw new IllegalArgumentException("CTL expression is illegal. Missing Expression after E Quantifier.");
        } else if (tokenList.isEmpty()) {
            return null;
        }

        List<Integer> orOperatorPos = findOperator(tokenList, CTLSymbolTokenType.OR);
        CTLNode boolNode = processBoolOperator(tokenList, orOperatorPos);
        if (boolNode != null) return boolNode;
        List<Integer> andOperatorPos = findOperator(tokenList, CTLSymbolTokenType.AND);
        boolNode = processBoolOperator(tokenList, andOperatorPos);
        if (boolNode != null) return boolNode;

        if (prevNode != null && prevNode.getToken().getType().equals(CTLSymbolTokenType.E)) {
            if (tokenList.get(0).getType().equals(CTLSymbolTokenType.X) || tokenList.get(0).getType().equals(CTLSymbolTokenType.G)) {
                CTLNode node = new CTLNode(tokenList.get(0));
                node.addChild(recursionParsing(node, tokenList.subList(1, tokenList.size())));
                return node;
            } else if (tokenList.get(0).getType().equals(CTLSymbolTokenType.LEFT_BRACKET)) {

                if (getClosingBracketIndex(0, tokenList) != tokenList.size() - 1) {
                    throw new IllegalArgumentException("CTL Expression is illegal. Closing bracket of U Quantifier expression in wrong position-");
                }
                List<CTLToken> tempTokenList = tokenList.subList(1, tokenList.size() - 1);

                List<Integer> indices = findOperator(tempTokenList, CTLSymbolTokenType.U);

                if (indices.isEmpty()) {
                    throw new IllegalArgumentException("CTL Expression is illegal. U Quantifier expected.");
                }

                CTLNode node = new CTLNode(tempTokenList.get(indices.get(0)));

                for (List<CTLToken> subList : splitByIndices(indices, tempTokenList)) {
                    node.addChild(recursionParsing(node, subList));
                }
                return node;

            }
        }

        if (tokenList.size() == 1 &&
                (tokenList.get(0).getType().equals(CTLLexemeTokenType.IDENTIFIER) ||
                        tokenList.get(0).getType().equals(CTLSymbolTokenType.ONE) ||
                        tokenList.get(0).getType().equals(CTLSymbolTokenType.ZERO))) {
            return new CTLNode(tokenList.get(0));
        } else if (tokenList.get(0).getType().equals(CTLSymbolTokenType.NOT)) {
            CTLNode node = new CTLNode(tokenList.get(0));
            node.addChild(recursionParsing(node, tokenList.subList(1, tokenList.size())));
            return node;
        } else if (tokenList.get(0).getType().equals(CTLSymbolTokenType.LEFT_BRACKET) && (prevNode == null || !prevNode.token.getType().equals(CTLSymbolTokenType.E))) {
            return recursionParsing(prevNode, tokenList.subList(1, getClosingBracketIndex(0, tokenList)));
        } else if (tokenList.get(0).getType().equals(CTLSymbolTokenType.E)) {
            CTLNode node = new CTLNode(tokenList.get(0));
            node.addChild(recursionParsing(node, tokenList.subList(1, tokenList.size())));
            return node;
        }


        throw new IllegalArgumentException("CTL Expression is illegal.");
    }

    private static CTLNode processBoolOperator(List<CTLToken> tokenList, List<Integer> andOperatorPos) {
        if (!andOperatorPos.isEmpty()) {
            CTLNode boolNode = new CTLNode(tokenList.get(andOperatorPos.get(0)));

            for (List<CTLToken> subList : splitByIndices(andOperatorPos, tokenList)) {
                boolNode.addChild(recursionParsing(boolNode, subList));
            }

            return boolNode;
        }
        return null;
    }


    private static List<List<CTLToken>> splitByIndices(List<Integer> indices, List<CTLToken> ctlTokens) {
        List<List<CTLToken>> result = new LinkedList<>();
        List<CTLToken> headSubList = ctlTokens.subList(0, indices.get(0));
        if (!headSubList.isEmpty()) {
            result.add(headSubList);
        }

        if (indices.size() > 1) {
            for (int i = 0; i < indices.size() - 1; i++) {
                List<CTLToken> tempSubList = ctlTokens.subList(indices.get(i) + 1, indices.get(i + 1));
                if (!tempSubList.isEmpty()) {
                    result.add(tempSubList);
                }
            }
        }
        List<CTLToken> tailSubList = ctlTokens.subList(indices.get(indices.size() - 1) + 1, ctlTokens.size());
        if (!tailSubList.isEmpty()) {
            result.add(tailSubList);
        }

        return result;
    }

    private static List<Integer> findOperator(List<CTLToken> ctlTokens, CTLSymbolTokenType operator) {
        List<Integer> result = new ArrayList<>();
        int i = 0;
        while (i < ctlTokens.size()) {
            if (ctlTokens.get(i).getType().equals(operator)) {
                result.add(i);
                i++;
                continue;
            }
            if (ctlTokens.get(i).getType().equals(CTLSymbolTokenType.LEFT_BRACKET)) {
                i = getClosingBracketIndex(i, ctlTokens);
                i++;
                continue;
            }
            i++;
        }
        return result;
    }


    private static int getClosingBracketIndex(int currentPos, List<CTLToken> ctlTokens) {
        int bracketLevel = 0;

        for (int i = currentPos + 1; i < ctlTokens.size(); i++) {
            if (bracketLevel == 0 && ctlTokens.get(i).getType().equals( CTLSymbolTokenType.RIGHT_BRACKET)) {
                return i;
            } else if (ctlTokens.get(i).getType().equals( CTLSymbolTokenType.RIGHT_BRACKET)) {
                bracketLevel--;
            }
            if (ctlTokens.get(i).getType().equals( CTLSymbolTokenType.LEFT_BRACKET)) {
                bracketLevel++;
            }
        }

        throw new IllegalArgumentException("Missing closing bracket in CTL expression");
    }

}
