package ch.fhnw.cpib.checks;

import ch.fhnw.cpib.lexer.tokens.Changemode;
import ch.fhnw.cpib.lexer.tokens.Flowmode;
import ch.fhnw.cpib.lexer.tokens.Mechmode;

import java.util.Objects;

public class VariableSignature {
    private int addr; // Written during codegen

    private final Types type;
    private final Flowmode.Attr flowMode;
    private final Changemode.Attr changeMode;
    private final Mechmode.Attr mechMode;

    private AccessMode accessMode;
    private Scope scope;

    public VariableSignature(Types type, Flowmode.Attr flowMode, Changemode.Attr changeMode, Mechmode.Attr mechMode) {
        this.type = type;
        this.flowMode = Objects.requireNonNullElse(flowMode, Flowmode.Attr.IN);
        this.changeMode = Objects.requireNonNullElse(changeMode, Changemode.Attr.CONST);
        this.mechMode = Objects.requireNonNullElse(mechMode, Mechmode.Attr.COPY);

        // TODO find accessMode and Scope
        this.accessMode = null;
        this.scope = null;
    }

    public VariableSignature(Types type) {
        this(type, null, null, null);
    }

    public VariableSignature(Types type, Flowmode.Attr flowMode, Changemode.Attr changeMode, Mechmode.Attr mechMode, AccessMode accessMode, Scope scope) {
        this(type, flowMode, changeMode, mechMode);

        // TODO remove this constructor if it can be calculated based on the first 4 params
        this.accessMode = accessMode;
        this.scope = scope;
    }

    public int getAddr() {
        return addr;
    }

    public void setAddr(int addr) {
        this.addr = addr;
    }

    public Types getType() {
        return type;
    }

    public Flowmode.Attr getFlowMode() {
        return flowMode;
    }

    public Changemode.Attr getChangeMode() {
        return changeMode;
    }

    public Mechmode.Attr getMechMode() {
        return mechMode;
    }

    public AccessMode getAccessMode() {
        return accessMode;
    }

    public Scope getScope() {
        return scope;
    }
}
