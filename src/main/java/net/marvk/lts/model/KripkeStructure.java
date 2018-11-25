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
     * */
    private HashMap<State, Set<AtomicProposition>> labelingFunction;

    /**
     *
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

        /*for(final State s: this.states){
            for(final KSTransition t: transitionRelation){
                if (t.getStartState() != s)
                    throw new IllegalArgumentException("There exists no outgoing transition " +
                            "for State " + s + "! This relation IS NOT left-total.");
            }
        }*/

        for (final State initialState: this.initialStates){
            if (!states.contains(initialState)){
                throw new IllegalArgumentException("Initial State " +  initialState + " not in set of states");
            }
        }

        for (final KSTransition transition: transitionRelation){
            if (!states.contains(transition.getStartState())){
                throw new IllegalArgumentException("Start state " + transition.getStartState() + " of transition " +
                        transition + " not in set of states.");
            }
            if (!states.contains(transition.getGoalState())){
                throw new IllegalArgumentException("Goal state " + transition.getGoalState() + " of transition " +
                        transition + " not in set of states.");
            }
        }
        this.labelingFunction = labelingFunction;
        for(final State s: this.states){
            if (!labelingFunction.containsKey(s)) {
                throw new IllegalArgumentException("There is no entry for state " + s);
            }
        }
    }

    public String getName(){
        return this.name;
    }

    public Set<State> getStates(){
        return this.states;
    }

    public Set<State> getInitialStates(){
        return this.initialStates;
    }

    public Set<KSTransition> getTransitionRelation(){
        return this.transitionRelation;
    }

    @Override
    public String toString(){
        return "KripkeStructure{" +
                "initialStates=" + initialStates +
                ", states=" + states +
                ", transitionRelation=" + transitionRelation +
                "}";
    }

    /**
     * Checks if the Transition relation is left-total.
     * */
    public boolean checkLeftTotality(){
        for(final State s: this.states){
            if (transitionRelContainsStartState(s)){
                continue;
            }else{
                System.out.println("There is no transition from " + s +
                        " to another state from States.\nThis transition relation IS NOT left-total!");
                return false;
            }
        }
        return true;
    }

    private boolean transitionRelContainsStartState(final State state){
        for (final KSTransition t: transitionRelation){
            if (t.getStartState().equals(state))
                return true;
        }
        return false;
    }

    private boolean transitionRelContainsGoalState(final State state){
        for (final KSTransition t: transitionRelation){
            if (t.getGoalState().equals(state)){
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


/*
*
* S = EX(exp) | EG(exp) | EU(exp) | S | !S | exp
* exp = exp or exp | exp und exp | !exp | AP | true | false
* AP
*
* */