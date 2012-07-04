package ro.redeul.google.go.intentions;

public class IntentionExecutionException extends RuntimeException {
    /**
     * start offset relative to the processing element
     */
    private int startOffset = -1;
    private int length = -1;

    public IntentionExecutionException(String message) {
        super(message);
    }

    public IntentionExecutionException(String message, int startOffset, int length) {
        super(message);
        this.startOffset = startOffset;
        this.length = length;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getLength() {
        return length;
    }
}
