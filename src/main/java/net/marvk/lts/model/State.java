package net.marvk.lts.model;

import java.util.Objects;

public class State {
    private final String representation;

    public State(final String representation) {
        this.representation = Objects.requireNonNull(representation);
    }

    public String getRepresentation() {
        return representation;
    }

    @Override
    public String toString() {
        return "State{" +
                "representation='" +  + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final State state = (State) o;

        return representation != null ? representation.equals(state.representation) : state.representation == null;
    }

    @Override
    public int hashCode() {
        return representation != null ? representation.hashCode() : 0;
    }
}
