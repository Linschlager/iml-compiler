package ch.fhnw.cpib.checks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProcedureSignature {
    public List<Integer> temporaryAddressLocations = new ArrayList<>(); // code locations that need to be updated with this.address later
    public int address = -1; // will be set during code gen
    public List<VariableSignature> arguments;
    public List<VariableSignature> globalImports;
    public Map<String, VariableSignature> symbolTable;

    public ProcedureSignature(List<VariableSignature> globalImports, List<VariableSignature> arguments, Map<String, VariableSignature> symbolTable) {
        this.globalImports = globalImports;
        this.arguments = arguments;
        this.symbolTable = symbolTable;
    }
}
