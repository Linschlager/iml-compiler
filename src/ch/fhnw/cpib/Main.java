package ch.fhnw.cpib;

import ch.fhnw.cpib.tokens.Token;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        List<Token> tokens = Scanner.scan("foo while 3+89 while+ foobar+ +while");
        System.out.println(tokens);
    }
}
