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

package com.goide.inspections.unresolved;

import com.goide.inspections.GoInspectionBase;
import com.goide.psi.*;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING;

public class GoAssignmentToConstantInspection extends GoInspectionBase {
  @Override
  protected void checkFile(@NotNull GoFile file, @NotNull final ProblemsHolder problemsHolder) {
    file.accept(new GoRecursiveVisitor() {
      @Override
      public void visitAssignmentStatement(@NotNull GoAssignmentStatement o) {
        int offset = o.getAssignOp().getTextOffset();
        List<GoExpression> list = o.getExpressionList();
        for (GoExpression expression : list) {
          if (expression.getTextOffset() < offset) checkExpression(expression);
        }
        super.visitAssignmentStatement(o);
      }

      private void checkExpression(GoExpression expression) {
        if (expression instanceof GoReferenceExpression) {
          PsiElement resolve = ((GoReferenceExpression)expression).getReference().resolve();
          if (resolve instanceof GoConstDefinition) {
            String name = ((GoReferenceExpression)expression).getIdentifier().getText();
            problemsHolder.registerProblem(expression, "Cannot assign to constant '" + name + "'", GENERIC_ERROR_OR_WARNING);
          }
        }
      }
    });
  }
}