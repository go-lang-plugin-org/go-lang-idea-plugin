package ro.redeul.google.go.compilation;

import com.intellij.openapi.compiler.CompilerMessageCategory;

/**
 * Go Compiler message implementation.
 *
 * <p/>
 * Author: Alexandre Normand
 * Date: 11-05-28
 * Time: 8:45 PM
 */
public class CompilerMessage {

    private CompilerMessageCategory category;
    private String message;
    private String fileName;
    private int row;
    private int column = -1;

    /**
     * Constructor
     * {@inheritDoc}
     */
    protected CompilerMessage(CompilerMessageCategory category, String message, String fileName, int row, int column) {
        this.category = category;
        this.message = message;
        this.fileName = fileName;
        this.row = row;
        this.column = column;
    }

    public CompilerMessageCategory getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }

    public String getFileName() {
        return fileName;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CompilerMessage");
        sb.append("{category=").append(category);
        sb.append(", message='").append(message).append('\'');
        sb.append(", fileName='").append(fileName).append('\'');
        sb.append(", row=").append(row);
        sb.append(", column=").append(column);
        sb.append('}');
        return sb.toString();
    }
}
