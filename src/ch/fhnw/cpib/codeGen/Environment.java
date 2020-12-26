package ch.fhnw.cpib.codeGen;

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

    public IdentifierInfo getIdentifierInfo(String ident) {
        throw new RuntimeException("Not yet implemented");
    }
}
