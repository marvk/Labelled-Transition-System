package net.marvk.lts.model;

import java.util.Objects;

public class Symbol {
    private final String representation;

    public Symbol(final String representation) {
        this.representation = Objects.requireNonNull(representation);
    }

    public String getRepresentation() {
        return representation;
    }

    @Override
    public String toString() {
        return representation;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Symbol symbol = (Symbol) o;

        return Objects.equals(representation, symbol.representation);
    }

    @Override
    public int hashCode() {
        return representation != null ? representation.hashCode() : 0;
    }
}
