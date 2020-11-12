package ch.fhnw.cpib.lexer.tokens;

public class Literal extends Token {

    public enum Attr {
        NUMBER, BOOL
    }

    public final Attr attr;
    public final int numberValue;
    public final boolean boolValue;

    public Literal(int value) {
        super(Terminal.LITERAL);
        this.attr = Attr.NUMBER;
        this.numberValue = value;
        this.boolValue = false;
    }
    public Literal(boolean value) {
        super(Terminal.LITERAL);
        this.attr = Attr.BOOL;
        this.numberValue = 0;
        this.boolValue = value;
    }

    @Override
    public String toString() {
        return "Literal(" + (attr == Attr.NUMBER ? numberValue : boolValue) + ')';
    }
}
