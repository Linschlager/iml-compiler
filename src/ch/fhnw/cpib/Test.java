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

    private final static String assocInput = "program Assoc()\n" +
            "global x:int32\n" +
            "do\n" +
            "  debugin x init ;\n" +
            "  debugout x - (x - x) ;\n" +
            "  debugout (x - x) - x ; \n" +
            "  debugout x - x - x ; // should be same as line above\n" +
            "\n" +
            "  debugout x;\n" +
            "  debugout ((((x))));\n" +
            "\n" +
            "\n" +
            "  debugout x divE (2 divE 2) ;\n" +
            "  debugout (x divE 2) divE 2 ;\n" +
            "  debugout x divE 2 divE 2 // should be same as above\n" +
            "endprogram\n";

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

    public static void test() throws Exception {
        AbsSyn.Program input = (AbsSyn.Program) compileToAST(assocInput);
        assert input != null;
        { // Check x - x - x => (x - x) - x
            AbsSyn.ICommand command = input.commands.get(3);
            var doc = (AbsSyn.DebugOutCommand) command;
            var ade = (AbsSyn.AdditionDyadicExpression) doc.expression;
            if (!(ade.l instanceof AbsSyn.AdditionDyadicExpression)) {
                throw new Exception("Assertion not fixed correctly");
            }
        }
        { // Check x divE 2 divE 2 => (x divE 2) divE 2
            AbsSyn.ICommand command = input.commands.get(8);
            var doc = (AbsSyn.DebugOutCommand) command;
            var ade = (AbsSyn.MultiplicationDyadicExpression) doc.expression;
            if (!(ade.l instanceof AbsSyn.MultiplicationDyadicExpression)) {
                throw new Exception("Assertion not fixed correctly");
            }
        }
    }
}
