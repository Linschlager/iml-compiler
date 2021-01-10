package ch.fhnw.cpib.checks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProcedureSignature {
    public List<Integer> temporaryAddressLocations = new ArrayList<>(); // code locations that need to be updated with this.address later
    public int address = -1; // will be set during code gen
    public List<VariableSignature> arguments;
    public Map<String, VariableSignature> symbolTable;

    public ProcedureSignature(List<VariableSignature> arguments, Map<String, VariableSignature> symbolTable) {
        this.arguments = arguments;
        this.symbolTable = symbolTable;
    }
}
