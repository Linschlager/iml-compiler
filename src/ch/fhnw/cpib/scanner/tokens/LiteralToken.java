package ch.fhnw.cpib.scanner.tokens;

public class LiteralToken extends Token {
    private final int value;

    public LiteralToken(int value) {
        super("LITERAL");
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    @Override
    public String toString() {
        return "("+super.toString()+","+getValue()+")";
    }
}
