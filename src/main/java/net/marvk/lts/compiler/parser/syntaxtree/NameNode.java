package net.marvk.lts.compiler.parser.syntaxtree;

public class NameNode extends Node {
    private final String name;

    public NameNode(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    void accept(final TreeVisitor<?> treeVisitor) {
        treeVisitor.accept(this, name);
    }
}
