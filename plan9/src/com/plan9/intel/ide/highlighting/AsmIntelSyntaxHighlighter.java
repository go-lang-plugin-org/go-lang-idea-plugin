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

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.plan9.intel.ide.highlighting.AsmIntelSyntaxHighlightingColors.*;

public class AsmIntelSyntaxHighlighter extends SyntaxHighlighterBase {

  private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = ContainerUtil.newHashMap();

  static {
    fillMap(ATTRIBUTES, LINE_COMMENT, AsmIntelLexerTokens.LINE_COMMENT);
    fillMap(ATTRIBUTES, INSTRUCTION, AsmIntelLexerTokens.INSTRUCTION);
    fillMap(ATTRIBUTES, PSEUDO_INSTRUCTION, AsmIntelLexerTokens.PSEUDO_INS);
    fillMap(ATTRIBUTES, STRING, AsmIntelLexerTokens.STRING);
    fillMap(ATTRIBUTES, LABEL, AsmIntelLexerTokens.LABEL);
    fillMap(ATTRIBUTES, FLAG, AsmIntelLexerTokens.FLAG);
    fillMap(ATTRIBUTES, OPERATOR, AsmIntelLexerTokens.OPERATOR);
    fillMap(ATTRIBUTES, PARENTHESIS, AsmIntelLexerTokens.PAREN);
    fillMap(ATTRIBUTES, IDENTIFIER, AsmIntelLexerTokens.IDENTIFIER);
    fillMap(ATTRIBUTES, PREPROCESSOR, AsmIntelLexerTokens.PREPROCESSOR);

    fillMap(ATTRIBUTES, AsmIntelLexerTokens.KEYWORDS, KEYWORD);
    fillMap(ATTRIBUTES, AsmIntelLexerTokens.NUMBERS, NUMBER);
    fillMap(ATTRIBUTES, AsmIntelLexerTokens.REGISTERS, REGISTER);
  }

  @NotNull
  @Override
  public Lexer getHighlightingLexer() {
    return new AsmIntelHighlightingLexer();
  }

  @Override
  @NotNull
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    return pack(ATTRIBUTES.get(tokenType));
  }
}
