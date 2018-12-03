package net.marvk.lts.model.ctl.ctlParser;

import net.marvk.lts.model.ctl.ctlLexer.CTLToken;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class CTLNode {
    final CTLToken token;
    List<CTLNode> children;

    public CTLNode(CTLToken token, List<CTLNode> children) {
        this.token = Objects.requireNonNull(token);
        this.children = Objects.requireNonNullElse(children, new LinkedList<CTLNode>());
    }

    public CTLNode(CTLToken token) {
        this(token, null);
    }

    public CTLToken getToken() {
        return token;
    }

    public List<CTLNode> getChildren() {
        return children;
    }

    public void setChildren(List<CTLNode> children) {
        this.children = children;
    }

    public void addChild(CTLNode child) {
        this.children.add(child);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("'" + this.token.getLexeme() + "'");

        if (!children.isEmpty()) {
            result.append('[');


            for (CTLNode child : children) {
                result.append(child.toString() + ",");

            }
            result.append("]");
        }
        return result.toString();
    }

}
