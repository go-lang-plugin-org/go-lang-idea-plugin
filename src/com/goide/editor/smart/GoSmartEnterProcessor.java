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

package com.goide.editor.smart;

import com.goide.psi.*;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.lang.SmartEnterProcessorWithFixers;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoSmartEnterProcessor extends SmartEnterProcessorWithFixers {
  public GoSmartEnterProcessor() {
    addFixers(new IfFixer(), new ForFixer());
    addEnterProcessors(new PlainEnterProcessor());
  }

  private static void addBlockIfNeeded(@NotNull GoStatement element) {
    if (element.getBlock() == null) element.add(GoElementFactory.createBlock(element.getProject()));
  }

  @Override
  public boolean doNotStepInto(PsiElement element) {
    return element instanceof GoBlock;
  }

  @Override
  protected void collectAdditionalElements(@NotNull PsiElement element, @NotNull List<PsiElement> result) {
    GoStatement statement = PsiTreeUtil.getParentOfType(element, GoStatement.class);
    if (statement != null) {
      result.add(statement);
      PsiElement parent = statement.getParent();
      if (parent instanceof GoStatement) {
        result.add(parent);
      }
    }
  }

  private static class IfFixer extends Fixer<SmartEnterProcessorWithFixers> {
    @Override
    public void apply(@NotNull Editor editor, @NotNull SmartEnterProcessorWithFixers processor, @NotNull PsiElement element)
      throws IncorrectOperationException {
      if (element instanceof GoIfStatement) addBlockIfNeeded((GoIfStatement)element);
    }
  }

  private static class ForFixer extends Fixer<SmartEnterProcessorWithFixers> {
    @Override
    public void apply(@NotNull Editor editor, @NotNull SmartEnterProcessorWithFixers processor, @NotNull PsiElement element)
      throws IncorrectOperationException {
      if (element instanceof GoForStatement) addBlockIfNeeded((GoStatement)element);
    }
  }

  private static class PlainEnterProcessor extends FixEnterProcessor {
    @Nullable
    private static GoBlock findBlock(@Nullable PsiElement element) {
      GoStatement statement = PsiTreeUtil.getParentOfType(element, GoStatement.class);
      if (statement instanceof GoSimpleStatement && statement.getParent() instanceof GoStatement) {
        statement = (GoStatement)statement.getParent();
      }
      if (statement instanceof GoIfStatement) return statement.getBlock();
      if (statement instanceof GoForStatement) return statement.getBlock();
      if (statement instanceof GoBlock) return (GoBlock)statement;
      return null;
    }

    @Override
    public boolean doEnter(PsiElement psiElement, PsiFile file, @NotNull Editor editor, boolean modified) {
      GoBlock block = findBlock(psiElement);
      if (block != null) {
        int offset = block.getTextOffset() + 1;
        editor.getCaretModel().moveToOffset(offset);
      }
      plainEnter(editor);
      return true;
    }
  }
}
