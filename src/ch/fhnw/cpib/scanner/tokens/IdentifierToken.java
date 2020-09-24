package ch.fhnw.cpib.scanner.tokens;

public class IdentifierToken extends Token {
    private final String value;

    public IdentifierToken(String value) {
        super("IDENT");
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "("+super.toString()+","+getValue()+")";
    }
}
