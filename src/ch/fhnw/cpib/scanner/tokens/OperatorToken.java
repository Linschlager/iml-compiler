package ch.fhnw.cpib.scanner.tokens;

import ch.fhnw.cpib.scanner.LexicalError;

import java.util.HashMap;

public class OperatorToken extends Token {
    private final Operator operator;

    // List of all valid operators
    private static final HashMap<String, Operator> operatorHashMap;
    static {
        operatorHashMap = new HashMap<>();
        operatorHashMap.put("+", Operator.ADD_OP);
        operatorHashMap.put("-", Operator.SUB_OP);
        operatorHashMap.put("*", Operator.MUL_OP);

        operatorHashMap.put("divE", Operator.DIVE_OP);
        operatorHashMap.put("divF", Operator.DIVF_OP);
        operatorHashMap.put("divT", Operator.DIVT_OP);
        operatorHashMap.put("modE", Operator.MODE_OP);
        operatorHashMap.put("modF", Operator.MODF_OP);
        operatorHashMap.put("modT", Operator.MODT_OP);

        operatorHashMap.put(":=", Operator.ASSIGNMENT_OP);
    }

    public OperatorToken(String op) throws LexicalError {
        super("OPERATOR");
        if (operatorHashMap.containsKey(op)) {
            this.operator = operatorHashMap.get(op);
        } else {
            throw new LexicalError(String.format("Cannot identify operator %s", op));
        }
    }

    String getOperator() {
        return operator.toString();
    }

    @Override
    public String toString() {
        return getOperator();
    }
}
