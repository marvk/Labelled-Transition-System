package net.marvk.lts.compiler.parser.syntaxtree;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
public class StateNode implements Node {
    private final String name;

    public StateNode(final String name) {
        this.name = name;
    }

    @Override
    public void accept(final TreeVisitor treeVisitor) {
        treeVisitor.accept(this, name);
    }
}
