package ch.fhnw.cpib.checks;

import ch.fhnw.cpib.parser.AbsSyn;

import java.util.LinkedList;
import java.util.List;

public class ScopeChecker {
    public AbsSyn.IProgram checkScopes (AbsSyn.IProgram ast) {
        AbsSyn.Program program = (AbsSyn.Program)ast;
        List<String> variablesInScope = new LinkedList<>();

        return ast;

    }
}
