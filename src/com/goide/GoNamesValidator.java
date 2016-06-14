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

package com.goide;

import com.goide.lexer.GoLexer;
import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoNamesValidator implements NamesValidator {
  @Override
  public boolean isKeyword(@NotNull String name, Project project) {
    return GoParserDefinition.KEYWORDS.contains(getLexerType(name));
  }

  @Override
  public boolean isIdentifier(@NotNull String name, Project project) {
    return getLexerType(name) == GoTypes.IDENTIFIER;
  }

  @Nullable
  private static IElementType getLexerType(@NotNull String text) {
    GoLexer lexer = new GoLexer();
    lexer.start(text);
    return lexer.getTokenEnd() == text.length() ? lexer.getTokenType() : null;
  }
}
