package ch.fhnw.cpib.tokens;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum Terminal {
    // reserved identifiers
    BOOL,
    CALL,
    CONST,
    COPY,
    DEBUGIN,
    DEBUGOUT,
    DIV_E,
    DIV_F,
    DIV_T,
    DO,
    ELSE,
    ENDFUN,
    ENDIF,
    ENDPROC,
    ENDPROGRAM,
    ENDWHILE,
    FALSE,
    FUN,
    GLOBAL,
    IF,
    IN,
    INIT,
    INOUT,
    INT_1024,
    INT_32,
    INT_64,
    LOCAL,
    MOD_E,
    MOD_F,
    MOD_T,
    NOT,
    OUT,
    PROC,
    PROGRAM,
    REF,
    RETURNS,
    SKIP,
    THEN,
    TRUE,
    VAR,
    WHILE,
    // symbols
    PAREN_OPEN,
    PAREN_CLOSE,
    COMMA,
    COLON,
    SEMICOLON,
    OP_ASSIGN,
    OP_COND_AND,
    OP_COND_OR,
    OP_EQ,
    OP_NEQ,
    OP_LT,
    OP_GT,
    OP_LTE,
    OP_GTE,
    OP_ADD,
    OP_SUB,
    OP_MUL,
    // other
    NUMBER_LITERAL,
    IDENTIFIER,
    COMMENT,
    SENTINEL;

    private static final Map<String, Terminal> keywordMappings;

    static {
        keywordMappings = new HashMap<>();
        keywordMappings.put("bool", BOOL);
        keywordMappings.put("call", CALL);
        keywordMappings.put("const", CONST);
        keywordMappings.put("copy", COPY);
        keywordMappings.put("debugin", DEBUGIN);
        keywordMappings.put("debugout", DEBUGOUT);
        keywordMappings.put("divE", DIV_E);
        keywordMappings.put("divF", DIV_F);
        keywordMappings.put("divT", DIV_T);
        keywordMappings.put("do", DO);
        keywordMappings.put("else", ELSE);
        keywordMappings.put("endfun", ENDFUN);
        keywordMappings.put("endif", ENDIF);
        keywordMappings.put("endproc", ENDPROC);
        keywordMappings.put("endprogram", ENDPROGRAM);
        keywordMappings.put("endwhile", ENDWHILE);
        keywordMappings.put("false", FALSE);
        keywordMappings.put("fun", FUN);
        keywordMappings.put("global", GLOBAL);
        keywordMappings.put("if", IF);
        keywordMappings.put("in", IN);
        keywordMappings.put("init", INIT);
        keywordMappings.put("inout", INOUT);
        keywordMappings.put("int1024", INT_1024);
        keywordMappings.put("int32", INT_32);
        keywordMappings.put("int64", INT_64);
        keywordMappings.put("local", LOCAL);
        keywordMappings.put("modE", MOD_E);
        keywordMappings.put("modF", MOD_F);
        keywordMappings.put("modT", MOD_T);
        keywordMappings.put("not", NOT);
        keywordMappings.put("out", OUT);
        keywordMappings.put("proc", PROC);
        keywordMappings.put("program", PROGRAM);
        keywordMappings.put("ref", REF);
        keywordMappings.put("returns", RETURNS);
        keywordMappings.put("skip", SKIP);
        keywordMappings.put("then", THEN);
        keywordMappings.put("true", TRUE);
        keywordMappings.put("var", VAR);
        keywordMappings.put("while", WHILE);
    }

    public static Optional<Terminal> identifierToKeyword(String identifier) {
        return Optional.ofNullable(keywordMappings.get(identifier));
    }
}
