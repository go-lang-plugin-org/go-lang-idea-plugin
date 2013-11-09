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
class CompilerMessage {

    private final CompilerMessageCategory category;
    private final String message;
    private final String fileName;
    private final int row;
    private int column = -1;

    /**
     * Constructor
     * {@inheritDoc}
     */
    CompilerMessage(CompilerMessageCategory category, String message, String fileName, int row, int column) {
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
        return "CompilerMessage" + "{category=" + category + ", message='" + message + '\'' + ", fileName='" + fileName + '\'' + ", row=" + row + ", column=" + column + '}';
    }
}
