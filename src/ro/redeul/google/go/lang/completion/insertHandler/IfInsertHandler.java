package ro.redeul.google.go.lang.completion.insertHandler;

public class IfInsertHandler extends CurlyBracesInsertHandler {

    @Override
    protected String getInsertionText() {
        return " _ {\n\n}";
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
