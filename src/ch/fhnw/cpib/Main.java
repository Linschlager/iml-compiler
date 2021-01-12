package ch.fhnw.cpib;

import ch.fhnw.cpib.codeGen.Environment;
import ch.fhnw.cpib.exceptions.ContextError;
import ch.fhnw.cpib.exceptions.GrammarError;
import ch.fhnw.cpib.exceptions.LexicalError;
import ch.fhnw.cpib.exceptions.TypeError;
import ch.fhnw.cpib.lexer.ITokenList;
import ch.fhnw.cpib.lexer.Scanner;
import ch.fhnw.cpib.parser.AbsSyn;
import ch.fhnw.cpib.parser.ConcSyn;
import ch.fhnw.cpib.parser.Parser;
import ch.fhnw.lederer.virtualmachineFS2015.CodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.ICodeArray;
import ch.fhnw.lederer.virtualmachineFS2015.VirtualMachine;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    private static ICodeArray compile(String programCode) throws LexicalError, GrammarError, ContextError, TypeError, ICodeArray.CodeTooSmallError {
        ICodeArray codeArray = new CodeArray(100_000); // just make it large enough
        ITokenList tokens = new Scanner(programCode).scan();
        ConcSyn.IProgram program = new Parser(tokens).parse();
        AbsSyn.IProgram abstractProgram = program.toAbsSyn();
        AbsSyn.IProgram validated = abstractProgram.check();
        validated.code(codeArray, 0, new Environment(validated.getSymbolTable()));
        codeArray.resize();
        return codeArray;
    }

    public static void main(String[] args) throws Exception {
        // Test.test(); // Do extensive testing!

        // Find the first program of that name in the list of parsed programs
        var file = Path.of("programs-demo/playground.iml");
        // var file = Path.of("programs-demo/Shapes.iml");
        // var file = Path.of("programs-demo/BasicRecords.iml");
        // var file = Path.of("programs-demo/CompareVectors.iml");
        // var file = Path.of("programs-demo/EEA.iml");
        // var file = Path.of("programs-demo/factorialRec.iml");
        // var file = Path.of("programs/Add17.iml");
        // var file = Path.of("programs/Add17Fun.iml");
        // var file = Path.of("programs/Add17Proc4.iml");
        // var file = Path.of("programs/Factorial.iml");
        // var file = Path.of("programs/IfCmd.iml");
        // var file = Path.of("programs/WhileCmd.iml");

        try {
            var codeArray = compile(Files.readString(file));
            System.out.println(codeArray.toString());
            new VirtualMachine(codeArray, 100_000);

        } catch (GrammarError | LexicalError | ContextError | TypeError | ICodeArray.CodeTooSmallError e) {
            System.out.printf("Error compiling '%s'%n", file.getFileName().toString());
            e.printStackTrace();
        }
    }
}
