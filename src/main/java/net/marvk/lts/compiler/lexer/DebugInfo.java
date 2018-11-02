package net.marvk.lts.compiler.lexer;

/**
 * Created on 2018-11-02.
 *
 * @author Marvin Kuhnke
 */
public class DebugInfo {
    private final int line;
    private final int pos;
    private final String content;

    public DebugInfo(final int line, final int pos, final String content) {
        this.line = line;
        this.pos = pos;
        this.content = content;
    }

    public int getLine() {
        return line;
    }

    public int getPos() {
        return pos;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "DebugInfo{" +
                "line=" + line +
                ", pos=" + pos +
                ", content='" + content + '\'' +
                '}';
    }
}
