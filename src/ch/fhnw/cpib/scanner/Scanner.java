package ch.fhnw.cpib.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Scanner {

    public static void scan(String inputString) {
        List<Token> tokenList = new ArrayList<>();
        String previousString = inputString;
        while (inputString.length() > 0) {

            Pattern p = TokenType.types.get("reservedid").regex;
            var m= p.matcher(inputString);
            if (m.matches()) {
                m.results().forEach(res -> {
                    tokenList.add(new Token(TokenType.types.get("reservedid"), res.group()));
                });
                inputString = m.replaceFirst("");
            }


            if (previousString.equals(inputString)) break; // no change -> done
            previousString = inputString;
        }

        System.out.println(tokenList);
    }
}
