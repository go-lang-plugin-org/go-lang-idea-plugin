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

package com.goide.inspections.suppression;

import com.goide.psi.*;
import com.intellij.codeInsight.daemon.impl.actions.AbstractBatchSuppressByNoInspectionCommentFix;
import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.codeInspection.SuppressionUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.ElementDescriptionUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usageView.UsageViewTypeLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

public class GoInspectionSuppressor implements InspectionSuppressor {
  @Override
  public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String toolId) {
    GoTopLevelDeclaration topLevelDeclaration = PsiTreeUtil.getTopmostParentOfType(element, GoTopLevelDeclaration.class);
    if (topLevelDeclaration != null && isSuppressedInStatement(toolId, topLevelDeclaration)) {
      return true;
    }
    GoImportDeclaration importDeclaration = PsiTreeUtil.getNonStrictParentOfType(element, GoImportDeclaration.class);
    if (importDeclaration != null && importDeclaration.getPrevSibling() == null
        && isSuppressedInStatement(element, toolId, GoImportList.class)) {
      return true;
    }
    return isSuppressedInStatement(element, toolId, GoPackageClause.class, GoStatement.class, GoCommClause.class, GoCaseClause.class,
                                   GoTopLevelDeclaration.class, GoImportDeclaration.class);
  }

  @NotNull
  @Override
  public SuppressQuickFix[] getSuppressActions(PsiElement element, @NotNull String toolId) {
    return new SuppressQuickFix[]{
      new GoSuppressInspectionFix("comm case", GoCommClause.class, false),
      new GoSuppressInspectionFix(toolId, "comm case", GoCommClause.class, false),
      new GoSuppressInspectionFix("case", GoCaseClause.class, false),
      new GoSuppressInspectionFix(toolId, "case", GoCaseClause.class, false),
      new GoSuppressInspectionFix("declaration", GoTopLevelDeclaration.class, true),
      new GoSuppressInspectionFix(toolId, "declaration", GoTopLevelDeclaration.class, true),
      new GoSuppressForStatementFix(),
      new GoSuppressForStatementFix(toolId),
      new GoSuppressInspectionFix("import", GoImportDeclaration.class, false),
      new GoSuppressInspectionFix(toolId, "import", GoImportDeclaration.class, false),
      new GoSuppressInspectionFix("package statement", GoPackageClause.class, false),
      new GoSuppressInspectionFix(toolId, "package statement", GoPackageClause.class, false),
    };
  }

  private static class GoSuppressForStatementFix extends GoSuppressInspectionFix {
    public GoSuppressForStatementFix() {
      super("statement", GoStatement.class, false);
    }

    public GoSuppressForStatementFix(@NotNull String ID) {
      super(ID, "statement", GoStatement.class, false);
    }

    @Nullable
    @Override
    public PsiElement getContainer(PsiElement context) {
      GoStatement statement = PsiTreeUtil.getNonStrictParentOfType(context, GoStatement.class);
      if (statement != null && statement.getParent() instanceof GoCommCase) {
        return PsiTreeUtil.getParentOfType(statement, GoStatement.class);
      }
      return statement;
    }
  }

  public static class GoSuppressInspectionFix extends AbstractBatchSuppressByNoInspectionCommentFix {
    private final Class<? extends GoCompositeElement> myContainerClass;
    private final String myBaseText;
    private final boolean myTopMost;

    public GoSuppressInspectionFix(@NotNull String elementDescription,
                                   Class<? extends GoCompositeElement> containerClass,
                                   boolean topMost) {
      super(SuppressionUtil.ALL, true);
      myBaseText = "Suppress all inspections for ";
      setText(myBaseText + elementDescription);
      myContainerClass = containerClass;
      myTopMost = topMost;
    }

    public GoSuppressInspectionFix(@NotNull String ID,
                                   @NotNull String elementDescription,
                                   Class<? extends GoCompositeElement> containerClass,
                                   boolean topMost) {
      super(ID, false);
      myBaseText = "Suppress for ";
      setText(myBaseText + elementDescription);
      myTopMost = topMost;
      myContainerClass = containerClass;
    }

    @Override
    @Nullable
    public PsiElement getContainer(PsiElement context) {
      PsiElement container;
      if (myTopMost) {
        container = PsiTreeUtil.getTopmostParentOfType(context, myContainerClass);
        if (container == null && myContainerClass.isInstance(context)) {
          container = context;
        }
      }
      else {
        container = PsiTreeUtil.getNonStrictParentOfType(context, myContainerClass);
      }
      if (container != null) {
        String description = ElementDescriptionUtil.getElementDescription(container, UsageViewTypeLocation.INSTANCE);
        if (StringUtil.isNotEmpty(description)) {
          setText(myBaseText + description);
        }
      }
      return container;
    }
  }

  private static boolean isSuppressedInStatement(@NotNull PsiElement place,
                                                 @NotNull String toolId,
                                                 @NotNull Class<? extends PsiElement>... statementClasses) {
    PsiElement statement = PsiTreeUtil.getNonStrictParentOfType(place, statementClasses);
    while (statement != null) {
      if (isSuppressedInStatement(toolId, statement)) {
        return true;
      }
      statement = PsiTreeUtil.getParentOfType(statement, statementClasses);
    }
    return false;
  }

  private static boolean isSuppressedInStatement(@NotNull String toolId, @Nullable PsiElement statement) {
    if (statement != null) {
      PsiElement prev = PsiTreeUtil.skipSiblingsBackward(statement, PsiWhiteSpace.class);
      if (prev instanceof PsiComment) {
        String text = prev.getText();
        Matcher matcher = SuppressionUtil.SUPPRESS_IN_LINE_COMMENT_PATTERN.matcher(text);
        return matcher.matches() && SuppressionUtil.isInspectionToolIdMentioned(matcher.group(1), toolId);
      }
    }
    return false;
  }
}
