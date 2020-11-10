package ch.fhnw.cpib.lexer;

import ch.fhnw.cpib.lexer.tokens.Token;

import java.util.Iterator;
import java.util.LinkedList;

public class TokenList implements ITokenList {
    LinkedList<Token> internalList;

    @Override
    public Iterator<Token> iterator() {
        return internalList.iterator();
    }

    @Override
    public void add(Token t) {
        internalList.add(t);
    }

    public TokenList() {
        this.internalList = new LinkedList<>();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }
}
