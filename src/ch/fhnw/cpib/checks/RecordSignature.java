package ch.fhnw.cpib.checks;

import java.util.List;
import java.util.Map;

public class RecordSignature {
    public List<RecordField> fields;

    public RecordSignature(List<RecordField> fields) {
        this.fields = fields;
    }

    public static class RecordField {
        public String name;
        public Types type;

        public RecordField(String name, Types type) {
            this.name = name;
            this.type = type;
        }
    }

    public int getRecordSize(Map<String, RecordSignature> recordMap) {
        int size = 0;

        for (RecordField field : fields) {
            if (field.type.getRecordName() == null) {
                // normal field
                size++;
            } else {
                size += recordMap.get(field.type.getRecordName()).getRecordSize(recordMap);
            }
        }

        return size;
    }

    public int calculateFieldOffset(List<String> fieldNames, Map<String, RecordSignature> recordMap) {
        if (fieldNames.isEmpty()) throw new RuntimeException("invalid state while looking up field offset");
        int offset = 0;

        for (RecordField field : fields) {
            if (field.name.equals(fieldNames.get(0))) {
                if (fieldNames.size() > 1) {
                    offset += recordMap.get(field.type.getRecordName()).calculateFieldOffset(fieldNames.subList(1, fieldNames.size()), recordMap);
                }
                return offset;
            }
            if (field.type.getRecordName() == null) {
                // normal field
                offset++;
            } else {
                offset += recordMap.get(field.type.getRecordName()).getRecordSize(recordMap);
            }
        }
        return offset;
    }
}
