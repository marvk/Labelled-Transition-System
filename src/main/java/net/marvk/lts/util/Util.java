package net.marvk.lts.util;

import guru.nidi.graphviz.engine.Engine;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import net.marvk.lts.compiler.parser.ParseException;
import net.marvk.lts.compiler.parser.Parser;
import net.marvk.lts.compiler.parser.syntaxtree.LtsNode;
import net.marvk.lts.compiler.parser.syntaxtree.LtsTreeVisitor;
import net.marvk.lts.model.LabeledTransitionSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Util {
    private Util() {
        throw new AssertionError("No instances of utility class " + Util.class);
    }

    public static void renderLts(final LabeledTransitionSystem labeledTransitionSystem, final Path folder, final Engine engine) throws IOException {
        Graphviz.fromGraph(labeledTransitionSystem.generateMutableGraph())
                .engine(engine)
                .render(Format.PNG)
                .toFile(folder.resolve(labeledTransitionSystem.getName() + ".png").toFile());
    }

    public static void renderLts(final LabeledTransitionSystem labeledTransitionSystem, final Path folder) throws IOException {
        renderLts(labeledTransitionSystem, folder, Engine.CIRCO);
    }

    public static LabeledTransitionSystem parseLts(final Path path) throws ParseException, IOException {
        final String input = String.join("\n", Files.readAllLines(path));

        final LtsNode tree = new Parser(input).parse();

        return new LtsTreeVisitor(tree).result();
    }
}
