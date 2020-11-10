package ch.fhnw.cpib.lexer.tokens;

public class BoolOpr extends Token {

    private final Attr attr;

    public enum Attr {
        CAND, COR;
    }


    public BoolOpr(Attr attr) {
        super(Terminal.BOOLOPR);
        this.attr = attr;
    }

    @Override
    public String toString() {
        return "BoolOpr(" + attr + ')';
    }
}
