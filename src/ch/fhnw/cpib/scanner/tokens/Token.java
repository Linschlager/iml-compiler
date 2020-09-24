package ch.fhnw.cpib.scanner.tokens;

public abstract class Token {
    private final String terminal;
    public String getTerminal() {
        return terminal;
    }

    public Token(String terminal) {
        this.terminal = terminal;
    }

    @Override
    public String toString() {
        return getTerminal();
    }
}
