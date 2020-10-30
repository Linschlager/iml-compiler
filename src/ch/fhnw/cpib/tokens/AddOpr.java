package ch.fhnw.cpib.tokens;

public class AddOpr extends Token {
    private final Attr attr;

    public enum Attr {
        PLUS,MINUS;
    }
    public AddOpr(Attr attr) {
        super(Terminal.ADDOPR);
        this.attr = attr;
    }

    @Override
    public String toString() {
        return "AddOpr(" + attr + ')';
    }
}
