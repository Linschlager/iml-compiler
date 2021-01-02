package ch.fhnw.cpib.checks.types;

import ch.fhnw.cpib.checks.RecordSignature;

import java.util.List;

public class RecordCodeType implements ICodeType {
    private final List<RecordSignature.RecordField> fields;
    private final String name;

    public RecordCodeType(String name, List<RecordSignature.RecordField> fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public int getSize() {
        return fields.stream().map(f -> f.type).mapToInt(ICodeType::getSize).sum();
    }

    public int calculateFieldOffset(List<String> fieldNames) {
        if (fieldNames.isEmpty()) throw new RuntimeException("invalid state while looking up field offset");
        int offset = 0;

        for (RecordSignature.RecordField field : fields) {
            if (field.name.equals(fieldNames.get(0))) {
                if (fieldNames.size() > 1) {
                    offset += ((RecordCodeType)(field.type)).calculateFieldOffset(fieldNames.subList(1, fieldNames.size()));
                }
                return offset;
            }
            if (!(field.type instanceof RecordCodeType)) {
                // normal field
                offset++;
            } else {
                offset += field.type.getSize();
            }
        }
        return offset;
    }

    @Override
    public String getName() {
        return name;
    }


}
