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

package com.goide.inspections;

import com.goide.psi.GoAssignmentStatement;
import com.goide.psi.GoIfStatement;
import com.goide.psi.GoShortVarDeclaration;
import com.goide.psi.GoVisitor;
import com.goide.psi.impl.GoElementFactory;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import static com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING;

public class GoUsedAsValueInCondition extends GoInspectionBase {
  public static final String QUICK_FIX_NAME = "Convert to '==''";

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitAssignmentStatement(@NotNull GoAssignmentStatement o) {
        if (o.getParent() != null && o.getParent() instanceof GoIfStatement && ((GoIfStatement)o.getParent()).getExpression() == null) {
          String left = GoPsiImplUtil.joinPsiElementText(o.getLeftHandExprList().getExpressionList());
          String right = GoPsiImplUtil.joinPsiElementText(o.getExpressionList());
          holder.registerProblem(o, left + " = " + right + " used as value", GENERIC_ERROR_OR_WARNING, 
                                 new GoAssignmentToComparisonQuickFix());
        }
      }

      @Override
      public void visitShortVarDeclaration(@NotNull GoShortVarDeclaration o) {
        PsiElement parent = o.getParent();
        if (parent != null) {
          PsiElement gradParent = parent.getParent();
          if (gradParent instanceof GoIfStatement && ((GoIfStatement)gradParent).getExpression() == null) {
            String left = GoPsiImplUtil.joinPsiElementText(o.getVarDefinitionList());
            String right = GoPsiImplUtil.joinPsiElementText(o.getRightExpressionsList());
            holder.registerProblem(o, left + " := " + right + " used as value", GENERIC_ERROR_OR_WARNING,
                                   new GoAssignmentToComparisonQuickFix());
          }
        }
      }
    };
  }

  private static class GoAssignmentToComparisonQuickFix extends LocalQuickFixBase {
    private GoAssignmentToComparisonQuickFix() {
      super(QUICK_FIX_NAME);
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      PsiElement element = descriptor.getPsiElement();
      if (element instanceof GoAssignmentStatement) {
        String left = GoPsiImplUtil.joinPsiElementText(((GoAssignmentStatement)element).getLeftHandExprList().getExpressionList());
        String right = GoPsiImplUtil.joinPsiElementText(((GoAssignmentStatement)element).getExpressionList());
        element.replace(GoElementFactory.createExpression(project, left + " == " + right));
      }
      else if (element instanceof GoShortVarDeclaration) {
        String left = GoPsiImplUtil.joinPsiElementText(((GoShortVarDeclaration)element).getVarDefinitionList());
        String right = GoPsiImplUtil.joinPsiElementText(((GoShortVarDeclaration)element).getRightExpressionsList());
        element.replace(GoElementFactory.createComparison(project, left + " == " + right));
      }
    }
  }
}
