package ch.fhnw.cpib.checks.types;

import java.util.Objects;

public class IntCodeType implements ICodeType {
    private final int bits;

    public IntCodeType(int bits) {
        this.bits = bits;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public String getName() {
        return String.format("int%d", bits);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof IntCodeType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bits);
    }

    public static String typeString = "int";
}
