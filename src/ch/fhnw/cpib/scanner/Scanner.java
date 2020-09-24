package ch.fhnw.cpib.scanner;

import ch.fhnw.cpib.scanner.tokens.*;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {

    static final Pattern keyword = Pattern.compile("^(bool|call|const|copy|debugin|debugout|divE|divF|divT|do|else|endfun|endif|endproc|endprogram|endwhile|false|fun|global|if|init|in|inout|int1024|int32|int64|local|modE|modF|modT|not|out|proc|program|ref|returns|skip|then|true|var|while)\\s");
    static final Pattern relopr = Pattern.compile("^(<=|>=|<|>|=)");
    static final Pattern literal = Pattern.compile("^([0-9]+)");
    static final Pattern ident = Pattern.compile("^([a-zA-Z][a-zA-Z0-9]*)");
    static final Pattern whitespace = Pattern.compile("^[ \t\n\r]+");

    private static Matcher doesMatch(CharSequence input, Pattern pattern) {
        Matcher matches = pattern.matcher(input);
        if (matches.find()) return matches;
        return null;
    }

    public static void scan(CharSequence inputString) throws LexicalError {
        List<Token> tokenList = new ArrayList<>();
        while (inputString.length() > 0) {
            boolean didMatch = false;
            Matcher m;
            if ((m = doesMatch(inputString, keyword)) != null) {
                var r = m.group(1); // Get second capture group (innermost brackets) first would be the full match. But we don't want to include adjacent necessary characters. (e.g. trailing whitespace)
                tokenList.add(new KeywordToken(r));

                didMatch = true;
            } else if ((m = doesMatch(inputString, relopr)) != null) {
                var r = m.group(1); // Get second capture group (innermost brackets) first would be the full match. But we don't want to include adjacent necessary characters. (e.g. trailing whitespace)
                tokenList.add(new RelativeOperatorToken(r));

                didMatch = true;
            } else if ((m = doesMatch(inputString, ident)) != null) {
                var r = m.group(1); // Get second capture group (innermost brackets) first would be the full match. But we don't want to include adjacent necessary characters. (e.g. trailing whitespace)
                tokenList.add(new IdentifierToken(r));

                didMatch = true;
            } else if ((m = doesMatch(inputString, literal)) != null) {
                var r = m.group(1); // Get second capture group (innermost brackets) first would be the full match. But we don't want to include adjacent necessary characters. (e.g. trailing whitespace)
                tokenList.add(new LiteralToken(Integer.parseInt(r)));

                didMatch = true;
            } else if ((m = doesMatch(inputString, whitespace)) != null) {
                didMatch = true;
            }


            if (didMatch) {
                inputString = m.replaceFirst("");
            } else {
                throw new LexicalError();
            }
        }
        tokenList.add(new KeywordToken("SENTINEL"));

        System.out.println(tokenList);
    }
}
