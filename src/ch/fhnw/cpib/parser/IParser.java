package ch.fhnw.cpib.parser;

import ch.fhnw.cpib.exceptions.GrammarError;

public interface IParser {

    ConcSyn.IProgram parse() throws GrammarError;
}
