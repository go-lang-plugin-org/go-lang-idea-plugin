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

import com.goide.psi.GoAssignmentStatement;
import com.goide.psi.GoExpression;
import com.goide.psi.GoRangeClause;
import com.goide.psi.GoRecvStatement;
import com.goide.psi.impl.GoElementFactory;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class GoReplaceAssignmentWithDeclarationQuickFix extends LocalQuickFixOnPsiElement {
  public static final String QUICK_FIX_NAME = "Replace with ':='";

  public GoReplaceAssignmentWithDeclarationQuickFix(@NotNull PsiElement element) {
    super(element);
  }

  @NotNull
  @Override
  public String getText() {
    return QUICK_FIX_NAME;
  }

  @Override
  public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
    if (startElement instanceof GoAssignmentStatement) {
      GoAssignmentStatement statement = (GoAssignmentStatement)startElement;
      String leftSide = statement.getLeftHandExprList().getText();
      String rightSide = GoPsiImplUtil.joinPsiElementText(statement.getExpressionList());
      statement.replace(GoElementFactory.createShortVarDeclarationStatement(project, leftSide, rightSide));
    }
    else if (startElement instanceof GoRangeClause) {
      GoRangeClause rangeClause = (GoRangeClause)startElement;
      String leftSide = GoPsiImplUtil.joinPsiElementText(rangeClause.getLeftExpressionsList());
      GoExpression rangeExpression = rangeClause.getRangeExpression();
      String rightSide = rangeExpression != null ? rangeExpression.getText() : "";
      rangeClause.replace(GoElementFactory.createRangeClause(project, leftSide, rightSide));
    }
    else if (startElement instanceof GoRecvStatement) {
      GoRecvStatement recvStatement = (GoRecvStatement)startElement;
      String leftSide = GoPsiImplUtil.joinPsiElementText(recvStatement.getLeftExpressionsList());
      GoExpression recvExpression = recvStatement.getRecvExpression();
      String rightSide = recvExpression != null ? recvExpression.getText() : "";
      recvStatement.replace(GoElementFactory.createRecvStatement(project, leftSide, rightSide));
    }
  }

  @Nls
  @NotNull
  @Override
  public String getFamilyName() {
    return QUICK_FIX_NAME;
  }
}
