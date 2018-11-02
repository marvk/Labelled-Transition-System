package net.marvk.lts.compiler.parser.syntaxtree;

import java.util.List;
import java.util.Set;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
public class LtsNode implements Node {
    private final NameNode nameNode;
    private final Set<StateNode> initialStates;
    private final Set<TransitionNode> transitionNodes;

    public LtsNode(final NameNode nameNode, final List<StateNode> initialStates, final List<TransitionNode> transitionNodes) {
        this.nameNode = nameNode;
        this.initialStates = Set.copyOf(initialStates);
        this.transitionNodes = Set.copyOf(transitionNodes);
    }

    @Override
    public void accept(final TreeVisitor treeVisitor) {
        treeVisitor.accept(this, nameNode, initialStates, transitionNodes);
    }
}
