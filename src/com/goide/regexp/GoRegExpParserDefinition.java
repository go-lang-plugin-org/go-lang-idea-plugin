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

package com.goide.regexp;

import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import org.intellij.lang.regexp.*;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class GoRegExpParserDefinition extends RegExpParserDefinition {
  private static final IFileElementType GO_REGEXP_FILE = new IFileElementType("GO_REGEXP_FILE", GoRegExpLanguage.INSTANCE);
  private final EnumSet<RegExpCapability> CAPABILITIES = EnumSet.of(RegExpCapability.UNICODE_CATEGORY_SHORTHAND,
                                                                    RegExpCapability.CARET_NEGATED_PROPERTIES,
                                                                    RegExpCapability.NESTED_CHARACTER_CLASSES,
                                                                    RegExpCapability.OCTAL_NO_LEADING_ZERO,
                                                                    RegExpCapability.POSIX_BRACKET_EXPRESSIONS);

  @Override
  @NotNull
  public Lexer createLexer(Project project) {
    return new RegExpLexer(CAPABILITIES);
  }

  @Override
  public PsiParser createParser(Project project) {
    return new RegExpParser(CAPABILITIES);
  }

  @Override
  public IFileElementType getFileNodeType() {
    return GO_REGEXP_FILE;
  }

  @Override
  public PsiFile createFile(FileViewProvider viewProvider) {
    return new RegExpFile(viewProvider, GoRegExpLanguage.INSTANCE);
  }
}
