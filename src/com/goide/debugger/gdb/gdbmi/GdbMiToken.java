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

import org.jetbrains.annotations.Nullable;

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
  @Nullable public String value = null;

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
  @Nullable
  public String toString() {
    return type + (value == null ? "" : ": " + value);
  }
}
