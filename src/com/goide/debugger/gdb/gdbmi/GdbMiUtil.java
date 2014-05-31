package com.goide.debugger.gdb.gdbmi;

import org.jetbrains.annotations.NotNull;

public class GdbMiUtil { // todo: find something from the util package
  private static final char[] HEX_ASCII = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  /**
   * Formats a string into the format required by GDB/MI.
   *
   * @param string The string to format.
   * @return The formatted string.
   */
  @NotNull
  public static String formatGdbString(@NotNull String string) {
    StringBuilder sb = new StringBuilder();
    sb.append("\"");
    for (int i = 0; i < string.length(); ++i) {
      char ch = string.charAt(i);

      // Restrict the use of escape characters to those that are likely to be safe
      if (ch == '\n') {
        sb.append("\\n");
      }
      else if (ch == '\r') {
        sb.append("\\r");
      }
      else if (ch == '"') {
        sb.append("\\\"");
      }
      else if (ch == '\\') {
        sb.append("\\\\");
      }
      else if (ch <= 0x1f || ch >= 0x7f) {
        sb.append("\\x");
        sb.append(HEX_ASCII[ch >> 4]);
        sb.append(HEX_ASCII[ch & 0x0f]);
      }
      else {
        sb.append(ch);
      }
    }
    sb.append("\"");
    return sb.toString();
  }
}
