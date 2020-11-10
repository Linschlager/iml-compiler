package ch.fhnw.cpib;

import ch.fhnw.cpib.parser.Parser;
import ch.fhnw.cpib.tokens.Token;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String input = "foo while 3+89 while+ foobar+ +while\n";
        input = Files.readString(Path.of("intDiv.iml.txt"));
        List<Token> tokens = Scanner.scan(input);
        System.out.println(tokens);
        new Parser(tokens).parse();
    }
}
