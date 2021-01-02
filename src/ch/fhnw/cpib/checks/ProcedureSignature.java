package ch.fhnw.cpib.checks;

import java.util.List;

public class ProcedureSignature {
    public List<VariableSignature> arguments;

    public ProcedureSignature(List<VariableSignature> arguments) {
        this.arguments = arguments;
    }
}
