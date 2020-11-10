package ch.fhnw.cpib.lexer.tokens;

public class Flowmode extends Token {

    private final Attr attr;

    public enum Attr {
        IN, INOUT, OUT
    }

    public Flowmode(Attr attr) {
        super(Terminal.FLOWMODE);
        this.attr = attr;
    }

    @Override
    public String toString() {
        return "Flowmode(" + attr + ')';
    }
}
