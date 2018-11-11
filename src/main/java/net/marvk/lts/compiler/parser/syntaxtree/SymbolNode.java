package net.marvk.lts.compiler.parser.syntaxtree;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
public class SymbolNode extends Node {
    private final String name;

    public SymbolNode(final String name) {
        this.name = name;
    }

    @Override
    void accept(final TreeVisitor<?> treeVisitor) {
        treeVisitor.accept(this, name);
    }
}