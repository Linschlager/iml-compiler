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
        String input = "foo while 3+89 while+ foobar+ +while\n";
        input = Files.readString(Path.of("intDiv.iml.txt"));
        ITokenList tokens = new Scanner(input).scan();
        ConcSyn.IProgram program = new Parser(tokens).parse();
        AbsSyn.IProgram abstractProgram = program.toAbsSyn();
    }
}
