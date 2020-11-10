package ch.fhnw.cpib.exceptions;

public class LexicalError extends Exception {
    public LexicalError(String message) {
        super(message);
    }

    public LexicalError(char c, int pos) {
        super("Invalid charcter '" + c + "' encountered at position " + pos + " in input!");
    }
}
