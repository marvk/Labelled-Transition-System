package net.marvk.lts.model;

import java.util.Objects;

public class AtomicProposition {

    private final String representation;

    public AtomicProposition(final String representation) {
        this.representation = Objects.requireNonNull(representation);
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

        final AtomicProposition ap = (AtomicProposition) o;

        return Objects.equals(representation, ap.representation);
    }

    @Override
    public int hashCode() {
        return representation != null ? representation.hashCode() : 0;

    }
}
