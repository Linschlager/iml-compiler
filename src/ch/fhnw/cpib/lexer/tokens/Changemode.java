package ch.fhnw.cpib.lexer.tokens;

public class Changemode extends Token {
    private final Attr attr;

    public enum Attr {
        CONST, VAR
    }

    public Changemode(Attr attr) {
        super(Terminal.CHANGEMODE);
        this.attr = attr;
    }

    @Override
    public String toString() {
        return "Changemode(" + attr + ')';
    }
}
