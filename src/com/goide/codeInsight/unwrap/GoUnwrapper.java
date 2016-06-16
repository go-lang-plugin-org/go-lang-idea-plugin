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

package com.goide.codeInsight.unwrap;

import com.goide.psi.GoBlock;
import com.goide.psi.GoStatement;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.codeInsight.unwrap.AbstractUnwrapper;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.codeStyle.CodeEditUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GoUnwrapper extends AbstractUnwrapper<GoUnwrapper.Context> {
  protected GoUnwrapper(String description) {
    super(description);
  }

  @Override
  protected Context createContext() {
    return new Context();
  }

  protected static class Context extends AbstractUnwrapper.AbstractContext {
    public void extractNewLine(PsiElement from) {
      PsiElement newLine = GoElementFactory.createNewLine(from.getProject());
      if (myIsEffective) {
        newLine = from.getParent().addBefore(newLine, from);
      }
      if (newLine != null) {
        addElementToExtract(newLine);
      }
    }

    public void extractFromBlock(@Nullable GoBlock block, @NotNull PsiElement from) {
      if (block != null) {
        for (GoStatement statement : block.getStatementList()) {
          extractElement(statement, from);
        }
      }
    }

    @Override
    public void addElementToExtract(PsiElement e) {
      super.addElementToExtract(e);
      CodeEditUtil.markToReformat(e.getNode(), true);
    }

    @Override
    protected boolean isWhiteSpace(PsiElement element) {
      return element instanceof PsiWhiteSpace;
    }
  }
}
