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

package com.goide.editor.smart;

import com.goide.inspections.GoDeferGoInspection;
import com.goide.psi.*;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.lang.SmartEnterProcessorWithFixers;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoSmartEnterProcessor extends SmartEnterProcessorWithFixers {
  public GoSmartEnterProcessor() {
    addFixers(new IfFixer(), new ForFixer(), new FuncFixer(), new GoDeferExpressionFixer());
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
    element = PsiTreeUtil.getParentOfType(element, GoStatement.class, GoFunctionOrMethodDeclaration.class, GoFunctionLit.class);
    if (element != null) {
      result.add(element);
      PsiElement parent = element.getParent();
      if (parent instanceof GoStatement) {
        result.add(parent);
      }
    }
  }

  private static class GoDeferExpressionFixer extends Fixer<SmartEnterProcessorWithFixers> {
    @Override
    public void apply(@NotNull Editor editor, @NotNull SmartEnterProcessorWithFixers processor, @NotNull PsiElement element)
      throws IncorrectOperationException {
      Project project = element.getProject();
      if (element instanceof GoGoStatement) {
        PsiElement expr = GoDeferGoInspection.GoAddParensQuickFix.addParensIfNeeded(project, ((GoGoStatement)element).getExpression());
        if (expr != null) {
          element.replace(GoElementFactory.createGoStatement(project, expr.getText()));
        }
      }
      else if (element instanceof GoDeferStatement) {
        PsiElement expr = GoDeferGoInspection.GoAddParensQuickFix.addParensIfNeeded(project, ((GoDeferStatement)element).getExpression());
        if (expr != null) {
          element.replace(GoElementFactory.createDeferStatement(project, expr.getText()));
        }
      }
    }
  }

  private static class FuncFixer extends Fixer<SmartEnterProcessorWithFixers> {
    @Override
    public void apply(@NotNull Editor editor, @NotNull SmartEnterProcessorWithFixers processor, @NotNull PsiElement element)
      throws IncorrectOperationException {
      if (element instanceof GoFunctionOrMethodDeclaration && ((GoFunctionOrMethodDeclaration)element).getBlock() == null) {
        element.add(GoElementFactory.createBlock(element.getProject()));
      }
      if (element instanceof GoFunctionLit && ((GoFunctionLit)element).getBlock() == null) {
        element.add(GoElementFactory.createBlock(element.getProject()));
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

  public static class PlainEnterProcessor extends FixEnterProcessor {
    @Nullable
    private static GoBlock findBlock(@Nullable PsiElement element) {
      element = PsiTreeUtil.getNonStrictParentOfType(element, GoStatement.class, GoBlock.class, GoFunctionOrMethodDeclaration.class,
                                                     GoFunctionLit.class);
      if (element instanceof GoSimpleStatement && element.getParent() instanceof GoStatement) {
        element = element.getParent();
      }
      if (element instanceof GoIfStatement) return ((GoIfStatement)element).getBlock();
      if (element instanceof GoForStatement) return ((GoForStatement)element).getBlock();
      if (element instanceof GoBlock) return (GoBlock)element;
      if (element instanceof GoFunctionOrMethodDeclaration) return ((GoFunctionOrMethodDeclaration)element).getBlock();
      if (element instanceof GoFunctionLit) return ((GoFunctionLit)element).getBlock();
      return null;
    }

    @Override
    public boolean doEnter(PsiElement psiElement, PsiFile file, @NotNull Editor editor, boolean modified) {
      GoBlock block = findBlock(psiElement);
      if (block != null) {
        editor.getCaretModel().moveToOffset(block.getLbrace().getTextRange().getEndOffset());
      }
      plainEnter(editor);
      return true;
    }
  }
}
