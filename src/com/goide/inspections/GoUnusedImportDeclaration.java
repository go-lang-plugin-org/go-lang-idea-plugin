package com.goide.inspections;

import com.goide.codeInsight.imports.GoImportOptimizer;
import com.goide.psi.GoFile;
import com.goide.psi.GoImportSpec;
import com.intellij.codeInspection.*;
import com.intellij.lang.ImportOptimizer;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;

public class GoUnusedImportDeclaration extends GoInspectionBase {
  private final static LocalQuickFix OPTIMIZE_QUICK_FIX = new LocalQuickFixBase("Optimize imports") {
    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      PsiElement element = descriptor.getPsiElement();
      if (element == null) {
        return;
      }
      final PsiFile file = element.getContainingFile();
      ImportOptimizer optimizer = new GoImportOptimizer();
      final Runnable runnable = optimizer.processFile(file);
      new WriteCommandAction.Simple(project, getFamilyName(), file) {
        @Override
        protected void run() throws Throwable {
          runnable.run();
        }
      }.execute();
    }
  };

  @Override
  protected void checkFile(PsiFile file, ProblemsHolder problemsHolder) {
    if (!(file instanceof GoFile)) return;
    MultiMap<String, PsiElement> importMap = ((GoFile)file).getImportMap();

    for (PsiElement importIdentifier : GoImportOptimizer.findRedundantImportIdentifiers(importMap)) {
      problemsHolder.registerProblem(importIdentifier, "Redundant alias", ProblemHighlightType.LIKE_UNUSED_SYMBOL, OPTIMIZE_QUICK_FIX);
    }

    for (GoImportSpec duplicatedImportSpec : GoImportOptimizer.findDuplicatedEntries(importMap)) {
      problemsHolder.registerProblem(duplicatedImportSpec, "Redeclared import", ProblemHighlightType.GENERIC_ERROR, OPTIMIZE_QUICK_FIX);
    }

    GoImportOptimizer.filterUnusedImports(file, importMap);
    for (PsiElement importEntry : importMap.values()) {
      GoImportSpec spec = GoImportOptimizer.getImportSpec(importEntry);
      if (spec != null) {
        if (spec.getImportString().resolve() != null) {
          problemsHolder.registerProblem(spec, "Unused import", ProblemHighlightType.GENERIC_ERROR, OPTIMIZE_QUICK_FIX);
        }
      }
    }
  }
}
