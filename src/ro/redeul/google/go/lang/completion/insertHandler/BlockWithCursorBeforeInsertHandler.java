package ro.redeul.google.go.lang.completion.insertHandler;

public class BlockWithCursorBeforeInsertHandler extends CurlyBracesInsertHandler {

    @Override
    protected String getInsertionText() {
        return " {\n\n}";
    }

    @Override
    protected int nextCaretPosition() {
        return 1;
    }

    @Override
    protected boolean shouldPressEnter() {
        return false;
    }

}
