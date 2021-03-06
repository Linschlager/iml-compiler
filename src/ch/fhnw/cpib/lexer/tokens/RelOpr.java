package ch.fhnw.cpib.lexer.tokens;

public class RelOpr extends Token {
    public final Attr attr;

    public enum Attr {
        EQ, NE, LT, GT, LE, GE;
    }
    public RelOpr(Attr attr) {
        super(Terminal.RELOPR);
        this.attr = attr;
    }

    @Override
    public String toString() {
        return "RelOpr(" + attr + ')';
    }
}
