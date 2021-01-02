package ch.fhnw.cpib.checks;

import ch.fhnw.cpib.checks.types.ICodeType;

import java.util.List;

public class FunctionSignature extends ProcedureSignature {
    public ICodeType returnType;

    public FunctionSignature(List<VariableSignature> args, ICodeType returnType) {
        super(args);
        this.returnType = returnType;
    }
}
