package ro.redeul.google.go.lang.completion.insertHandler;

public class CurlyBracesInsertHandler extends KeywordInsertionHandler {

    @Override
    protected String getInsertionText() {
        return super.getInsertionText() + "{\n}";
    }

    @Override
    protected boolean shouldPressEnter() {
        return true;
    }

    @Override
    protected int nextCaretPosition() {
        return super.getInsertionText().length() + 1;
    }

    @Override
    protected boolean shouldReformat() {
        return true;
    }
}