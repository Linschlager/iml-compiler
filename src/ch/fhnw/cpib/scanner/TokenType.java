package ch.fhnw.cpib.scanner;

import java.util.HashMap;
import java.util.regex.Pattern;

public class TokenType {

    public static final HashMap<String, TokenType> types;

    public static final TokenType[] validTypes;

    static {
        types = new HashMap<>();

        types.put("digit", new TokenType(Pattern.compile("([0-9]+)")));
        types.put("lowercase", new TokenType(Pattern.compile("([a-z]+)")));
        types.put("uppercase", new TokenType(Pattern.compile("([A-Z]+)")));
        types.put("letter", new TokenType(Pattern.compile("([a-zA-Z]+)"))); // lowercase | uppercase
        types.put("special", new TokenType(Pattern.compile("([!\"#$%&’()*+,-./:;<=>?@\\[\\]^_‘{|}~]+)")));
        types.put("space", new TokenType(Pattern.compile("[\\x20␣]+")));
        types.put("printable", new TokenType(Pattern.compile("[0-9a-zA-Z\\w]+")));
        types.put("linefeed", new TokenType(Pattern.compile(""))); // TODO
        types.put("carriret", new TokenType(Pattern.compile(""))); // TODO
        types.put("newline", new TokenType(Pattern.compile(""))); // TODO
        types.put("whitebase", new TokenType(Pattern.compile(""))); // TODO
        types.put("comment", new TokenType(Pattern.compile("//[0-9a-zA-Z\\w]*"))); // //printable*
        types.put("whitespace", new TokenType(Pattern.compile("//\\w*"))); // TODO (whitespace | comment) *
        types.put("reservedid", new TokenType(Pattern.compile("^(bool|call|const|copy|debugin|debugout|divE|divF|divT|do|else|endfun|endif|endproc|endprogram|endwhile|false|fun|global|if|in|init|inout|int1024|int32|int64|local|modE|modF|modT|not|out|proc|program|ref|returns|skip|then|true|var|while)\\s")));
        types.put("symbol", new TokenType(Pattern.compile("(\\(|,|\\)|:|;|:=|/\\\\\\?|\\\\/\\?|=|/=|<|>|<=|>=|\\+|-|\\*)\\w")));
        types.put("boollit", new TokenType(Pattern.compile("(true|false)\\w")));


        validTypes = new TokenType[] {
            types.get("reservedid"),
        };
    }

    public Pattern regex;
    private TokenType(Pattern regex) {
        this.regex = regex;
    }
}
