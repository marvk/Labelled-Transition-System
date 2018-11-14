package net.marvk.lts.compiler.parser.syntaxtree;

import net.marvk.lts.model.LabeledTransitionSystem;
import net.marvk.lts.model.State;
import net.marvk.lts.model.Symbol;
import net.marvk.lts.model.Transition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LtsTreeVisitor implements TreeVisitor<LabeledTransitionSystem> {
    private final LtsNode ltsNode;
    private LabeledTransitionSystem result;
    private String name;
    private Set<State> initialStates;
    private State currentStartState;

    private final List<Transition> transitions;

    public LtsTreeVisitor(final LtsNode ltsNode) {
        this.ltsNode = ltsNode;
        this.transitions = new ArrayList<>();
    }

    @Override
    public synchronized LabeledTransitionSystem result() {
        if (result == null) {
            ltsNode.accept(this);
            this.result = new LabeledTransitionSystem(name, initialStates, transitions);
        }

        return result;
    }

    @Override
    public void accept(final LtsNode node, final NameNode nameNode, final Set<StateNode> initialStates, final Set<AssignNode> assignNodes) {
        this.initialStates = initialStates.stream()
                                          .map(StateNode::getName)
                                          .map(State::new)
                                          .collect(Collectors.toSet());

        nameNode.accept(this);
        assignNodes.forEach(this::accept);
    }

    @Override
    public void accept(final NameNode node, final String name) {
        this.name = name;
    }

    @Override
    public void accept(final StateNode node, final String name) {

    }

    @Override
    public void accept(final SymbolNode node, final String name) {

    }

    @Override
    public void accept(final AssignNode assignNode, final StateNode stateNode, final TransitionNode node) {
        currentStartState = new State(stateNode.getName());
        accept(node);
    }

    @Override
    public void accept(final TerminalTransitionNode terminalTransitionNode, final SymbolNode symbolNode, final StateNode stateNode) {
        final Symbol symbol = new Symbol(symbolNode.getName());
        final State goal = new State(stateNode.getName());

        transitions.add(new Transition(currentStartState, symbol, goal));
    }

    @Override
    public void accept(final NestedTransitionNode transitionNode, final SymbolNode symbolNode, final TransitionNode terminalTransitionNode) {
        throw new UnsupportedOperationException("Nested transitions are currently not supported");
    }
}
