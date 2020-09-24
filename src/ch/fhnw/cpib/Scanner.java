package ch.fhnw.cpib;

import ch.fhnw.cpib.tokens.Identifier;
import ch.fhnw.cpib.tokens.NumberLiteral;
import ch.fhnw.cpib.tokens.Terminal;
import ch.fhnw.cpib.tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class Scanner {

    public static List<Token> scan(CharSequence input) throws LexicalError {
        List<Token> tokenList = new ArrayList<>();


        int state = 0;
        StringBuilder lexAccu = null;
        long numAccu = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            switch (state) {
                case 0:
                    if (c == '+') {
                        tokenList.add(new Token(Terminal.OP_ADD));
                    } else if (c >= 'a' && c <= 'z') {
                        lexAccu = new StringBuilder();
                        lexAccu.append(c);
                        state = 1;
                    } else if (Character.isDigit(c)) {
                        numAccu = Character.digit(c, 10);
                        state = 2;
                    } else if (Character.isWhitespace(c)) {
                        // skip whitespace
                    } else {
                        throw new LexicalError(c, i);
                    }
                    break;
                case 1:
                    if ((c >= 'a' && c <= 'z') || Character.isDigit(c)) {
                        lexAccu.append(c);
                    } else {
                        state = 0;
                        i--;
                        tokenList.add(tokenFromIdentifier(lexAccu.toString()));
                        lexAccu = null;
                    }
                    break;
                case 2:
                    if (Character.isDigit(c)) {
                        numAccu = numAccu * 10 + Character.digit(c, 10);
                        if (numAccu > Integer.MAX_VALUE) throw new LexicalError("IntegerLiteral too large at position " + i + "!");
                    } else {
                        state = 0;
                        i--;
                        tokenList.add(new NumberLiteral((int) numAccu));
                        numAccu = 0;
                    }
                    break;
                default: throw new RuntimeException("Invalid state: " + state);
            }
        }


        tokenList.add(new Token(Terminal.SENTINEL));

        return tokenList;
    }

    private static Token tokenFromIdentifier(String identifier) {
        return Terminal.identifierToKeyword(identifier).map(Token::new).orElse(new Identifier(identifier));
    }
}
