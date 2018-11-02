package net.marvk.lts.compiler.parser.syntaxtree;

import net.marvk.lts.model.Symbol;
import net.marvk.lts.model.Transition;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
public class TransitionNode implements Node {

    private final SymbolNode symbolNode;
    private final TransitionNode transitionNode;

    public TransitionNode(final SymbolNode symbolNode, final TransitionNode transitionNode) {
        this.symbolNode = symbolNode;
        this.transitionNode = transitionNode;
    }

    @Override
    public void accept(final TreeVisitor treeVisitor) {
        treeVisitor.accept(this, symbolNode, transitionNode);
    }
}
