package ch.fhnw.cpib.lexer.tokens;

public class Mechmode extends Token {

    private final Attr attr;

    public enum Attr {
        COPY, REF
    }

    public Mechmode(Attr attr) {
        super(Terminal.MECHMODE);
        this.attr = attr;
    }

    @Override
    public String toString() {
        return "Mechmode(" + attr + ')';
    }
}
