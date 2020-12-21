package ch.fhnw.cpib;

import ch.fhnw.cpib.exceptions.GrammarError;
import ch.fhnw.cpib.exceptions.LexicalError;
import ch.fhnw.cpib.lexer.ITokenList;
import ch.fhnw.cpib.lexer.Scanner;
import ch.fhnw.cpib.parser.AbsSyn;
import ch.fhnw.cpib.parser.ConcSyn;
import ch.fhnw.cpib.parser.Parser;

import java.util.Objects;

public class Test {

    private final static String assocInput1 = "program a() global x:int32 do debugin x init; debugout (x - x) - x endprogram ";
    private final static String assocInput2 = "program a() global x:int32 do debugin x init; debugout x1 - x2 - x3 endprogram ";

    private static AbsSyn.IProgram compileToAST (String input) {
        try {
            ITokenList tokens = new Scanner(input).scan();
            ConcSyn.IProgram program = new Parser(tokens).parse();
            return program.toAbsSyn();

        } catch (LexicalError | GrammarError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void test() {
        var input1 = compileToAST(assocInput1);
        var input2 = compileToAST(assocInput2);

        assert Objects.deepEquals(input1, input2);
    }
}
