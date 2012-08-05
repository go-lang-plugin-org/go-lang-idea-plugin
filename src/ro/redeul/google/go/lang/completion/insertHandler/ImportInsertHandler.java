package ro.redeul.google.go.lang.completion.insertHandler;

public class ImportInsertHandler extends KeywordInsertionHandler {
    @Override
    protected String getInsertionText() {
        return super.getInsertionText() + "(\n\"\"\n)\n";
    }

    @Override
    protected boolean shouldPressEnter() {
        return false;
    }

    @Override
    protected int nextCaretPosition() {
        return super.getInsertionText().length() + 3;
    }

    @Override
    protected boolean shouldReformat() {
        return true;
    }
}
