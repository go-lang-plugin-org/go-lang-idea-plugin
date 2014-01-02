package com.goide.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;

public class GoParserUtil extends GeneratedParserUtilBase {
  @SuppressWarnings("UnusedParameters")
  public static boolean reportError(PsiBuilder builder, int level) {
    builder.mark().error("Expected new line or semicolon");
    return true;
  }
}
