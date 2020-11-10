package ch.fhnw.cpib.tokens;

public class Type extends Token {
    private final Attr attr;

    public enum Attr {
        BOOL, INT32, INT64, INT1024
    }
    public Type(Attr attr) {
        super(Terminal.TYPE);
        this.attr = attr;
    }

    @Override
    public String toString() {
        return "Type(" + attr + ')';
    }
}
