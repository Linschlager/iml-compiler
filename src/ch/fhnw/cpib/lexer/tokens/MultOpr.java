package ch.fhnw.cpib.lexer.tokens;

public class MultOpr extends Token {

    public final Attr attr;

    public enum Attr {
        TIMES, DIV_E, DIV_F, DIV_T, MOD_E, MOD_F, MOD_T;
    }

    public MultOpr(Attr attr) {
        super(Terminal.MULTOPR);
        this.attr = attr;
    }

    @Override
    public String toString() {
        return "MultOpr(" + attr + ')';
    }
}
