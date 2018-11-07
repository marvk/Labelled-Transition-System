package net.marvk.lts.compiler.parser;

import net.marvk.lts.compiler.parser.syntaxtree.LtsNode;
import net.marvk.lts.compiler.parser.syntaxtree.PrintTreeVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
public final class Main {

    private Main() {
        throw new AssertionError("No instances of class " + Main.class);
    }

    public static void main(final String[] args) throws IOException, ParseException {
        final Path path = Paths.get("src/main/resources/net/marvk/lts/compiler/test1.lts");

        final String input = String.join("\n", Files.readAllLines(path));

        final LtsNode ltsNode = new Parser(input).parse();

        ltsNode.accept(new PrintTreeVisitor());
    }
}
