/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

package com.goide.highlighting;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class GoSyntaxHighlightingColors {
  public static final TextAttributesKey LINE_COMMENT = createTextAttributesKey("GO_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
  public static final TextAttributesKey BLOCK_COMMENT = createTextAttributesKey("GO_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
  public static final TextAttributesKey KEYWORD = createTextAttributesKey("GO_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
  public static final TextAttributesKey STRING = createTextAttributesKey("GO_STRING", DefaultLanguageHighlighterColors.STRING);
  public static final TextAttributesKey NUMBER = createTextAttributesKey("GO_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
  public static final TextAttributesKey BRACKETS = createTextAttributesKey("GO_BRACKET", DefaultLanguageHighlighterColors.BRACKETS);
  public static final TextAttributesKey BRACES = createTextAttributesKey("GO_BRACES", DefaultLanguageHighlighterColors.BRACES);
  public static final TextAttributesKey PARENTHESES = createTextAttributesKey("GO_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
  public static final TextAttributesKey OPERATOR = createTextAttributesKey("GO_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
  public static final TextAttributesKey IDENTIFIER = createTextAttributesKey("GO_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
  public static final TextAttributesKey DOT = createTextAttributesKey("GO_DOT", DefaultLanguageHighlighterColors.DOT);
  public static final TextAttributesKey SEMICOLON = createTextAttributesKey("GO_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON);
  public static final TextAttributesKey COLON = createTextAttributesKey("GO_COLON", HighlighterColors.TEXT);
  public static final TextAttributesKey COMMA = createTextAttributesKey("GO_COMMA", DefaultLanguageHighlighterColors.COMMA);
  public static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("GO_BAD_TOKEN", HighlighterColors.BAD_CHARACTER);
  public static final TextAttributesKey TYPE_SPECIFICATION = createTextAttributesKey("GO_TYPE_SPECIFICATION", DefaultLanguageHighlighterColors.CLASS_NAME);
  public static final TextAttributesKey TYPE_REFERENCE = createTextAttributesKey("GO_TYPE_REFERENCE", DefaultLanguageHighlighterColors.CLASS_REFERENCE);
  public static final TextAttributesKey BUILTIN_TYPE_REFERENCE = createTextAttributesKey("GO_BUILTIN_TYPE_REFERENCE", DefaultLanguageHighlighterColors.CLASS_REFERENCE);
  public static final TextAttributesKey BUILTIN_FUNCTION = createTextAttributesKey("GO_BUILTIN_FUNCTION", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
  public static final TextAttributesKey EXPORTED_FUNCTION = createTextAttributesKey("GO_EXPORTED_FUNCTION", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
  public static final TextAttributesKey LOCAL_FUNCTION = createTextAttributesKey("GO_LOCAL_FUNCTION", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
  public static final TextAttributesKey PACKAGE_EXPORTED_INTERFACE = createTextAttributesKey("GO_PACKAGE_EXPORTED_INTERFACE", DefaultLanguageHighlighterColors.INTERFACE_NAME);
  public static final TextAttributesKey PACKAGE_EXPORTED_STRUCT = createTextAttributesKey("GO_PACKAGE_EXPORTED_STRUCT", DefaultLanguageHighlighterColors.CLASS_NAME);
  public static final TextAttributesKey PACKAGE_EXPORTED_CONSTANT = createTextAttributesKey("GO_PACKAGE_EXPORTED_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT);
  public static final TextAttributesKey PACKAGE_EXPORTED_VARIABLE = createTextAttributesKey("GO_PACKAGE_EXPORTED_VARIABLE", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE);
  public static final TextAttributesKey PACKAGE_LOCAL_INTERFACE = createTextAttributesKey("GO_PACKAGE_LOCAL_INTERFACE", DefaultLanguageHighlighterColors.INTERFACE_NAME);
  public static final TextAttributesKey PACKAGE_LOCAL_STRUCT = createTextAttributesKey("GO_PACKAGE_LOCAL_STRUCT", DefaultLanguageHighlighterColors.CLASS_NAME);
  public static final TextAttributesKey PACKAGE_LOCAL_CONSTANT = createTextAttributesKey("GO_PACKAGE_LOCAL_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT);
  public static final TextAttributesKey PACKAGE_LOCAL_VARIABLE = createTextAttributesKey("GO_PACKAGE_LOCAL_VARIABLE", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
  public static final TextAttributesKey STRUCT_EXPORTED_MEMBER = createTextAttributesKey("GO_STRUCT_EXPORTED_MEMBER", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE);
  public static final TextAttributesKey STRUCT_LOCAL_MEMBER = createTextAttributesKey("GO_STRUCT_LOCAL_MEMBER", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
  public static final TextAttributesKey METHOD_RECEIVER = createTextAttributesKey("GO_METHOD_RECEIVER", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
  public static final TextAttributesKey FUNCTION_PARAMETER = createTextAttributesKey("GO_FUNCTION_PARAMETER", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
  public static final TextAttributesKey LOCAL_CONSTANT = createTextAttributesKey("GO_LOCAL_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT);
  public static final TextAttributesKey LOCAL_VARIABLE = createTextAttributesKey("GO_LOCAL_VARIABLE", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
  public static final TextAttributesKey SCOPE_VARIABLE = createTextAttributesKey("GO_SCOPE_VARIABLE", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
  public static final TextAttributesKey LABEL = createTextAttributesKey("GO_LABEL", DefaultLanguageHighlighterColors.LABEL);
  private GoSyntaxHighlightingColors() {
  }
}
