package net.marvk.lts.compiler.parser.syntaxtree;

public abstract class Node {
    abstract void accept(final TreeVisitor<?> treeVisitor);
}
