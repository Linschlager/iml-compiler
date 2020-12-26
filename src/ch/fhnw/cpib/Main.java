package ch.fhnw.cpib;

import ch.fhnw.cpib.lexer.ITokenList;
import ch.fhnw.cpib.lexer.Scanner;
import ch.fhnw.cpib.parser.AbsSyn;
import ch.fhnw.cpib.parser.ConcSyn;
import ch.fhnw.cpib.parser.Parser;
import ch.fhnw.lederer.virtualmachineFS2015.CodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        Test.test(); // Do extensive testing!

        String input = "foo while 3+89 while+ foobar+ +while\n";
        input = Files.readString(Path.of("intDiv.iml.txt"));
        input = Files.readString(Path.of("iml-programs/Add17.iml"));
        ITokenList tokens = new Scanner(input).scan();
        ConcSyn.IProgram program = new Parser(tokens).parse();
        AbsSyn.IProgram abstractProgram = program.toAbsSyn();

        ICodeArray codeArray = new CodeArray(100_000); // just make it large enough
        abstractProgram.code(codeArray, 0);

    }
}
