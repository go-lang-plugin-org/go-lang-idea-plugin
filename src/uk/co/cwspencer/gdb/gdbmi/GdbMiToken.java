package uk.co.cwspencer.gdb.gdbmi;

/**
 * Class representing a token read from a GDB/MI stream.
 */
public class GdbMiToken {
    /**
     * Possible token types.
     */
    public enum Type {
        UserToken,                  // String of digits
        ResultRecordPrefix,         // "^"
        ExecAsyncOutputPrefix,      // "*"
        StatusAsyncOutputPrefix,    // "+"
        NotifyAsyncOutputPrefix,    // "="
        ConsoleStreamOutputPrefix,  // "~"
        TargetStreamOutputPrefix,   // "@"
        LogStreamOutputPrefix,      // "&"
        Identifier,                 // result-class or async-class
        Equals,                     // "="
        ResultSeparator,            // ","
        StringPrefix,               // """
        StringFragment,             // Part of a string
        StringEscapePrefix,         // "\"
        StringEscapeApostrophe,     // "'"
        StringEscapeQuote,          // """
        StringEscapeQuestion,       // "?"
        StringEscapeBackslash,      // "\"
        StringEscapeAlarm,          // "a"
        StringEscapeBackspace,      // "b"
        StringEscapeFormFeed,       // "f"
        StringEscapeNewLine,        // "n"
        StringEscapeCarriageReturn, // "r"
        StringEscapeHorizontalTab,  // "t"
        StringEscapeVerticalTab,    // "v"
        StringEscapeHexPrefix,      // "x"
        StringEscapeHexValue,       // 1-* hexadecimal digits
        StringEscapeOctValue,       // 1-3 octal digits
        StringSuffix,               // """
        TuplePrefix,                // "{"
        TupleSuffix,                // "}"
        ListPrefix,                 // "["
        ListSuffix,                 // "]"
        NewLine,                    // "\r" or "\r\n"
        GdbSuffix                   // "(gdb)"
    }

    /**
     * The type of token.
     */
    public Type type;

    /**
     * The token value, if any.
     */
    public String value = null;

    /**
     * Constructor; sets the values.
     *
     * @param type  The type of the token.
     * @param value The value of the token.
     */
    public GdbMiToken(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Constructor; sets the type. The value is set to null.
     *
     * @param type The type of token.
     */
    public GdbMiToken(Type type) {
        this.type = type;
    }

    /**
     * Converts the token to a string.
     *
     * @return A string containing the type and, if set, the value.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        if (value != null) {
            sb.append(": ");
            sb.append(value);
        }
        return sb.toString();
    }
}
