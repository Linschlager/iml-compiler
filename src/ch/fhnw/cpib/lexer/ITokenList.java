package ch.fhnw.cpib.lexer;

import ch.fhnw.cpib.lexer.tokens.Token;

import java.util.Iterator;

public interface ITokenList {
    Iterator<Token> iterator();

    void add(Token t);
}
