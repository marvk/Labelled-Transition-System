package net.marvk.lts.compiler.parser.syntaxtree;

import java.util.Set;
import java.util.StringJoiner;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
public class ToStringTreeVisitor extends TreeVisitor<String> {
    private final StringJoiner stringJoiner;
    private final LtsNode ltsNode;
    private String result;

    private int depth;

    public ToStringTreeVisitor(final LtsNode ltsNode) {
        super(ltsNode);

        this.stringJoiner = new StringJoiner("\n");
        this.depth = 0;
        this.ltsNode = ltsNode;
    }

    @Override
    public synchronized String result() {
        if (result == null) {
            ltsNode.accept(this);
            this.result = stringJoiner.toString();
        }

        return result;
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
        stringJoiner.add("-".repeat(depth) + node.getClass().getSimpleName());
    }

    private void print(final String node) {
        stringJoiner.add("-".repeat(depth) + node);
    }
}
