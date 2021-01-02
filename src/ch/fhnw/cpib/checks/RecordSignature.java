package ch.fhnw.cpib.checks;

import ch.fhnw.cpib.checks.types.ICodeType;

import java.util.List;

public class RecordSignature {
    public List<RecordField> fields;

    public RecordSignature(List<RecordField> fields) {
        this.fields = fields;
    }

    public static class RecordField {
        public String name;
        public ICodeType type;

        public RecordField(String name, ICodeType type) {
            this.name = name;
            this.type = type;
        }
    }
}
