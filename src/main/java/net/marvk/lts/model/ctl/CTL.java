package net.marvk.lts.model.ctl;


import net.marvk.lts.model.ctl.ctlLexer.CTLLexer;
import net.marvk.lts.model.ctl.ctlLexer.CTLToken;
import net.marvk.lts.model.ctl.ctlParser.CTLNode;
import net.marvk.lts.model.ctl.ctlParser.CTLParser;

import java.util.List;
import java.util.Objects;

public class CTL {

    private final String formula;

    private final CTLNode rootNode;

    public CTL(final String formula){
        this.formula = Objects.requireNonNull(formula.trim());
        List<CTLToken> tokenList = CTLLexer.tokenize(this.formula);
        this.rootNode = CTLParser.startParsing(tokenList);
    }

    public String getFormula() {
        return formula;
    }

    public CTLNode getRootNode() {
        return rootNode;
    }
}
