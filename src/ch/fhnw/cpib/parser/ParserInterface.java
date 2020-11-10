package ch.fhnw.cpib.parser;

import ch.fhnw.cpib.exceptions.GrammarError;

public interface ParserInterface {

    void parse() throws GrammarError;
}
