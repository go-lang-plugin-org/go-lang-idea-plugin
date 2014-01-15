package com.goide.inspections;

import com.goide.inspections.suppression.GoSuppressionUtil;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class GoInspectionBase extends LocalInspectionTool implements BatchSuppressableTool {
  @Override
  public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
    ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);
    try {
      checkFile(file, problemsHolder);
    }
    catch (PsiInvalidElementAccessException ignored) {
    }
    return problemsHolder.getResultsArray();
  }

  protected abstract void checkFile(PsiFile file, ProblemsHolder problemsHolder);

  @NotNull
  @Override
  public SuppressQuickFix[] getBatchSuppressActions(@Nullable PsiElement element) {
    return GoSuppressionUtil.getSuppressQuickFixes(getShortName());
  }

  @Override
  public boolean isSuppressedFor(@NotNull PsiElement element) {
    return GoSuppressionUtil.isSuppressedFor(element, getShortName());
  }
}
