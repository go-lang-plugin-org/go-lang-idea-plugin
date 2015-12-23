/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan, Stuart Carnie
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

package com.plan9.intel.ide.highlighting;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class AsmIntelSyntaxHighlightingColors {
  public static final TextAttributesKey LINE_COMMENT = createTextAttributesKey("com.plan9.LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
  public static final TextAttributesKey INSTRUCTION = createTextAttributesKey("com.plan9.INSTRUCTION", DefaultLanguageHighlighterColors.PREDEFINED_SYMBOL);
  public static final TextAttributesKey PSEUDO_INSTRUCTION = createTextAttributesKey("com.plan9.PSEUDO_INSTRUCTION", INSTRUCTION);
  public static final TextAttributesKey KEYWORD = createTextAttributesKey("com.plan9.KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
  public static final TextAttributesKey STRING = createTextAttributesKey("com.plan9.STRING", DefaultLanguageHighlighterColors.STRING);
  public static final TextAttributesKey NUMBER = createTextAttributesKey("com.plan9.NUMBER", DefaultLanguageHighlighterColors.NUMBER);
  public static final TextAttributesKey LABEL = createTextAttributesKey("com.plan9.LABEL", DefaultLanguageHighlighterColors.LABEL);
  public static final TextAttributesKey FLAG = createTextAttributesKey("com.plan9.FLAG", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE);
  public static final TextAttributesKey REGISTER = createTextAttributesKey("com.plan9.REGISTER", DefaultLanguageHighlighterColors.PARAMETER);
  public static final TextAttributesKey PARENTHESIS = createTextAttributesKey("com.plan9.PARENTHESIS", DefaultLanguageHighlighterColors.PARENTHESES);
  public static final TextAttributesKey OPERATOR = createTextAttributesKey("com.plan9.OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
  public static final TextAttributesKey DIRECTIVE = createTextAttributesKey("com.plan9.DIRECTIVE", DefaultLanguageHighlighterColors.KEYWORD);
  public static final TextAttributesKey IDENTIFIER = createTextAttributesKey("com.plan9.IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER);
  public static final TextAttributesKey PREPROCESSOR = createTextAttributesKey("com.plan9.PREPROCESSOR", DefaultLanguageHighlighterColors.KEYWORD);

}
