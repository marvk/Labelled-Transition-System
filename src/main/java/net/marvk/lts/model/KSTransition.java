package net.marvk.lts.model;

import java.util.Objects;

public class KSTransition {

    private final State startState;
    private final State goalState;


    public KSTransition(final State startState, final State goalState) {
        this.startState = Objects.requireNonNull(startState);
        this.goalState = Objects.requireNonNull(goalState);
    }

    public State getStartState() {
        return startState;
    }
    public State getGoalState(){
        return goalState;
    }

    @Override
    public String toString(){
        return "(" + startState + ", " + goalState + ")";
    }

    @Override
    public boolean equals (final Object o){

        if (o == null || this.getClass() != o.getClass())
            return false;

        final KSTransition other = (KSTransition) o;
        if (!Objects.equals(this.startState, other.startState))
            return false;
        if (!Objects.equals(this.goalState, other.goalState))
            return  false;

        return (this == o);
    }
}
