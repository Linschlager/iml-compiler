package ch.fhnw.cpib.scanner.tokens;

public class LiteralToken extends Token {
    private final byte valueIndex;

    private final int intValue;
    private final boolean booleanValue;

    public LiteralToken(boolean value) {
        super("LITERAL");

        this.booleanValue = value;
        this.intValue = 0; // Init IntValue with default value

        this.valueIndex = 1;
    }

    public LiteralToken(int value) {
        super("LITERAL");

        this.intValue = value;
        this.booleanValue = false; // Init BooleanValue with default value

        this.valueIndex = 0;
    }

    public int getIntValue() {
        return intValue;
    }
    public boolean getBooleanValue() { return booleanValue; }
    @Override
    public String toString() {
        return "("+super.toString()+","+(valueIndex == 0 ? getIntValue() : getBooleanValue())+")";
    }
}
