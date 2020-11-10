package ch.fhnw.cpib.lexer.tokens;

public class Identifier extends Token {

    public final String ident;

    public Identifier(String ident) {
        super(Terminal.IDENT);
        this.ident = ident;
    }

    @Override
    public String toString() {
        return "Identifier(" + ident + ')';
    }
}
