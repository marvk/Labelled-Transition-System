package net.marvk.lts;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.MutableGraph;
import net.marvk.lts.model.LabeledTransitionSystem;
import net.marvk.lts.model.State;
import net.marvk.lts.model.Symbol;
import net.marvk.lts.model.Transition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class Main {
    private Main() {
        throw new AssertionError("No instances of class " + Main.class);
    }

    public static void main(String[] args) throws IOException {
        final State off = new State("off");
        final State low = new State("low");
        final State high = new State("high");
        final State rel = new State("rel");
        final State pr = new State("pr");

        final Symbol p = new Symbol("p");
        final Symbol h = new Symbol("h");
        final Symbol r = new Symbol("r");

        final LabeledTransitionSystem lamp = new LabeledTransitionSystem(off,
                new Transition(off, p, low),
                new Transition(low, p, off),
                new Transition(low, h, high),
                new Transition(high, p, off)
        );

        final LabeledTransitionSystem button = new LabeledTransitionSystem(rel,
                new Transition(rel, p, pr),
                new Transition(pr, r, rel),
                new Transition(rel, h, rel)
        );

        final LabeledTransitionSystem labeledTransitionSystem = lamp.parallelComposition(button);

        final Path folder = Paths.get("results");
        Files.createDirectories(folder);
        final Path file = Paths.get("result" + LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + ".gml");
        Files.write(folder.resolve(file), labeledTransitionSystem.toGml().getBytes());

        /*
        * HERE Starts the graph vizualization
        * */

        MutableGraph g = Factory.mutGraph("lamp").setDirected(true).add(
                Factory.mutNode("off").add(Color.RED).addLink(Factory.mutNode("b"))
        );

        Graphviz.fromGraph(g).width(200).render(Format.PNG).toFile(new File("example/lamp.png"));
    }
}
