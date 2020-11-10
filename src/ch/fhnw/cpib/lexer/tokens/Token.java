package ch.fhnw.cpib.lexer.tokens;

public class Token {

    public final Terminal terminal;

    public Token(Terminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public String toString() {
        return terminal.toString();
    }
}
