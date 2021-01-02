package ch.fhnw.cpib.checks;

import ch.fhnw.cpib.checks.types.BoolCodeType;
import ch.fhnw.cpib.checks.types.ICodeType;
import ch.fhnw.cpib.checks.types.IntCodeType;

import java.util.HashMap;

public class Types {
    public static HashMap<String, ICodeType> allTypes;

    static {
        allTypes = new HashMap<>();
        // Currently only using RecordTypes
        allTypes.put("bool", new BoolCodeType());
        allTypes.put("int32", new IntCodeType(32));
        allTypes.put("int64", new IntCodeType(64));
        allTypes.put("int1024", new IntCodeType(1024));
    }
}
