package com.goide.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiInvalidElementAccessException;
import org.jetbrains.annotations.NotNull;

abstract public class GoInspectionBase extends LocalInspectionTool {
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
}
