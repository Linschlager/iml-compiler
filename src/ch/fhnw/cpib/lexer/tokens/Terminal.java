package ch.fhnw.cpib.lexer.tokens;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum Terminal {
    ADDOPR,
    BECOMES,
    BOOLOPR,
    CALL,
    CHANGEMODE,
    COLON,
    COMMA,
    DEBUGIN,
    DEBUGOUT,
    DO,
    ELSE,
    ENDFUN,
    ENDIF,
    ENDPROC,
    ENDPROGRAM,
    ENDWHILE,
    FUN,
    FLOWMODE,
    GLOBAL,
    IDENT,
    IF,
    INIT,
    LITERAL,
    LOCAL,
    LPAREN,
    MECHMODE,
    MULTOPR,
    NOTOPR,
    PROC,
    PROGRAM,
    RELOPR,
    RETURNS,
    RPAREN,
    SKIP,
    SEMICOLON,
    SENTINEL,
    THEN,
    TYPE,
    WHILE,
    RECORD,
    ACCESSOPR;

    private static final Map<String, Token> keywordMappings;

    static {
        keywordMappings = new HashMap<>();
        keywordMappings.put("bool", new Type(Type.Attr.BOOL));
        keywordMappings.put("call", new Token(CALL));
        keywordMappings.put("const", new Changemode(Changemode.Attr.CONST));
        keywordMappings.put("copy", new Mechmode(Mechmode.Attr.COPY));
        keywordMappings.put("debugin", new Token(DEBUGIN));
        keywordMappings.put("debugout", new Token(DEBUGOUT));
        keywordMappings.put("divE", new MultOpr(MultOpr.Attr.DIV_E));
        keywordMappings.put("divF", new MultOpr(MultOpr.Attr.DIV_F));
        keywordMappings.put("divT", new MultOpr(MultOpr.Attr.DIV_T));
        keywordMappings.put("do", new Token(DO));
        keywordMappings.put("else", new Token(ELSE));
        keywordMappings.put("endfun", new Token(ENDFUN));
        keywordMappings.put("endif", new Token(ENDIF));
        keywordMappings.put("endproc", new Token(ENDPROC));
        keywordMappings.put("endprogram", new Token(ENDPROGRAM));
        keywordMappings.put("endwhile", new Token(ENDWHILE));
        keywordMappings.put("false", new Literal(false));
        keywordMappings.put("fun", new Token(FUN));
        keywordMappings.put("global", new Token(GLOBAL));
        keywordMappings.put("if", new Token(IF));
        keywordMappings.put("in", new Flowmode(Flowmode.Attr.IN));
        keywordMappings.put("init", new Token(INIT));
        keywordMappings.put("inout", new Flowmode(Flowmode.Attr.INOUT));
        keywordMappings.put("int1024", new Type(Type.Attr.INT1024));
        keywordMappings.put("int32", new Type(Type.Attr.INT32));
        keywordMappings.put("int64", new Type(Type.Attr.INT64));
        keywordMappings.put("local", new Token(LOCAL));
        keywordMappings.put("modE", new MultOpr(MultOpr.Attr.MOD_E));
        keywordMappings.put("modF", new MultOpr(MultOpr.Attr.MOD_F));
        keywordMappings.put("modT", new MultOpr(MultOpr.Attr.MOD_T));
        keywordMappings.put("not", new Token(NOTOPR));
        keywordMappings.put("out", new Flowmode(Flowmode.Attr.OUT));
        keywordMappings.put("proc", new Token(PROC));
        keywordMappings.put("program", new Token(PROGRAM));
        keywordMappings.put("record", new Token(RECORD));
        keywordMappings.put("ref", new Mechmode(Mechmode.Attr.REF));
        keywordMappings.put("returns", new Token(RETURNS));
        keywordMappings.put("skip", new Token(SKIP));
        keywordMappings.put("then", new Token(THEN));
        keywordMappings.put("true", new Literal(true));
        keywordMappings.put("var", new Changemode(Changemode.Attr.VAR));
        keywordMappings.put("while", new Token(WHILE));
    }

    public static Optional<Token> identifierToKeyword(String identifier) {
        return Optional.ofNullable(keywordMappings.get(identifier));
    }
}
