package ch.fhnw.cpib.parser.abssyn;

import ch.fhnw.cpib.lexer.tokens.Type;

public class TraditionalType implements IType {
    private Type type;

    public TraditionalType(Type type) {
        this.type = type;
    }
}
