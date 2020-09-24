package ch.fhnw.cpib.scanner.tokens;

public class OperatorToken extends Token {
    private final String operator;

    public OperatorToken(String operator) {
        super("OPERATOR");
        this.operator = operator;
    }

    String getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return "(" + super.toString() + "," + getOperator() + ")";
    }
}
