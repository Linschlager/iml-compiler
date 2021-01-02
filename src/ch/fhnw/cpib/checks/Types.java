package ch.fhnw.cpib.checks;

import java.util.HashMap;

public class Types {
    public static HashMap<String, Types> allTypes;
    public static Types BOOLEAN;
    public static Types INTEGER;

    static {
        allTypes = new HashMap<>();
        BOOLEAN = new Types("BOOLEAN");
        INTEGER = new Types("INTEGER");
        allTypes.put("bool", BOOLEAN);
        allTypes.put("int32", INTEGER);
        allTypes.put("int64", INTEGER);
        allTypes.put("int1024", INTEGER);
    }

    private final String type; // BOOLEAN, INTEGER or Record
    private final String recordName;
    public Types(String type) {
        this(type, null);
    }
    public Types(String type, String recordName) {
        this.type = type;
        this.recordName = recordName;
    }


    public String toString() {
        if (recordName != null) {
            return type + " (" + recordName + ")";
        }
        return type;
    }

    public String getRecordName() {
        return recordName;
    }

    public String getType() {
        return type;
    }
}
