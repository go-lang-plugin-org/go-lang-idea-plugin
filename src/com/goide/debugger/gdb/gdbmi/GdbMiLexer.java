/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.debugger.gdb.gdbmi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Lexer for GDB/MI output.
 */
public class GdbMiLexer {
  // Possible states for the lexer FSM
  private enum FsmState {
    Idle,                  // Ready to read a new token
    UserToken,             // Reading a user-token
    Identifier,            // Reading an identifier
    CString,               // Reading a C string
    CStringEscape,         // Reading a C string escape sequence
    CStringEscapeHexHead,  // Ready to read the first digit from a hexadecimal escape sequence
    CStringEscapeHex,      // Reading a hexadecimal escape sequence
    CStringEscapeOct1,     // Read the first character of an octal escape sequence
    CStringEscapeOct2,     // Read the second character of an octal escape sequence
    GdbSuffix1,            // Partially read GDB suffix "("
    GdbSuffix2,            // Partially read GDB suffix "(g"
    GdbSuffix3,            // Partially read GDB suffix "(gd"
    GdbSuffix4,            // Partially read GDB suffix "(gdb"
    GdbSuffix5,            // Read GDB suffix
    CrLf                   // Ready to optionally read LF
  }

  // State of the lexer FSM
  @NotNull private FsmState myState = FsmState.Idle;

  // Temporary store for partially read tokens
  @Nullable private StringBuilder myPartialToken;

  // List of unprocessed tokens
  @NotNull private final List<GdbMiToken> myTokens = new ArrayList<GdbMiToken>();

  /**
   * Returns a list of unprocessed tokens. The caller should erase items from this list as they
   * are processed.
   *
   * @return A list of unprocessed tokens.
   */
  @NotNull
  public List<GdbMiToken> getTokens() {
    return myTokens;
  }

  /**
   * Processes the given data.
   *
   * @param data   Data read from the GDB process.
   * @param length Number of bytes from data to process.
   */
  public void process(byte[] data, int length) {
    for (int i = 0; i != length; ++i) {
      switch (myState) {
        case Idle:
          // Legal tokens:
          // User token (digits)
          // ^, *, +, =, ~, @, &, ,, ", {, }, [, ]
          // Identifier (string)
          // CRLF
          // "(gdb)"
          switch (data[i]) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
              myPartialToken = new StringBuilder();
              myPartialToken.append((char)data[i]);
              myState = FsmState.UserToken;
              break;

            case '^':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.ResultRecordPrefix));
              break;

            case '*':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.ExecAsyncOutputPrefix));
              break;

            case '+':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StatusAsyncOutputPrefix));
              break;

            case '=':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.NotifyAsyncOutputPrefix));
              break;

            case '~':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.ConsoleStreamOutputPrefix));
              break;

            case '@':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.TargetStreamOutputPrefix));
              break;

            case '&':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.LogStreamOutputPrefix));
              break;

            case ',':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.ResultSeparator));
              break;

            case '"':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StringPrefix));
              myPartialToken = new StringBuilder();
              myState = FsmState.CString;
              break;

            case '{':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.TuplePrefix));
              break;

            case '}':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.TupleSuffix));
              break;

            case '[':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.ListPrefix));
              break;

            case ']':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.ListSuffix));
              break;

            case '(':
              myState = FsmState.GdbSuffix1;
              break;

            case '\r':
            case '\n':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.NewLine));
              myState = FsmState.CrLf;
              break;

            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
              myPartialToken = new StringBuilder();
              myPartialToken.append((char)data[i]);
              myState = FsmState.Identifier;
              break;

            default:
              throw new IllegalArgumentException("Unexpected character: '" + data[i] + "'");
          }
          break;

        case UserToken:
          // Legal tokens:
          // User token (digits)
          // Anything else is reprocessed
          switch (data[i]) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
              myPartialToken.append((char)data[i]);
              break;

            default:
              myTokens.add(new GdbMiToken(GdbMiToken.Type.UserToken,
                                          myPartialToken.toString()));
              myState = FsmState.Idle;
              --i;
          }
          break;

        case Identifier:
          // Legal tokens:
          // For an identifier: "_", 0-9, a-z, A-Z
          // "=" is handled specially as it means something else if not used after an
          // identifier
          // Anything else is reprocessed
          if (data[i] == '_' || data[i] == '-' ||
              (data[i] >= '0' && data[i] <= '9') ||
              (data[i] >= 'a' && data[i] <= 'z') ||
              (data[i] >= 'A' && data[i] <= 'Z')) {
            myPartialToken.append((char)data[i]);
          }
          else if (data[i] == '=') {
            myTokens.add(new GdbMiToken(GdbMiToken.Type.Identifier,
                                        myPartialToken.toString()));
            myTokens.add(new GdbMiToken(GdbMiToken.Type.Equals));
            myPartialToken = null;
            myState = FsmState.Idle;
          }
          else {
            myTokens.add(new GdbMiToken(GdbMiToken.Type.Identifier,
                                        myPartialToken.toString()));
            myPartialToken = null;
            myState = FsmState.Idle;
            --i;
          }
          break;

        case CString:
          // Legal tokens:
          // Anything except CR or LF
          // Escape sequences:
          //   \'
          //   \"
          //   \?
          //   \\
          //   \a
          //   \b
          //   \f
          //   \n
          //   \r
          //   \t
          //   \v
          //   \[octal digits]
          //   \x[hexadecimal digits]
          switch (data[i]) {
            case '"':
              if (myPartialToken.length() != 0) {
                myTokens.add(new GdbMiToken(GdbMiToken.Type.StringFragment,
                                            myPartialToken.toString()));
              }
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StringSuffix));
              myPartialToken = null;
              myState = FsmState.Idle;
              break;

            case '\\':
              if (myPartialToken.length() != 0) {
                myTokens.add(new GdbMiToken(GdbMiToken.Type.StringFragment,
                                            myPartialToken.toString()));
              }
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapePrefix));
              myState = FsmState.CStringEscape;
              myPartialToken = null;
              break;

            case '\r':
            case '\n':
              throw new IllegalArgumentException("Unexpected character: '" + data[i] + "'");

            default:
              myPartialToken.append((char)data[i]);
          }
          break;

        case CStringEscape:
          // Legal tokens:
          // "'", """, "?", "\", "a", "b", "f", "n", "r", "t", "v", "x", 0-7
          switch (data[i]) {
            case '\'':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapeApostrophe));
              myPartialToken = new StringBuilder();
              myState = FsmState.CString;
              break;

            case '"':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapeQuote));
              myPartialToken = new StringBuilder();
              myState = FsmState.CString;
              break;

            case '?':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapeQuestion));
              myPartialToken = new StringBuilder();
              myState = FsmState.CString;
              break;

            case '\\':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapeBackslash));
              myPartialToken = new StringBuilder();
              myState = FsmState.CString;
              break;

            case 'a':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapeAlarm));
              myPartialToken = new StringBuilder();
              myState = FsmState.CString;
              break;

            case 'b':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapeBackspace));
              myPartialToken = new StringBuilder();
              myState = FsmState.CString;
              break;

            case 'f':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapeFormFeed));
              myPartialToken = new StringBuilder();
              myState = FsmState.CString;
              break;

            case 'n':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapeNewLine));
              myPartialToken = new StringBuilder();
              myState = FsmState.CString;
              break;

            case 'r':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapeCarriageReturn));
              myPartialToken = new StringBuilder();
              myState = FsmState.CString;
              break;

            case 't':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapeHorizontalTab));
              myPartialToken = new StringBuilder();
              myState = FsmState.CString;
              break;

            case 'v':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapeVerticalTab));
              myPartialToken = new StringBuilder();
              myState = FsmState.CString;
              break;

            case 'x':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapeHexPrefix));
              myPartialToken = new StringBuilder();
              myState = FsmState.CStringEscapeHexHead;
              break;

            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
              myPartialToken = new StringBuilder();
              myPartialToken.append((char)data[i]);
              myState = FsmState.CStringEscapeOct1;
              break;

            default:
              throw new IllegalArgumentException("Unexpected character: '" + data[i] + "'");
          }
          break;

        case CStringEscapeHexHead:
          // Legal tokens:
          // Hex digits: 0-9, a-f, A-F
          if ((data[i] >= '0' && data[i] <= '9') ||
              (data[i] >= 'a' && data[i] <= 'f') ||
              (data[i] >= 'A' && data[i] <= 'F')) {
            myPartialToken.append((char)data[i]);
            myState = FsmState.CStringEscapeHex;
          }
          else {
            throw new IllegalArgumentException("Unexpected character: '" + data[i] + "'");
          }
          break;

        case CStringEscapeHex:
          // Legal tokens:
          // Hex digits: 0-9, a-f, A-F
          // Else reprocess as normal C string character
          if ((data[i] >= '0' && data[i] <= '9') ||
              (data[i] >= 'a' && data[i] <= 'f') ||
              (data[i] >= 'A' && data[i] <= 'F')) {
            myPartialToken.append((char)data[i]);
          }
          else {
            myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapeHexValue,
                                        myPartialToken.toString()));
            myPartialToken = new StringBuilder();
            myState = FsmState.CString;
            --i;
          }
          break;

        case CStringEscapeOct1:
          // Legal tokens:
          // Oct digits: 0-7
          // Else reprocess as normal C string character
          if (data[i] >= '0' && data[i] <= '7') {
            myPartialToken.append((char)data[i]);
            myState = FsmState.CStringEscapeOct2;
          }
          else {
            myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapeOctValue,
                                        myPartialToken.toString()));
            myPartialToken = new StringBuilder();
            myState = FsmState.CString;
            --i;
          }
          break;

        case CStringEscapeOct2:
          // Legal tokens:
          // Oct digits: 0-7
          // Else reprocess as normal C string character
          if (data[i] >= '0' && data[i] <= '7') {
            myPartialToken.append((char)data[i]);
          }
          else {
            --i;
          }
          myTokens.add(new GdbMiToken(GdbMiToken.Type.StringEscapeOctValue,
                                      myPartialToken.toString()));
          myPartialToken = new StringBuilder();
          myState = FsmState.CString;
          break;

        case GdbSuffix1:
          // Read so far: "("
          switch (data[i]) {
            case 'g':
              myState = FsmState.GdbSuffix2;
              break;

            default:
              throw new IllegalArgumentException("Unexpected character: '" + data[i] + "'");
          }
          break;

        case GdbSuffix2:
          // Read so far: "(g"
          switch (data[i]) {
            case 'd':
              myState = FsmState.GdbSuffix3;
              break;

            default:
              throw new IllegalArgumentException("Unexpected character: '" + data[i] + "'");
          }
          break;

        case GdbSuffix3:
          // Read so far: "(gd"
          switch (data[i]) {
            case 'b':
              myState = FsmState.GdbSuffix4;
              break;

            default:
              throw new IllegalArgumentException("Unexpected character: '" + data[i] + "'");
          }
          break;

        case GdbSuffix4:
          // Read so far: "(gdb"
          switch (data[i]) {
            case ')':
              myTokens.add(new GdbMiToken(GdbMiToken.Type.GdbSuffix));
              myState = FsmState.GdbSuffix5;
              break;

            default:
              throw new IllegalArgumentException("Unexpected character: '" + data[i] + "'");
          }
          break;

        case GdbSuffix5:
          // GDB seems to print a space here, even though the documentation doesn't mention
          // this. We just ignore it if it does
          switch (data[i]) {
            case ' ':
              myState = FsmState.Idle;
              break;

            default:
              // Reprocess the character
              --i;
              myState = FsmState.Idle;
          }
          break;

        case CrLf:
          // Legal tokens:
          // \n
          // If the character is not '\n' the state is changed to Idle and the character
          // reprocessed
          switch (data[i]) {
            case '\n':
              myState = FsmState.Idle;
              break;

            default:
              // Reprocess the character
              myState = FsmState.Idle;
              --i;
          }
          break;

        default:
          throw new IllegalArgumentException("Unexpected lexer FSM state: " + myState);
      }
    }
  }
}
