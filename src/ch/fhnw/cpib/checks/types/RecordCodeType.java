package ch.fhnw.cpib.checks.types;

import java.util.List;

public class RecordCodeType implements ICodeType {
    private final List<ICodeType> fields;
    private final String name;

    public RecordCodeType(String name, List<ICodeType> fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public int getSize() {
        return fields.stream().mapToInt(ICodeType::getSize).sum();
    }

    @Override
    public String getName() {
        return name;
    }


}
