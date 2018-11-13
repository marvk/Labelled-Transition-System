package net.marvk.lts.model;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.to;


public class LabeledTransitionSystem {
    /**
     * S
     */
    private final Set<State> states;

    /**
     * I, I &isin; S
     */
    private final Set<State> initialStates;

    /**
     * &Sigma;
     */
    private final Set<Symbol> alphabet;

    /**
     * T, T &sube; S &times; &Sigma; &times; S
     */
    private final Set<Transition> transitions;

    public LabeledTransitionSystem(final Collection<State> states,
                                   final Collection<State> initialStates,
                                   final Collection<Symbol> alphabet,
                                   final Collection<Transition> transitions) {
        this.states = Set.copyOf(states);
        this.initialStates = Set.copyOf(initialStates);

        for (final State initialState : this.initialStates) {
            if (!states.contains(initialState)) {
                throw new IllegalArgumentException("Initial state " + initialState + " not in set of states");
            }
        }

        this.alphabet = Set.copyOf(alphabet);
        this.transitions = Set.copyOf(transitions);

        for (final Transition transition : this.transitions) {
            if (!states.contains(transition.getStartState())) {
                throw new IllegalArgumentException("Start state " + transition.getStartState() + " of transition " + transition + " not in set of states");
            }

            if (!alphabet.contains(transition.getSymbol())) {
                throw new IllegalArgumentException("Symbol " + transition.getSymbol() + " of transition " + transition + " not in alphabet");
            }

            if (!states.contains(transition.getGoalState())) {
                throw new IllegalArgumentException("Goal state " + transition.getGoalState() + " of transition " + transition + " not in set of states");
            }
        }
    }

    public LabeledTransitionSystem(final Collection<State> initialStates, final Collection<Transition> transitions) {
        this(generateStates(initialStates, transitions), initialStates, generateAlphabet(transitions), transitions);
    }

    public LabeledTransitionSystem(final Collection<State> initialStates, final Transition... transitions) {
        this(initialStates, Arrays.asList(transitions));
    }

    public LabeledTransitionSystem(final State initialState, final Collection<Transition> transitions) {
        this(Set.of(initialState), transitions);
    }

    public LabeledTransitionSystem(final State initialState, final Transition... transitions) {
        this(initialState, Arrays.asList(transitions));
    }

    public Set<State> getStates() {
        return states;
    }

    public Set<State> getInitialStates() {
        return initialStates;
    }

    public Set<Symbol> getAlphabet() {
        return alphabet;
    }

    public Set<Transition> getTransitions() {
        return transitions;
    }

    private static List<Symbol> generateAlphabet(final Collection<Transition> transitions) {
        return transitions.stream().map(Transition::getSymbol).collect(Collectors.toList());
    }

    private static Set<State> generateStates(final Collection<State> initialStates, final Collection<Transition> transitions) {
        return Stream.concat(
                initialStates.stream(),
                transitions.stream()
                           .flatMap(transition -> Stream.of(transition.getStartState(), transition.getGoalState())))
                     .collect(Collectors.toSet());
    }

    public LabeledTransitionSystem parallelComposition(final LabeledTransitionSystem other) {
        final Set<Symbol> h = intersection(this.alphabet, other.alphabet);
        final Set<Symbol> thisWithoutOther = relativeComplement(this.alphabet, other.alphabet);
        final Set<Symbol> otherWithoutThis = relativeComplement(other.alphabet, this.alphabet);

        final Set<State> initialStates = new HashSet<>();

        for (final State s1 : this.initialStates) {
            for (final State s2 : other.initialStates) {
                initialStates.add(new CompositeState(s1, s2));
            }
        }

        final Set<Transition> transitions = new HashSet<>();

        for (final Symbol symbol : h) {
            for (final Transition t1 : this.transitions) {
                if (t1.getSymbol().equals(symbol)) {
                    for (final Transition t2 : other.transitions) {
                        if (t2.getSymbol().equals(symbol)) {
                            final State startState = new CompositeState(t1.getStartState(), t2.getStartState());
                            final State goalState = new CompositeState(t1.getGoalState(), t2.getGoalState());

                            transitions.add(new Transition(startState, symbol, goalState));
                        }
                    }
                }
            }
        }

        transitions.addAll(generateUnsynchronizedTransitions(thisWithoutOther, this.transitions, other.states, false));
        transitions.addAll(generateUnsynchronizedTransitions(otherWithoutThis, other.transitions, this.states, true));

        final Set<State> states = generateStates(initialStates, transitions);

        // Remove unreachable transitions
        while (true) {
            final boolean removal = states.removeIf(
                    // No incoming transitions
                    state -> transitions.stream().noneMatch(transition -> transition.getGoalState().equals(state))
            );

            if (!removal) {
                break;
            }

            transitions.removeIf(transition -> !states.contains(transition.getStartState()));
        }

        return new LabeledTransitionSystem(initialStates, transitions);
    }

    private static Set<Transition> generateUnsynchronizedTransitions(
            final Set<Symbol> thisWithoutOtherSymbols,
            final Set<Transition> thisTransitions,
            final Set<State> otherStates,
            final boolean switched) {
        final Set<Transition> transitions = new HashSet<>();

        for (final Symbol symbol : thisWithoutOtherSymbols) {
            for (final Transition t1 : thisTransitions) {
                if (t1.getSymbol().equals(symbol)) {
                    for (final State state : otherStates) {
                        final State startState = createCompositeState(t1.getStartState(), state, switched);
                        final State goalState = createCompositeState(t1.getGoalState(), state, switched);

                        transitions.add(new Transition(startState, symbol, goalState));
                    }
                }
            }
        }

        return transitions;
    }

    private static State createCompositeState(final State state1, final State state2, final boolean switched) {
        if (switched) {
            return new CompositeState(state2, state1);
        }
        return new CompositeState(state1, state2);
    }

    private static <T> Set<T> intersection(final Set<T> setA, final Set<T> setB) {
        final Set<T> result = new HashSet<>(setA);
        result.retainAll(setB);
        return Collections.unmodifiableSet(result);
    }

    private static <T> Set<T> relativeComplement(final Set<T> setA, final Set<T> setB) {
        final Set<T> result = new HashSet<>(setA);
        result.removeAll(setB);
        return Collections.unmodifiableSet(result);
    }

    @Override
    public String toString() {
        return "LabeledTransitionSystem{" +
                "initialStates=" + initialStates +
                ", transitions=" + transitions +
                '}';
    }

    public String toGml() {
        final StringJoiner stringJoiner = new StringJoiner("\n");

        stringJoiner.add("graph [");
        stringJoiner.add("directed 1");
        stringJoiner.add("hierarchic 1");

        final AtomicInteger nodeId = new AtomicInteger();

        final HashMap<State, Integer> stateIdMap = new HashMap<>();

        for (final State state : states) {
            final int id = nodeId.getAndIncrement();
            stateIdMap.put(state, id);
            stringJoiner.add("node [");
            stringJoiner.add("id " + id);
            stringJoiner.add("label \"" + state.getRepresentation() + "\"");
            stringJoiner.add("]");
        }

        for (final Transition transition : transitions) {
            stringJoiner.add("edge [");
            stringJoiner.add("source " + stateIdMap.get(transition.getStartState()));
            stringJoiner.add("target " + stateIdMap.get(transition.getGoalState()));
            stringJoiner.add("label \"" + transition.getSymbol() + "\"");
            stringJoiner.add("]");
        }

        stringJoiner.add("]");

        return stringJoiner.toString();
    }

    public MutableGraph generateMutableGraph(String name){
        final MutableGraph graph = mutGraph(name).setDirected(true);
        graph.linkAttrs().add(Style.BOLD, Color.BLACK);
        final Map<State, MutableNode> stateMutableNodeMap = states.stream()
                .collect(Collectors.toMap(Function.identity(),
                        state -> Factory.mutNode(state.getRepresentation()).add(Color.BLACK, Shape.CIRCLE, Label.of(state.getRepresentation()))));

        //Make all initial States Red
        initialStates.forEach(state -> stateMutableNodeMap.get(state).add(Color.RED));

        for (final Transition t: transitions){
            final MutableNode start = stateMutableNodeMap.get(t.getStartState());
            final MutableNode goal = stateMutableNodeMap.get(t.getGoalState());
            start.links().add(to(goal).with(Label.of(t.getSymbol().getRepresentation())));
        }
        states.forEach(state -> graph.add(stateMutableNodeMap.get(state).asLinkSource()));

        return graph;
    }
}
