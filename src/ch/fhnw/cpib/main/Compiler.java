package ch.fhnw.cpib.main;

import ch.fhnw.cpib.scanner.LexicalError;
import ch.fhnw.cpib.scanner.Scanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Compiler {

    private static String readInputFile(String path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path))) + " " /* Sentinel Whitespace */;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws LexicalError {
        String inputCode = readInputFile(args[0]);
        assert inputCode != null;

        Scanner.scan(inputCode);
        System.out.println(inputCode);
    }
}
