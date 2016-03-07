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

package com.plan9.intel.ide.highlighting;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.plan9.intel.lang.core.lexer.AsmIntelTokenType;

public class AsmIntelLexerTokens {
  public static final IElementType LINE_COMMENT = new AsmIntelTokenType("LINE_COMMENT");

  public static final IElementType OPERATOR = new AsmIntelTokenType("operator");
  public static final IElementType PAREN = new AsmIntelTokenType("PAREN");
  public static final IElementType COLON = new AsmIntelTokenType(":");
  public static final IElementType COMMA = new AsmIntelTokenType(",");

  public static final IElementType FLAG = new AsmIntelTokenType("FLAG");

  public static final IElementType HEX = new AsmIntelTokenType("hex");
  public static final IElementType INT = new AsmIntelTokenType("int");

  public static final IElementType IDENTIFIER = new AsmIntelTokenType("identifier");
  public static final IElementType PREPROCESSOR = new AsmIntelTokenType("PREPROCESSOR");
  public static final IElementType PSEUDO_INS = new AsmIntelTokenType("PSEUDO_INS");
  public static final IElementType INSTRUCTION = new AsmIntelTokenType("INSTRUCTION");
  public static final IElementType LABEL = new AsmIntelTokenType("LABEL");
  public static final IElementType PSEUDO_REG = new AsmIntelTokenType("PSEUDO_REG");
  public static final IElementType REGISTER = new AsmIntelTokenType("REGISTER");
  public static final IElementType STRING = new AsmIntelTokenType("STRING");

  public static final IElementType DIRECTIVE = new AsmIntelTokenType("DIRECTIVE");

  public static final TokenSet KEYWORDS = TokenSet.create(DIRECTIVE);
  public static final TokenSet NUMBERS = TokenSet.create(HEX, INT);
  public static final TokenSet REGISTERS = TokenSet.create(PSEUDO_REG, REGISTER);

  private AsmIntelLexerTokens() {}
}
