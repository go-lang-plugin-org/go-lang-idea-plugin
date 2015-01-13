/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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
import org.jetbrains.annotations.Nullable;

public class GoUnusedImportDeclaration extends GoInspectionBase {
  @Nullable private final static LocalQuickFix OPTIMIZE_QUICK_FIX = new LocalQuickFixBase("Optimize imports") {
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
  protected void checkFile(@NotNull GoFile file, @NotNull ProblemsHolder problemsHolder) {
    MultiMap<String, GoImportSpec> importMap = file.getImportMap();

    for (PsiElement importIdentifier : GoImportOptimizer.findRedundantImportIdentifiers(importMap)) {
      problemsHolder.registerProblem(importIdentifier, "Redundant alias", ProblemHighlightType.LIKE_UNUSED_SYMBOL, OPTIMIZE_QUICK_FIX);
    }

    for (GoImportSpec duplicatedImportSpec : GoImportOptimizer.findDuplicatedEntries(importMap)) {
      problemsHolder.registerProblem(duplicatedImportSpec, "Redeclared import", ProblemHighlightType.GENERIC_ERROR, OPTIMIZE_QUICK_FIX);
    }

    GoImportOptimizer.filterUnusedImports(file, importMap);
    for (PsiElement importEntry : importMap.values()) {
      GoImportSpec spec = GoImportOptimizer.getImportSpec(importEntry);
      if (spec != null && !spec.isBlank()) {
        if (spec.getImportString().resolve() != null) {
          problemsHolder.registerProblem(spec, "Unused import", ProblemHighlightType.GENERIC_ERROR, OPTIMIZE_QUICK_FIX);
        }
      }
    }
  }
}
