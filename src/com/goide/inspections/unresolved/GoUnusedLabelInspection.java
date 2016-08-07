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

package com.goide.inspections.unresolved;

import com.goide.inspections.GoInspectionBase;
import com.goide.psi.GoLabelDefinition;
import com.goide.psi.GoLabeledStatement;
import com.goide.psi.GoStatement;
import com.goide.psi.GoVisitor;
import com.goide.quickfix.GoRenameToBlankQuickFix;
import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoUnusedLabelInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {

      @Override
      public void visitLabelDefinition(@NotNull GoLabelDefinition o) {
        super.visitLabelDefinition(o);
        if (o.isBlank()) return;
        if (ReferencesSearch.search(o, o.getUseScope()).findFirst() == null) {
          String name = o.getName();
          holder.registerProblem(o, "Unused label <code>#ref</code> #loc", ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                                 new GoRenameToBlankQuickFix(o), new GoDeleteLabelStatementQuickFix(name));
        }
      }
    };
  }

  private static class GoDeleteLabelStatementQuickFix extends LocalQuickFixBase {
    public GoDeleteLabelStatementQuickFix(@Nullable String labelName) {
      super("Delete label " + (labelName != null ? "'" + labelName + "'" : ""), "Delete label");
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      PsiElement element = descriptor.getPsiElement();
      if (element.isValid() && element instanceof GoLabelDefinition) {
        PsiElement parent = element.getParent();
        if (parent instanceof GoLabeledStatement) {
          GoStatement innerStatement = ((GoLabeledStatement)parent).getStatement();
          if (innerStatement != null) {
            parent.replace(innerStatement);
          }
        }
      }
    }
  }
}
