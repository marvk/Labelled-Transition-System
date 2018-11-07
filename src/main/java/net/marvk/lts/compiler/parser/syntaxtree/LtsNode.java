package net.marvk.lts.compiler.parser.syntaxtree;

import java.util.Collection;
import java.util.Set;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
public class LtsNode implements Node {
    private final NameNode nameNode;
    private final Set<StateNode> initialStates;
    private final Set<AssignNode> assignNodes;

    public LtsNode(final NameNode nameNode, final Collection<StateNode> initialStates, final Collection<AssignNode> assignNodes) {
        this.nameNode = nameNode;
        this.initialStates = Set.copyOf(initialStates);
        this.assignNodes = Set.copyOf(assignNodes);
    }

    @Override
    public void accept(final TreeVisitor treeVisitor) {
        treeVisitor.accept(this, nameNode, initialStates, assignNodes);
    }
}
