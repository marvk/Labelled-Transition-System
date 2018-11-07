package net.marvk.lts.compiler.parser.syntaxtree;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
public class NestedTransitionNode implements TransitionNode {

    private final SymbolNode symbolNode;
    private final TransitionNode transitionNode;

    public NestedTransitionNode(final SymbolNode symbolNode, final TransitionNode transitionNode) {
        this.symbolNode = symbolNode;
        this.transitionNode = transitionNode;
    }

    @Override
    public void accept(final TreeVisitor treeVisitor) {
        treeVisitor.accept(this, symbolNode, transitionNode);
    }
}
