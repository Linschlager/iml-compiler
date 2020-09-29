package ch.fhnw.cpib.scanner.tokens;

import ch.fhnw.cpib.scanner.LexicalError;
import ch.fhnw.cpib.scanner.enums.RelativeOperator;

public class RelativeOperatorToken extends Token {
    private final RelativeOperator relOp;

    /**
     *
     * @param relativeOperatorString Input String. One of <=,>=,<,>,=
     * @return RelativeOperator that is represented by the input string
     * @throws LexicalError When the input string is not one of the recognized RelativeOperators
     */
    private static RelativeOperator getRelativeOperator(String relativeOperatorString) throws LexicalError {
        switch(relativeOperatorString) {
            case ">=": return RelativeOperator.GE;
            case "<=": return RelativeOperator.LE;
            case ">": return RelativeOperator.GT;
            case "<": return RelativeOperator.LT;
            case "=": return RelativeOperator.EQ;
            default:
                throw new LexicalError(relativeOperatorString);
        }
    }

    public RelativeOperatorToken(String terminal) throws LexicalError {
        super("RELOP");
        this.relOp = getRelativeOperator(terminal);
    }

    RelativeOperator getAttribute() {
        return relOp;
    }

    @Override
    public String toString() {
        return "("+super.toString()+","+getAttribute().toString()+")";
    }
}
