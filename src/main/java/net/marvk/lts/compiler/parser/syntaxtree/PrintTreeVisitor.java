package net.marvk.lts.compiler.parser.syntaxtree;

import java.util.Set;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
public class PrintTreeVisitor extends TreeVisitor {
    private int depth;

    public PrintTreeVisitor() {
        this.depth = 0;
    }

    @Override
    void accept(final LtsNode node, final NameNode nameNode, final Set<StateNode> initialStates, final Set<AssignNode> assignNodes) {
        print(node);
        depth++;
        nameNode.accept(this);
        initialStates.forEach(stateNode -> stateNode.accept(this));
        assignNodes.forEach(assignNode -> assignNode.accept(this));
        depth--;
    }

    @Override
    void accept(final NameNode node, final String name) {
        print(node);
        depth++;
        print(name);
        depth--;
    }

    @Override
    void accept(final StateNode node, final String name) {
        print(node);
        depth++;
        print(name);
        depth--;
    }

    @Override
    void accept(final SymbolNode node, final String name) {
        print(node);
        depth++;
        print(name);
        depth--;
    }

    @Override
    void accept(final AssignNode assignNode, final StateNode stateNode, final TransitionNode transitionNode) {
        print(assignNode);
        depth++;
        stateNode.accept(this);
        transitionNode.accept(this);
        depth--;
    }

    @Override
    void accept(final TerminalTransitionNode terminalTransitionNode, final SymbolNode symbolNode, final StateNode goalStateNode) {
        print(terminalTransitionNode);
        depth++;
        symbolNode.accept(this);
        goalStateNode.accept(this);
        depth--;
    }

    @Override
    void accept(final NestedTransitionNode transitionNode, final SymbolNode symbolNode, final TransitionNode goalTransitionNode) {
        print(transitionNode);
        depth++;
        symbolNode.accept(this);
        goalTransitionNode.accept(this);
        depth--;
    }

    private void print(final Node node) {
        System.out.println("-".repeat(depth) + node.getClass().getSimpleName());
    }

    private void print(final String node) {
        System.out.println("-".repeat(depth) + node);
    }
}
