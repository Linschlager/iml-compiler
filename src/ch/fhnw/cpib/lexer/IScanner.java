package ch.fhnw.cpib.lexer;

import ch.fhnw.cpib.exceptions.LexicalError;

public interface IScanner {
    ITokenList scan() throws LexicalError;
}
