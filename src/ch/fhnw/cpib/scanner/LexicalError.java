package ch.fhnw.cpib.scanner;

public class LexicalError extends Exception {

    public LexicalError(CharSequence input) {
        super(input.toString());
    }
}
