package com.goide.inspections;

import com.goide.codeInsight.imports.GoImportOptimizer;
import com.goide.psi.GoFile;
import com.goide.psi.GoImportSpec;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.MultiMap;

import java.util.Collection;
import java.util.List;

public class GoUnusedImportDeclaration extends GoInspectionBase {
  @Override
  protected void checkFile(PsiFile file, ProblemsHolder problemsHolder) {
    if (!(file instanceof GoFile)) return;
    MultiMap<String, PsiElement> importMap = ((GoFile)file).getImportMap();

    final List<PsiElement> importIdentifiersToDelete = GoImportOptimizer.findRedundantImportIdentifiers(importMap);
    for (PsiElement importIdentifier : importIdentifiersToDelete) {
      problemsHolder.registerProblem(importIdentifier, "Redundant alias", ProblemHighlightType.LIKE_UNUSED_SYMBOL);
    }

    Collection<GoImportSpec> duplicatedImportSpecs = GoImportOptimizer.findDuplicatedEntries(importMap);
    for (GoImportSpec duplicatedImportSpec : duplicatedImportSpecs) {
      problemsHolder.registerProblem(duplicatedImportSpec, "Redeclared import", ProblemHighlightType.GENERIC_ERROR);
    }

    GoImportOptimizer.filterUnusedImports(file, importMap);
    for (PsiElement importEntry : importMap.values()) {
      GoImportSpec spec = GoImportOptimizer.getImportSpec(importEntry);
      if (spec != null) {
        if (spec.getImportString().resolve() != null) {
          problemsHolder.registerProblem(spec, "Unused import", ProblemHighlightType.GENERIC_ERROR);
        }
      }
    }
  }
}
