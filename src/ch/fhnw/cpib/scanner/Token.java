package ch.fhnw.cpib.scanner;

public class Token {
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        return type;
    }



    public static Token getToken(String inputString) {
        return new Token(null, inputString);
    }
}
