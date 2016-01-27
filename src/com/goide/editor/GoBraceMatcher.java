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

package com.goide.editor;

import com.goide.GoParserDefinition;
import com.goide.GoTypes;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoBraceMatcher implements PairedBraceMatcher {
  private static final BracePair[] PAIRS = new BracePair[]{
    new BracePair(GoTypes.LBRACE, GoTypes.RBRACE, true),
    new BracePair(GoTypes.LPAREN, GoTypes.RPAREN, false),
    new BracePair(GoTypes.LBRACK, GoTypes.RBRACK, false),
  };

  @NotNull
  @Override
  public BracePair[] getPairs() {
    return PAIRS;
  }

  @Override
  public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType type) {
    return GoParserDefinition.COMMENTS.contains(type)
           || GoParserDefinition.WHITESPACES.contains(type)
           || type == GoTypes.SEMICOLON
           || type == GoTypes.COMMA
           || type == GoTypes.RPAREN
           || type == GoTypes.RBRACK
           || type == GoTypes.RBRACE
           || type == GoTypes.LBRACE
           || null == type;
  }

  @Override
  public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
    return openingBraceOffset;
  }
}
