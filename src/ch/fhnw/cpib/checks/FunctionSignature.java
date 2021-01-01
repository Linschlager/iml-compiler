package ch.fhnw.cpib.checks;

import java.util.List;

public class FunctionSignature extends ProcedureSignature {
    public Types returnType;

    public FunctionSignature(List<VariableSignature> args, Types returnType) {
        super(args);
        this.returnType = returnType;
    }
}
