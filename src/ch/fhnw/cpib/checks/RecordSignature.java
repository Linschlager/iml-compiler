package ch.fhnw.cpib.checks;

import java.util.List;

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
}
