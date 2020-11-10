package ch.fhnw.cpib.lexer;

import ch.fhnw.cpib.exceptions.LexicalError;
import ch.fhnw.cpib.lexer.tokens.*;

import java.util.ArrayList;
import java.util.List;

public class Scanner implements IScanner {

    private final CharSequence input;

    public Scanner(CharSequence input) {
        this.input = input;
    }

    public ITokenList scan() throws LexicalError {
        assert(input.length() == 0 || input.charAt(input.length()-1) == '\n');
        ITokenList tokenList = new TokenList();

        int state = 0;
        StringBuilder lexAccu = null; // for identifiers and comments
        long numAccu = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            switch (state) {
                case 0:
                    if (c == '(') {
                        tokenList.add(new Token(Terminal.LPAREN));
                    } else if (c == ',') {
                        tokenList.add(new Token(Terminal.COMMA));
                    } else if (c == ')') {
                        tokenList.add(new Token(Terminal.RPAREN));
                    } else if (c == ';') {
                        tokenList.add(new Token(Terminal.SEMICOLON));
                    } else if (c == '=') {
                        tokenList.add(new RelOpr(RelOpr.Attr.EQ));
                    } else if (c == '+') {
                        tokenList.add(new AddOpr(AddOpr.Attr.PLUS));
                    } else if (c == '-') {
                        tokenList.add(new AddOpr(AddOpr.Attr.MINUS));
                    } else if (c == '*') {
                        tokenList.add(new MultOpr(MultOpr.Attr.TIMES));
                    } else if (c == '.') {
                        tokenList.add(new Token(Terminal.ACCESSOPR));
                    } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                        lexAccu = new StringBuilder();
                        lexAccu.append(c);
                        state = 1;
                    } else if (Character.isDigit(c)) {
                        numAccu = Character.digit(c, 10);
                        state = 2;
                    } else if (c == '/') {
                        state = 3;
                    } else if (c == ':') {
                        state = 5;
                    } else if (c == '\\') {
                        state = 7;
                    } else if (c == '<') {
                        state = 9;
                    } else if (c == '>') {
                        state = 10;
                    } else if (Character.isWhitespace(c)) {
                        // skip whitespace
                    } else {
                        throw new LexicalError(c, i);
                    }
                    break;
                case 1:
                    if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || Character.isDigit(c) || c == '_') {
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
                        tokenList.add(new Literal((int) numAccu));
                        numAccu = 0;
                    }
                    break;
                case 3:
                    if (c == '/') {
                        state = 4;
                    } else if (c == '\\') {
                        state = 6;
                    } else if (c == '=') {
                        tokenList.add(new RelOpr(RelOpr.Attr.NE));
                        state = 0;
                    } else {
                        throw new LexicalError(c, i);
                    }
                    break;
                case 4:
                    if (c == '\n') {
                        if (lexAccu != null) {
                            // skip comments
                            // tokenList.add(new Comment(lexAccu.toString()));
                            lexAccu = null;
                        }
                        state = 0;
                        i--; // let state 0 handle newline
                    } else {
                        if (lexAccu == null) lexAccu = new StringBuilder();
                        lexAccu.append(c);
                    }
                    break;
                case 5:
                    if (c == '=') {
                        tokenList.add(new Token(Terminal.BECOMES));
                        state = 0;
                    } else {
                        tokenList.add(new Token(Terminal.COLON));
                        state = 0;
                        i--;
                    }
                    break;
                case 6:
                    if (c == '?') {
                        tokenList.add(new BoolOpr(BoolOpr.Attr.CAND));
                        state = 0;
                    } else {
                        throw new LexicalError(c, i);
                    }
                    break;
                case 7:
                    if (c == '/') {
                        state = 8;
                    } else {
                        throw new LexicalError(c, i);
                    }
                    break;
                case 8:
                    if (c == '?') {
                        tokenList.add(new BoolOpr(BoolOpr.Attr.COR));
                        state = 0;
                    } else {
                        throw new LexicalError(c, i);
                    }
                    break;
                case 9:
                    if (c == '=') {
                        tokenList.add(new RelOpr(RelOpr.Attr.LE));
                        state = 0;
                    } else {
                        tokenList.add(new RelOpr(RelOpr.Attr.LT));
                        state = 0;
                        i--;
                    }
                    break;
                case 10:
                    if (c == '=') {
                        tokenList.add(new RelOpr(RelOpr.Attr.GE));
                        state = 0;
                    } else {
                        tokenList.add(new RelOpr(RelOpr.Attr.GT));
                        state = 0;
                        i--;
                    }
                    break;
                default: throw new RuntimeException("Invalid state: " + state);
            }
        }

        assert(state == 0);

        tokenList.add(new Token(Terminal.SENTINEL));

        return tokenList;
    }

    private static Token tokenFromIdentifier(String identifier) {
        return Terminal.identifierToKeyword(identifier).orElse(new Identifier(identifier));
    }
}
