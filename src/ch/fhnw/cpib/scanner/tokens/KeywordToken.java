package ch.fhnw.cpib.scanner.tokens;

public class KeywordToken extends Token {
    public KeywordToken(String word) {
        super(word.toUpperCase());
    }
}
