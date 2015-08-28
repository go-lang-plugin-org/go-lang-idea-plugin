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

package com.goide.inspections;

import com.goide.psi.GoFile;
import com.goide.psi.GoImportSpec;
import com.goide.runconfig.testing.GoTestFinder;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class GoSelfImportInspection extends GoInspectionBase {
  @Override
  protected void checkFile(@NotNull GoFile file, @NotNull ProblemsHolder problemsHolder) {
    if (GoTestFinder.getTestTargetPackage(file) != null) return;

    String fileImportPath = file.getImportPath();
    for (GoImportSpec importSpec : file.getImports()) {
      String path = importSpec.getPath();
      if (path.equals(fileImportPath) || path.equals(".")) {
        problemsHolder.registerProblem(importSpec, "Self import is not allowed", new GoSelfImportQuickFix());
      }
    }
  }

  public static class GoSelfImportQuickFix extends LocalQuickFixBase {
    protected GoSelfImportQuickFix() {
      super("Remove self import");
    }
    @Override
    public void applyFix(@NotNull final Project project, @NotNull ProblemDescriptor descriptor) {
      final PsiElement element = descriptor.getPsiElement();
      final PsiFile file = element != null ? element.getContainingFile() : null;
      if (!(element instanceof GoImportSpec && file instanceof GoFile)) return;
  
      WriteCommandAction.runWriteCommandAction(project, new Runnable() {
        @Override
        public void run() {
          ((GoFile)file).deleteImport((GoImportSpec)element);
        }
      });
    }
  }
}
