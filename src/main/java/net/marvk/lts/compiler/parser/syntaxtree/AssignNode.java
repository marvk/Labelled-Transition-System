package net.marvk.lts.compiler.parser.syntaxtree;

public class AssignNode extends Node {
    private final StateNode stateNode;
    private final TransitionNode transitionNode;

    public AssignNode(final StateNode stateNode, final TransitionNode transitionNode) {
        this.stateNode = stateNode;
        this.transitionNode = transitionNode;
    }

    @Override
    void accept(final TreeVisitor<?> treeVisitor) {
        treeVisitor.accept(this, stateNode, transitionNode);
    }
}
