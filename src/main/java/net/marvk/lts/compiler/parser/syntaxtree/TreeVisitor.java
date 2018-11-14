package net.marvk.lts.compiler.parser.syntaxtree;

import java.util.Set;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
interface TreeVisitor<T> {
    T result();

    void accept(final LtsNode node, final NameNode nameNode, final Set<StateNode> initialStates, final Set<AssignNode> assignNodes);

    void accept(final NameNode node, final String name);

    void accept(final StateNode node, final String name);

    void accept(final SymbolNode node, final String name);

    void accept(final AssignNode assignNode, final StateNode stateNode, final TransitionNode node);

    void accept(final TerminalTransitionNode terminalTransitionNode, final SymbolNode symbolNode, final StateNode stateNode);

    void accept(final NestedTransitionNode transitionNode, final SymbolNode symbolNode, final TransitionNode terminalTransitionNode);

    default void accept(final Node node) {
        node.accept(this);
    }
}
