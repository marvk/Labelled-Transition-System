package net.marvk.lts.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class KripkeStructure {

    private final String name;
    /**
     * S
     */
    private final Set<State> states;

    /**
     * S0, initial States
     */
    private final Set<State> initialStates;

    /**
     * R
     */
    private final Set<KSTransition> transitionRelation;

    /**
     * Labeling Function
     */
    private HashMap<State, Set<AtomicProposition>> labelingFunction;

    /**
     * @param name
     * @param states
     * @param initialStates
     * @param transitionRelation
     */
    public KripkeStructure(final String name, final Collection<State> states, final Collection<State> initialStates, final Collection<KSTransition> transitionRelation, final HashMap<State, Set<AtomicProposition>> labelingFunction) {
        this.name = name == null || name.isEmpty() ? defaultName() : name;
        this.states = Objects.requireNonNull(Set.copyOf(states));
        this.initialStates = Set.copyOf(initialStates);

        Set<KSTransition> transitions = Set.copyOf(transitionRelation);
        boolean isLeftTotal = checkLeftTotality(transitions);
        if (isLeftTotal) {
            this.transitionRelation = Set.copyOf(transitionRelation);
        } else {
            this.transitionRelation = Collections.emptySet();
        }

        for (final State initialState : this.initialStates) {
            if (!states.contains(initialState)) {
                throw new IllegalArgumentException("Initial State " + initialState + " not in set of states");
            }
        }

        for (final KSTransition transition : transitionRelation) {
            if (!states.contains(transition.getStartState())) {
                throw new IllegalArgumentException("Start state " + transition.getStartState() + " of transition " +
                        transition + " not in set of states.");
            }
            if (!states.contains(transition.getGoalState())) {
                throw new IllegalArgumentException("Goal state " + transition.getGoalState() + " of transition " +
                        transition + " not in set of states.");
            }
        }
        this.labelingFunction = Objects.requireNonNull(labelingFunction);
        for (final State s : this.states) {
            if (!labelingFunction.containsKey(s)) {
                //throw new IllegalArgumentException("There is no entry for state " + s);
                //System.out.println("WARNING: There is no entry for state " + s);
                Set<AtomicProposition> ap = new HashSet<>();
                labelingFunction.put(s, ap);
            }

        }
    }

    public KripkeStructure(LabeledTransitionSystem lts) {
        this(lts.getName(), lts.getStates(), lts.getInitialStates(), lts.getKSTransitions(), lts.getLabelingAP());
    }

    public String getName() {
        return this.name;
    }

    public Set<State> getStates() {
        return this.states;
    }

    public Set<State> getInitialStates() {
        return this.initialStates;
    }

    public HashMap<State, Set<AtomicProposition>> getLabelingFunction() {
        return labelingFunction;
    }

    public Set<KSTransition> getTransitionRelation() {
        return this.transitionRelation;
    }

    @Override
    public String toString() {
        return "KripkeStructure{" +
                "initialStates=" + initialStates +
                ", states=" + states +
                ", transitionRelation=" + transitionRelation +
                "}";
    }

    /**
     * Checks if the Transition relation is left-total.
     */
    public boolean checkLeftTotality(final Set<KSTransition> ksTransition) {
        for (final State s : this.states) {
            if (!hasOutgoingTransition(ksTransition, s)) {
                System.out.println("There is no transition from " + s +
                        " to another state from States.\nThis transition relation IS NOT left-total!");
                return false;
            }
        }
        return true;
    }

    private static boolean hasOutgoingTransition(Set<KSTransition> ksTransition, State s) {
        for (KSTransition t : ksTransition) {
            if (t.getStartState().equals(s)) {
                return true;
            }
        }
        return false;
    }

    private static String defaultName() {
        return "KS" + LocalDateTime.now()
                .toEpochSecond(ZoneOffset.UTC);
    }
}