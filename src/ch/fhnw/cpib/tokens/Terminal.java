package ch.fhnw.cpib.tokens;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum Terminal {
    OP_ADD,
    WHILE,
    NUMBER_LITERAL,
    IDENTIFIER,
    SENTINEL;

    private static final Map<String, Terminal> keywordMappings;

    static {
        keywordMappings = new HashMap<>();
        keywordMappings.put("while", WHILE);
    }

    public static Optional<Terminal> identifierToKeyword(String identifier) {
        return Optional.ofNullable(keywordMappings.get(identifier));
    }
}
