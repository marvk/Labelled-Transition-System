package net.marvk.lts.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompositeState extends State {
    public CompositeState(final List<State> states) {
        this(states.stream());
    }

    public CompositeState(final State... states) {
        this(Arrays.stream(states));
    }

    private CompositeState(final Stream<State> states) {
        super(states.map(State::getRepresentation).collect(Collectors.joining("+")));
    }
}
