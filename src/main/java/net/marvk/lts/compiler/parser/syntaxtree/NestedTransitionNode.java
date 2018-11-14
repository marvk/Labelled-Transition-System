package net.marvk.lts.compiler.parser.syntaxtree;

public class NestedTransitionNode extends TransitionNode {

    private final SymbolNode symbolNode;
    private final TransitionNode transitionNode;

    public NestedTransitionNode(final SymbolNode symbolNode, final TransitionNode transitionNode) {
        this.symbolNode = symbolNode;
        this.transitionNode = transitionNode;
    }

    @Override
    void accept(final TreeVisitor<?> treeVisitor) {
        treeVisitor.accept(this, symbolNode, transitionNode);
    }
}
