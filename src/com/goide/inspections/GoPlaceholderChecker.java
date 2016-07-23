/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

package com.goide.inspections;

import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.goide.GoConstants.TESTING_PATH;
import static com.goide.inspections.GoPlaceholderChecker.PrintfArgumentType.*;

public class GoPlaceholderChecker {

  // This holds the name of the known formatting functions and position of the string to be formatted
  private static final Map<String, Integer> FORMATTING_FUNCTIONS = ContainerUtil.newHashMap(
    Pair.pair("errorf", 0),
    Pair.pair("fatalf", 0),
    Pair.pair("fprintf", 1),
    Pair.pair("fscanf", 1),
    Pair.pair("logf", 0),
    Pair.pair("panicf", 0),
    Pair.pair("printf", 0),
    Pair.pair("scanf", 0),
    Pair.pair("skipf", 0),
    Pair.pair("sprintf", 0),
    Pair.pair("sscanf", 1));

  private static final Set<String> PRINTING_FUNCTIONS = ContainerUtil.newHashSet(
    "error",
    "error",
    "fatal",
    "fprint",
    "fprintln",
    "log",
    "panic",
    "panicln",
    "print",
    "println",
    "sprint",
    "sprintln"
  );

  protected enum PrintfArgumentType {
    ANY(-1),
    BOOL(1),
    INT(2),
    RUNE(3),
    STRING(4),
    FLOAT(5),
    COMPLEX(6),
    POINTER(7);

    private int myMask;

    PrintfArgumentType(int mask) {
      myMask = mask;
    }

    public int getValue() {
      return myMask;
    }
  }

  enum PrintVerb {
    Percent('%', "", 0),
    b('b', " -+.0", INT.getValue() | FLOAT.getValue() | COMPLEX.getValue()),
    c('c', "-", RUNE.getValue() | INT.getValue()),
    d('d', " -+.0", INT.getValue()),
    e('e', " -+.0", FLOAT.getValue() | COMPLEX.getValue()),
    E('E', " -+.0", FLOAT.getValue() | COMPLEX.getValue()),
    f('f', " -+.0", FLOAT.getValue() | COMPLEX.getValue()),
    F('F', " -+.0", FLOAT.getValue() | COMPLEX.getValue()),
    g('g', " -+.0", FLOAT.getValue() | COMPLEX.getValue()),
    G('G', " -+.0", FLOAT.getValue() | COMPLEX.getValue()),
    o('o', " -+.0#", INT.getValue()),
    p('p', "-#", POINTER.getValue()),
    q('q', " -+.0#", RUNE.getValue() | INT.getValue() | STRING.getValue()),
    s('s', " -+.0", STRING.getValue()),
    t('t', "-", BOOL.getValue()),
    T('T', "-", ANY.getValue()),
    U('U', "-#", RUNE.getValue() | INT.getValue()),
    V('v', " -+.0#", ANY.getValue()),
    x('x', " -+.0#", RUNE.getValue() | INT.getValue() | STRING.getValue()),
    X('X', " -+.0#", RUNE.getValue() | INT.getValue() | STRING.getValue());

    private char myVerb;
    private String myFlags;
    private int myMask;

    PrintVerb(char verb, String flags, int mask) {
      myVerb = verb;
      myFlags = flags;
      myMask = mask;
    }

    public char getVerb() {
      return myVerb;
    }

    @NotNull
    public String getFlags() {
      return myFlags;
    }

    public int getMask() {
      return myMask;
    }

    @Nullable
    public static PrintVerb getByVerb(char verb) {
      for (PrintVerb v : values()) {
        if (verb == v.getVerb()) return v;
      }
      return null;
    }
  }

  public static boolean isFormattingFunction(String functionName) {
    return FORMATTING_FUNCTIONS.containsKey(functionName);
  }

  public static boolean isPrintingFunction(String functionName) {
    return PRINTING_FUNCTIONS.contains(functionName);
  }

  static class Placeholder {
    private final String placeholder;
    private final int startPos;
    private final State state;
    private final PrintVerb verb;
    private final List<Integer> arguments;
    private final String flags;

    enum State {
      OK,
      MISSING_VERB_AT_END,
      ARGUMENT_INDEX_NOT_NUMERIC
    }

    Placeholder(State state, int startPos, String placeholder, String flags, List<Integer> arguments, PrintVerb verb) {
      this.placeholder = placeholder;
      this.startPos = startPos;
      this.verb = verb;
      this.state = state;
      this.arguments = arguments;
      this.flags = flags;
    }

    public String getPlaceholder() {
      return placeholder;
    }

    public int getPosition() {
      return arguments.get(arguments.size() - 1);
    }

    public State getState() {
      return state;
    }

    public PrintVerb getVerb() {
      return verb;
    }

    public List<Integer> getArguments() {
      return arguments;
    }

    public String getFlags() {
      return flags;
    }

    public int getStartPos() {
      return startPos;
    }
  }

  @NotNull
  public static List<Placeholder> parsePrintf(@NotNull String placeholderText) {
    List<Placeholder> placeholders = ContainerUtil.newArrayList();
    int argNum = 1;
    int w;
    for (int i = 0; i < placeholderText.length(); i += w) {
      w = 1;
      if (placeholderText.charAt(i) == '%') {
        FormatState state = parsePrintfVerb(placeholderText.substring(i), i, argNum);
        w = state.format.length();

        // We are not interested in %% which prints %
        if (state.state == Placeholder.State.OK && state.verb == PrintVerb.Percent) {
          // Special magic case for allowing things like %*% to pass (which are sadly valid expressions)
          if (state.format.length() == 2) continue;
        }

        placeholders.add(state.toPlaceholder());

        // We only consider ok states as valid to increase the number of arguments. Should we?
        if (state.state != Placeholder.State.OK) continue;

        if (!state.indexed) {
          if (!state.argNums.isEmpty()) {
            int maxArgNum = Collections.max(state.argNums);
            if (argNum < maxArgNum) {
              argNum = maxArgNum;
            }
          }
        }
        else {
          argNum = state.argNums.get(state.argNums.size() - 1);
        }
        argNum++;
      }
    }

    return placeholders;
  }

  protected static int getPlaceholderPosition(@NotNull GoFunctionOrMethodDeclaration function) {
    Integer position = FORMATTING_FUNCTIONS.get(StringUtil.toLowerCase(function.getName()));
    if (position != null) {
      String importPath = function.getContainingFile().getImportPath(false);
      if ("fmt".equals(importPath) || "log".equals(importPath) || TESTING_PATH.equals(importPath)) {
        return position;
      }
    }
    return -1;
  }

  private static class FormatState {
    @Nullable private PrintVerb verb;   // the format verb: 'd' for "%d"
    private String format;              // the full format directive from % through verb, "%.3d"
    @NotNull private String flags = ""; // the list of # + etc
    private boolean indexed;            // whether an indexing expression appears: %[1]d
    private final int startPos;         // index of the first character of the placeholder in the formatting string

    // the successive argument numbers that are consumed, adjusted to refer to actual arg in call
    private final List<Integer> argNums = ContainerUtil.newArrayList();

    // Keep track of the parser state
    private Placeholder.State state;

    // Used only during parse.
    private int argNum;           // Which argument we're expecting to format now
    private int nBytes = 1;       // number of bytes of the format string consumed

    FormatState(String format, int startPos, int argNum) {
      this.format = format;
      this.startPos = startPos;
      this.argNum = argNum;
    }

    @NotNull
    private Placeholder toPlaceholder() {
      return new Placeholder(state, startPos, format, flags, argNums, verb);
    }
  }

  @NotNull
  private static FormatState parsePrintfVerb(@NotNull String format, int startPos, int argNum) {
    FormatState state = new FormatState(format, startPos, argNum);

    parseFlags(state);

    if (!parseIndex(state)) return state;

    // There may be a width
    if (!parseNum(state)) return state;

    if (!parsePrecision(state)) return state;

    // Now a verb, possibly prefixed by an index (which we may already have)
    if (!parseIndex(state)) return state;

    if (state.nBytes == format.length()) {
      state.state = Placeholder.State.MISSING_VERB_AT_END;
      return state;
    }

    state.verb = PrintVerb.getByVerb(state.format.charAt(state.nBytes));
    if (state.verb != null && state.verb != PrintVerb.Percent) state.argNums.add(state.argNum);
    state.nBytes++;
    state.format = state.format.substring(0, state.nBytes);
    state.state = Placeholder.State.OK;

    return state;
  }

  public static boolean hasPlaceholder(@Nullable String formatString) {
    return formatString != null && StringUtil.containsChar(formatString, '%');
  }

  private static void parseFlags(@NotNull FormatState state) {
    String knownFlags = "#0+- ";
    StringBuilder flags = new StringBuilder(state.flags);
    while (state.nBytes < state.format.length()) {
      if (StringUtil.containsChar(knownFlags, state.format.charAt(state.nBytes))) {
        flags.append(state.format.charAt(state.nBytes));
      }
      else {
        state.flags = flags.toString();
        return;
      }
      state.nBytes++;
    }
    state.flags = flags.toString();
  }

  private static void scanNum(@NotNull FormatState state) {
    while (state.nBytes < state.format.length()) {
      if (!StringUtil.isDecimalDigit(state.format.charAt(state.nBytes))) {
        return;
      }
      state.nBytes++;
    }
  }

  private static boolean parseIndex(@NotNull FormatState state) {
    if (state.nBytes == state.format.length() || state.format.charAt(state.nBytes) != '[') return true;

    state.indexed = true;
    state.nBytes++;
    int start = state.nBytes;
    scanNum(state);
    if (state.nBytes == state.format.length() || state.nBytes == start || state.format.charAt(state.nBytes) != ']') {
      state.state = Placeholder.State.ARGUMENT_INDEX_NOT_NUMERIC;
      return false;
    }

    int arg;
    try {
      arg = Integer.parseInt(state.format.substring(start, state.nBytes));
    }
    catch (NumberFormatException ignored) {
      state.state = Placeholder.State.ARGUMENT_INDEX_NOT_NUMERIC;
      return false;
    }

    state.nBytes++;
    state.argNum = arg;

    return true;
  }

  private static boolean parseNum(@NotNull FormatState state) {
    if (state.nBytes < state.format.length() && state.format.charAt(state.nBytes) == '*') {
      state.nBytes++;
      state.argNums.add(state.argNum);
      state.argNum++;
    }
    else {
      scanNum(state);
    }

    return true;
  }

  private static boolean parsePrecision(@NotNull FormatState state) {
    if (state.nBytes < state.format.length() && state.format.charAt(state.nBytes) == '.') {
      state.flags += '.';
      state.nBytes++;
      if (!parseIndex(state)) return false;
      if (!parseNum(state)) return false;
    }

    return true;
  }
}
