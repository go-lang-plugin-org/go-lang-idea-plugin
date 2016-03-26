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

package com.plan9.intel.lang.core;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.plan9.intel.lang.AsmIntelLanguage;
import com.plan9.intel.lang.core.lexer.AsmIntelLexer;
import com.plan9.intel.lang.core.lexer.AsmIntelTokenType;
import com.plan9.intel.lang.core.parser.AsmIntelParser;
import com.plan9.intel.lang.core.psi.AsmIntelFile;
import org.jetbrains.annotations.NotNull;

import static com.plan9.intel.lang.core.psi.AsmIntelTypes.*;

public class AsmIntelParserDefinition implements ParserDefinition {

  public static final IElementType LINE_COMMENT = new AsmIntelTokenType("LINE_COMMENT");

  private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);

  private static final TokenSet COMMENTS = TokenSet.create(LINE_COMMENT);
  public static final TokenSet KEYWORDS = TokenSet.create(TEXT);
  public static final TokenSet NUMBERS = TokenSet.create(HEX, INT);
  public static final TokenSet REGISTERS = TokenSet.create(PSEUDO_REG);

  public static final IFileElementType FILE = new IFileElementType(Language.findInstance(AsmIntelLanguage.class));

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new AsmIntelLexer();
  }

  @Override
  @NotNull
  public TokenSet getWhitespaceTokens() {
    return WHITE_SPACES;
  }

  @Override
  @NotNull
  public TokenSet getCommentTokens() {
    return COMMENTS;
  }

  @Override
  @NotNull
  public TokenSet getStringLiteralElements() {
    return TokenSet.EMPTY;
  }

  @Override
  @NotNull
  public PsiParser createParser(Project project) {
    return new AsmIntelParser();
  }

  @Override
  public IFileElementType getFileNodeType() {
    return FILE;
  }

  @Override
  public PsiFile createFile(FileViewProvider viewProvider) {
    return new AsmIntelFile(viewProvider);
  }

  @Override
  public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
    return SpaceRequirements.MAY;
  }

  @Override
  @NotNull
  public PsiElement createElement(ASTNode node) {
    return Factory.createElement(node);
  }
}
