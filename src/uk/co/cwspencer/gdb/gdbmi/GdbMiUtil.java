package uk.co.cwspencer.gdb.gdbmi;

/**
 * Utility functions relating to the use of GDB/MI.
 */
public class GdbMiUtil {
    // Mapping from hexadecimal digits to ASCII characters
    private static final char[] m_hexAscii =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Formats a string into the format required by GDB/MI.
     *
     * @param string The string to format.
     * @return The formatted string.
     */
    public static String formatGdbString(String string, Boolean autoQuote) {
        StringBuilder sb = new StringBuilder();
        if (autoQuote) {
            sb.append("\"");
        }
        for (int i = 0; i < string.length(); ++i) {
            char ch = string.charAt(i);

            // Restrict the use of escape characters to those that are likely to be safe
            if (ch == '\n') {
                sb.append("\\n");
            } else if (ch == '\r') {
                sb.append("\\r");
            } else if (ch == '"') {
                sb.append("\\\"");
            } else if (ch == '\\') {
                sb.append("\\\\");
            } else if (ch <= 0x1f || ch >= 0x7f) {
                sb.append("\\x");
                sb.append(m_hexAscii[ch >> 4]);
                sb.append(m_hexAscii[ch & 0x0f]);
            } else {
                sb.append(ch);
            }
        }
        if (autoQuote) {
            sb.append("\"");
        }
        return sb.toString();
    }

    public static String formatGdbString(String string) {
        return formatGdbString(string, true);
    }
}
