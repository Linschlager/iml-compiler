package ch.fhnw.cpib;

import ch.fhnw.cpib.exceptions.ContextError;
import ch.fhnw.cpib.exceptions.GrammarError;
import ch.fhnw.cpib.exceptions.LexicalError;
import ch.fhnw.cpib.exceptions.TypeError;
import ch.fhnw.cpib.lexer.ITokenList;
import ch.fhnw.cpib.lexer.Scanner;
import ch.fhnw.cpib.parser.AbsSyn;
import ch.fhnw.cpib.parser.ConcSyn;
import ch.fhnw.cpib.parser.Parser;

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
            System.out.printf("Error compiling '%s'%n", fileName);
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

        allPrograms.stream().map(Main::compile).collect(Collectors.toList());
    }
}
