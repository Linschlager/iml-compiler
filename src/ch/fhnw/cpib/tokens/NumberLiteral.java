package ch.fhnw.cpib.tokens;

public class NumberLiteral extends Token {

    public final int value;

    public NumberLiteral(int value) {
        super(Terminal.NUMBER_LITERAL);
        this.value = value;
    }

    @Override
    public String toString() {
        return "NumberLiteral(" + value + ')';
    }
}
