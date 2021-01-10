package ch.fhnw.cpib.checks;

import ch.fhnw.cpib.checks.types.ICodeType;

import java.util.List;
import java.util.Map;

public class FunctionSignature extends ProcedureSignature {
    public ICodeType returnType;

    public FunctionSignature(List<VariableSignature> args, Map<String, VariableSignature> symbolTable, ICodeType returnType) {
        super(args, symbolTable);
        this.returnType = returnType;
    }
}
