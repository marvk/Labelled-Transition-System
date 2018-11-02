package net.marvk.lts.compiler.parser.syntaxtree;

import java.util.Set;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
interface TreeVisitor {
    void accept(final LtsNode node, final NameNode nameNode, final Set<StateNode> initialStates, final Set<TransitionNode> transitionNodes);

    void accept(final NameNode node, final String name);

    void accept(final StateNode node, final String name);

    void accept(final SymbolNode node, final String name);

    void accept(final AssignNode assignNode, final StateNode stateNode, final TransitionNode node);

    void accept(final TerminalTransitionNode terminalTransitionNode, final SymbolNode symbolNode, final StateNode stateNode);

    void accept(final TransitionNode transitionNode, final SymbolNode symbolNode, final TransitionNode terminalTransitionNode);

    default void accept(final Node node) {
        node.accept(this);
    }
}
