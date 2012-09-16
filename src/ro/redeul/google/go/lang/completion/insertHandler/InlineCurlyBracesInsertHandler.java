package ro.redeul.google.go.lang.completion.insertHandler;

public class InlineCurlyBracesInsertHandler extends KeywordInsertionHandler {
    @Override
    protected String getInsertionText() {
        return " {}";
    }

    @Override
    protected int nextCaretPosition() {
        return 3;
    }
}
