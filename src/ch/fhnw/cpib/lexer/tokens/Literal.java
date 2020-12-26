package ch.fhnw.cpib.lexer.tokens;

public class Literal extends Token {

    public enum Attr {
        NUMBER, BOOL
    }

    public final Attr attr;
    public final String numberValue; // Needs to be String to support int1024
    public final boolean boolValue;

    public Literal(String value) {
        super(Terminal.LITERAL);
        this.attr = Attr.NUMBER;
        this.numberValue = value;
        this.boolValue = false;
    }
    public Literal(boolean value) {
        super(Terminal.LITERAL);
        this.attr = Attr.BOOL;
        this.numberValue = null;
        this.boolValue = value;
    }

    @Override
    public String toString() {
        return "Literal(" + (attr == Attr.NUMBER ? numberValue : boolValue) + ')';
    }
}
