package ch.fhnw.cpib.codeGen;

import ch.fhnw.cpib.checks.AccessMode;
import ch.fhnw.cpib.checks.Scope;
import ch.fhnw.cpib.checks.VariableSignature;

import java.util.Map;

// todo: should be created by scope checker (see lecture from 8.12.2020, ~47:00)
public class Environment {

    public static class IdentifierInfo {
        public final int addr; // can be absolute or relative, depending on Scope
        public final boolean isLocalScope; // local must treat addr as relative address (based on frame-pointer)
        public final boolean isDirectAccess;

        public IdentifierInfo(int addr, boolean isLocalScope, boolean isDirectAccess) {
            this.addr = addr;
            this.isLocalScope = isLocalScope;
            this.isDirectAccess = isDirectAccess;
        }
    }

    public final Map<String, VariableSignature> symbolTable;

    public Environment(Map<String, VariableSignature> symbolTable) {
        this.symbolTable = symbolTable;
    }

    public Map<String, VariableSignature> getSymbolTable() {
        return symbolTable;
    }

    public IdentifierInfo getIdentifierInfo(String ident) {
        VariableSignature signature = symbolTable.get(ident);
        return new IdentifierInfo(signature.getAddr(), signature.getScope() == Scope.LOCAL, signature.getAccessMode() != AccessMode.INDIRECT);
    }
}
