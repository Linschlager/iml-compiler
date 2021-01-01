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
import ch.fhnw.lederer.virtualmachineFS2015.IVirtualMachine;
import ch.fhnw.lederer.virtualmachineFS2015.VirtualMachine;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static AbsSyn.IProgram compile(String[] input) {
        var fileName = input[0];
        var programCode = input[1];
        try {
            ITokenList tokens = new Scanner(programCode).scan();
            ConcSyn.IProgram program = new Parser(tokens).parse();
            AbsSyn.IProgram abstractProgram = program.toAbsSyn();
            AbsSyn.IProgram validatedAbstractProgram = abstractProgram.check();

            return validatedAbstractProgram;
        } catch (GrammarError | LexicalError | ContextError | TypeError e) {
            System.err.printf("Error compiling '%s'%n", fileName);
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Test.test(); // Do extensive testing!
        List<String[]> allPrograms = new LinkedList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of("programs"))) {
            for (Path file: stream) {
                var output = Files.readString(file);
                allPrograms.add(new String[] {file.getFileName().toString(), output});
            }
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of("programs-demo"))) {
            for (Path file: stream) {
                var output = Files.readString(file);
                allPrograms.add(new String[] {file.getFileName().toString(), output});
            }
        }

        List<AbsSyn.IProgram> asts = new LinkedList<>();
        for (String[] program : allPrograms) {
            asts.add(compile(program));
        }

        // Find the first program of that name in the list of parsed programs
        var programToCompile = asts.stream().filter(program -> ((AbsSyn.Program) program).name.equals("extendedEuclidianAlgorithm")).findFirst();

        if (programToCompile.isEmpty()) throw new Exception("Couldn't find program");
        else {
            var ast = programToCompile.get();
            ICodeArray codeArray = new CodeArray(100_000); // just make it large enough
            ast.code(codeArray, 0, new Environment() {
                @Override
                public IdentifierInfo getIdentifierInfo(String ident) {
                    return switch (ident) {
                        case "x" -> new IdentifierInfo(0, false, true);
                        case "y" -> new IdentifierInfo(1, false, true);
                        default -> throw new IllegalStateException("Unexpected value: " + ident);
                    };
                }
            });
            codeArray.resize();
        }

        //new VirtualMachine(codeArray, 100_000);
    }
}
