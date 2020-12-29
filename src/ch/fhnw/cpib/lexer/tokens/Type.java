package ch.fhnw.cpib.lexer.tokens;

public class Type extends Token {
    public final Attr attr;

    public enum Attr {
        BOOL, INT32, INT64, INT1024;

        @Override
        public String toString() {
            return switch (this) {
                case BOOL -> "bool";
                case INT32 -> "int32";
                case INT64 -> "int64";
                case INT1024 -> "int1024";
            };
        }
    }
    public Type(Attr attr) {
        super(Terminal.TYPE);
        this.attr = attr;
    }

    @Override
    public String toString() {
        return "Type(" + attr + ')';
    }
}
