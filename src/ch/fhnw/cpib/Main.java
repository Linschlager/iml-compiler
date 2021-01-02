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

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
        Test.test(); // Do extensive testing!
        //Map<String, AbsSyn.IProgram> programMap = compileAllPrograms(); doesnt work, because it uses static variables

        // Find the first program of that name in the list of parsed programs
        //var file = Path.of("programs/Add17.iml");
        //var file = Path.of("programs/IfCmd.iml");
        //var file = Path.of("programs/WhileCmd.iml");
        var file = Path.of("programs-demo/BasicRecords.iml");

        var programToCompile = compile(file.getFileName().toString(), Files.readString(file));

        ICodeArray codeArray = new CodeArray(100_000); // just make it large enough
        programToCompile.code(codeArray, 0, new Environment(programToCompile.getSymbolTable()) {
            @Override
            public IdentifierInfo getIdentifierInfo(String ident) {
                return switch (ident) {
                    case "x", "vec" -> new IdentifierInfo(0, false, true);
                    case "y" -> new IdentifierInfo(1, false, true);
                    default -> throw new IllegalStateException("Unexpected value: " + ident);
                };
            }
        });
        codeArray.resize();

        new VirtualMachine(codeArray, 100_000);
    }

    private static AbsSyn.IProgram compile(String fileName, String programCode) {
        try {
            ITokenList tokens = new Scanner(programCode).scan();
            ConcSyn.IProgram program = new Parser(tokens).parse();
            AbsSyn.IProgram abstractProgram = program.toAbsSyn();
            AbsSyn.IProgram validatedAbstractProgram = abstractProgram.check();

            return validatedAbstractProgram;
        } catch (GrammarError | LexicalError | ContextError | TypeError e) {
            System.out.printf("Error compiling '%s'%n", fileName);
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String, AbsSyn.IProgram> compileAllPrograms() throws IOException {
        Map<String, AbsSyn.IProgram> allPrograms = new HashMap<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of("programs"))) {
            for (Path file : stream) {
                var output = Files.readString(file);
                String fileName = file.getFileName().toString();
                allPrograms.put(fileName, compile(fileName, output));
            }
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of("programs-demo"))) {
            for (Path file : stream) {
                var output = Files.readString(file);
                String fileName = file.getFileName().toString();
                allPrograms.put(fileName, compile(fileName, output));
            }
        }
        return allPrograms;
    }
}
