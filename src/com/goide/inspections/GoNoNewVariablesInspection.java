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

import com.goide.psi.*;
import com.goide.psi.impl.GoElementFactory;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoNoNewVariablesInspection extends GoInspectionBase {
  public static final String QUICK_FIX_NAME = "Replace with '='";

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitShortVarDeclaration(@NotNull GoShortVarDeclaration o) {
        visitVarDefinitionList(o, o.getVarDefinitionList());
      }

      @Override
      public void visitRecvStatement(@NotNull GoRecvStatement o) {
        visitVarDefinitionList(o, o.getVarDefinitionList());
      }

      @Override
      public void visitRangeClause(@NotNull GoRangeClause o) {
        visitVarDefinitionList(o, o.getVarDefinitionList());
      }

      private void visitVarDefinitionList(@NotNull PsiElement o, @NotNull List<GoVarDefinition> list) {
        GoVarDefinition first = ContainerUtil.getFirstItem(list);
        GoVarDefinition last = ContainerUtil.getLastItem(list);
        if (first == null || last == null) return;
        if (hasNonNewVariables(list)) {
          TextRange textRange = TextRange.create(first.getStartOffsetInParent(), last.getStartOffsetInParent() + last.getTextLength());
          holder.registerProblem(o, textRange, "No new variables on left side of :=", new MyLocalQuickFixBase());
        }
      }
    };
  }

  public static boolean hasNonNewVariables(@NotNull List<GoVarDefinition> list) {
    if (list.isEmpty()) return false;
    for (GoVarDefinition def : list) {
      if (def.isBlank()) continue;
      PsiReference reference = def.getReference();
      if (reference == null || reference.resolve() == null) return false;
    }
    return true;
  }

  public static void replaceWithAssignment(@NotNull Project project, @NotNull PsiElement element) {
    if (element instanceof GoShortVarDeclaration) {
      PsiElement parent = element.getParent();
      if (parent instanceof GoSimpleStatement) {
        String left = GoPsiImplUtil.joinPsiElementText(((GoShortVarDeclaration)element).getVarDefinitionList());
        String right = GoPsiImplUtil.joinPsiElementText(((GoShortVarDeclaration)element).getRightExpressionsList());
        parent.replace(GoElementFactory.createAssignmentStatement(project, left, right));
      }
    }
    else if (element instanceof GoRangeClause) {
      String left = GoPsiImplUtil.joinPsiElementText(((GoRangeClause)element).getVarDefinitionList());
      GoExpression rangeExpression = ((GoRangeClause)element).getRangeExpression();
      String right = rangeExpression != null ? rangeExpression.getText() : "";
      element.replace(GoElementFactory.createRangeClauseAssignment(project, left, right));
    }
    else if (element instanceof GoRecvStatement) {
      String left = GoPsiImplUtil.joinPsiElementText(((GoRecvStatement)element).getVarDefinitionList());
      GoExpression recvExpression = ((GoRecvStatement)element).getRecvExpression();
      String right = recvExpression != null ? recvExpression.getText() : "";
      element.replace(GoElementFactory.createRecvStatementAssignment(project, left, right));
    }
  }

  private static class MyLocalQuickFixBase extends LocalQuickFixBase {
    public MyLocalQuickFixBase() {
      super(QUICK_FIX_NAME);
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      PsiElement element = descriptor.getStartElement();
      if (element.isValid()) {
        replaceWithAssignment(project, element);
      }
    }
  }
}
