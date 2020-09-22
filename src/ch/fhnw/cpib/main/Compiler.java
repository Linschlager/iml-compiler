package ch.fhnw.cpib.main;

import ch.fhnw.cpib.scanner.Scanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Compiler {

    private static String readInputFile(String path) {
        try {
            List<String> linesOfInputCode = Files.readAllLines(Paths.get(path));
            return String.join("", linesOfInputCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String inputCode = readInputFile(args[0]);
        assert inputCode != null;

        Scanner.scan(inputCode);
    }
}
