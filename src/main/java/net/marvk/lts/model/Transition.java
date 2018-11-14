package net.marvk.lts.model;

import java.util.Objects;

public class Transition {
    private final State startState;
    private final State goalState;
    private final Symbol symbol;

    public Transition(final State startState, final Symbol symbol, final State goalState) {
        this.startState = Objects.requireNonNull(startState);
        this.goalState = Objects.requireNonNull(goalState);
        this.symbol = Objects.requireNonNull(symbol);
    }

    public State getStartState() {
        return startState;
    }

    public State getGoalState() {
        return goalState;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return "(" + startState + ", " + symbol + ", " + goalState + ')';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Transition other = (Transition) o;

        if (!Objects.equals(startState, other.startState)) {
            return false;
        }

        if (!Objects.equals(goalState, other.goalState)) {
            return false;
        }

        return Objects.equals(symbol, other.symbol);
    }

    @Override
    public int hashCode() {
        int result = startState != null ? startState.hashCode() : 0;
        result = 31 * result + (goalState != null ? goalState.hashCode() : 0);
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        return result;
    }

}
