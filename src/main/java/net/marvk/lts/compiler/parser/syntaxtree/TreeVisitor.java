package net.marvk.lts.compiler.parser.syntaxtree;

import java.util.Set;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
abstract class TreeVisitor {
    abstract void accept(final LtsNode node, final NameNode nameNode, final Set<StateNode> initialStates, final Set<AssignNode> assignNodes);

    abstract void accept(final NameNode node, final String name);

    abstract void accept(final StateNode node, final String name);

    abstract void accept(final SymbolNode node, final String name);

    abstract void accept(final AssignNode assignNode, final StateNode stateNode, final TransitionNode node);

    abstract void accept(final TerminalTransitionNode terminalTransitionNode, final SymbolNode symbolNode, final StateNode stateNode);

    abstract void accept(final NestedTransitionNode transitionNode, final SymbolNode symbolNode, final TransitionNode terminalTransitionNode);

    void accept(final Node node) {
        node.accept(this);
    }
}
