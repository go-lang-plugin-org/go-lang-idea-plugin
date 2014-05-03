package com.goide.editor.smart;

import com.goide.psi.*;
import com.goide.psi.impl.GoElementFactory;
import com.intellij.lang.SmartEnterProcessorWithFixers;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoSmartEnterProcessor extends SmartEnterProcessorWithFixers {
  public GoSmartEnterProcessor() {
    addFixers(new IfFixer(), new ForFixer());
  }

  @Override
  public boolean doNotStepInto(PsiElement element) {
    return element instanceof GoBlock;
  }

  @Override
  protected void collectAdditionalElements(@NotNull PsiElement element, @NotNull final List<PsiElement> result) {
    GoStatement statement = PsiTreeUtil.getParentOfType(element, GoStatement.class);
    if (statement instanceof GoSimpleStatement) statement = PsiTreeUtil.getParentOfType(statement, GoStatement.class);
    if (statement == null) return;
    result.add(statement);
  }

  private static class IfFixer extends Fixer<SmartEnterProcessorWithFixers> {
    @Override
    public void apply(@NotNull Editor editor, @NotNull SmartEnterProcessorWithFixers processor, @NotNull PsiElement element)
      throws IncorrectOperationException {
      if (element instanceof GoIfStatement) {
        if (((GoIfStatement)element).getBlock() == null) {
          element.add(GoElementFactory.createBlock(element.getProject()));
        }
      }
    }
  }

  private static class ForFixer extends Fixer<SmartEnterProcessorWithFixers> {
    @Override
    public void apply(@NotNull Editor editor, @NotNull SmartEnterProcessorWithFixers processor, @NotNull PsiElement element)
      throws IncorrectOperationException {
      if (element instanceof GoForStatement) {
        if (((GoForStatement)element).getBlock() == null) {
          element.add(GoElementFactory.createBlock(element.getProject()));
        }
      }
    }
  }
}
