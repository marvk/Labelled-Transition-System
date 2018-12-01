package net.marvk.lts.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

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
        this.transitionRelation = Set.copyOf(transitionRelation);

        for (final State s : this.states) {
            boolean hasOutRelation = false;
            for (KSTransition t : transitionRelation) {
                if (t.getStartState() == s) {
                    hasOutRelation = true;
                    break;
                }
            }
            if (!hasOutRelation) {
                throw new IllegalArgumentException("There exists no outgoing transition " +
                        "for State " + s.toString() + "! This relation IS NOT left-total.");
            }

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
        this.labelingFunction = labelingFunction;
        for (final State s : this.states) {
            if (!labelingFunction.containsKey(s)) {
                throw new IllegalArgumentException("There is no entry for state " + s);
            }

        }
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
            boolean hasOutRelation = false;
            for (KSTransition t : ksTransition) {
                if (t.getStartState() == s) {
                    hasOutRelation = true;
                    break;
                }
            }
            if (!hasOutRelation) {
                System.out.println("There is no transition from " + s +
                        " to another state from States.\nThis transition relation IS NOT left-total!");
                return false;
            }

        }
        return true;
    }

    private static String defaultName() {
        return "KS" + LocalDateTime.now()
                .toEpochSecond(ZoneOffset.UTC);
    }
}


/*
 *
 * S = EX(exp) | EG(exp) | EU(exp) | S | !S | exp
 * exp = exp or exp | exp und exp | !exp | AP | true | false
 * AP
 *
 * */