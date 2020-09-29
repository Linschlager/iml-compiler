package ch.fhnw.cpib.scanner.tokens;

import ch.fhnw.cpib.scanner.LexicalError;
import ch.fhnw.cpib.scanner.enums.Symbol;

import java.util.HashMap;

public class SymbolToken extends Token {

    private final Symbol symbol;

    private static final HashMap<String, Symbol> symbolHashMap;

    static {
        symbolHashMap = new HashMap<>();

        symbolHashMap.put("(", Symbol.BRACK_OPEN);
        symbolHashMap.put(")", Symbol.BRACK_CLOSE);
        symbolHashMap.put("{", Symbol.CURLYBRACK_OPEN);
        symbolHashMap.put("}", Symbol.CURLYBRACK_CLOSE);
        symbolHashMap.put("[", Symbol.ARRBRACK_OPEN);
        symbolHashMap.put("]", Symbol.ARRBRACK_CLOSE);
        symbolHashMap.put(":", Symbol.COLON);
        symbolHashMap.put(";", Symbol.SEMICOLON);
        symbolHashMap.put(",", Symbol.COMMA);
    }

    public SymbolToken(String terminal) throws LexicalError {
        super(terminal);

        if (symbolHashMap.containsKey(terminal)) {
            symbol = symbolHashMap.get(terminal);
        } else {
            throw new LexicalError(String.format("Cannot parse symbol %s", terminal));
        }
    }

    @Override
    public String toString() {
        return symbol.toString();
    }
}
