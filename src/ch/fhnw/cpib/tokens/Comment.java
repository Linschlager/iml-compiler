package ch.fhnw.cpib.tokens;

public class Comment extends Token {
    private final String content;

    public Comment(String content) {
        super(Terminal.COMMENT);
        this.content = content;
    }

    @Override
    public String toString() {
        return "Comment(" + content + ')';
    }
}
