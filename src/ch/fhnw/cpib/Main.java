package ch.fhnw.cpib;

import ch.fhnw.cpib.lexer.ITokenList;
import ch.fhnw.cpib.lexer.Scanner;
import ch.fhnw.cpib.parser.AbsSyn;
import ch.fhnw.cpib.parser.ConcSyn;
import ch.fhnw.cpib.parser.Parser;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        Test.test(); // Do extensive testing!

        String programName = "WhileCmd";
        String input = Files.readString(Path.of("programs",programName+".iml"));
        ITokenList tokens = new Scanner(input).scan();
        ConcSyn.IProgram program = new Parser(tokens).parse();
        AbsSyn.IProgram abstractProgram = program.toAbsSyn();
        AbsSyn.IProgram validatedAbstractProgram = abstractProgram.check();

    }
}
