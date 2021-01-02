package ch.fhnw.cpib.checks.types;

public class BoolCodeType implements ICodeType {
    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public String getName() {
        return "bool";
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BoolCodeType;
    }

    public static String typeString = "bool";
}
