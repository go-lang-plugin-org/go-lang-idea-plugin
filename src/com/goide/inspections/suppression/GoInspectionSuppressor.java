package com.goide.inspections.suppression;

import com.goide.psi.GoCompositeElement;
import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.psi.GoImportDeclaration;
import com.goide.psi.GoStatement;
import com.intellij.codeInsight.daemon.impl.actions.AbstractBatchSuppressByNoInspectionCommentFix;
import com.intellij.codeInspection.InspectionSuppressor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.codeInspection.SuppressionUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoInspectionSuppressor implements InspectionSuppressor {
  @Override
  public boolean isSuppressedFor(@NotNull PsiElement element, String toolId) {
    return SuppressionUtil.isSuppressedInStatement(element, toolId, GoStatement.class) ||
           SuppressionUtil.isSuppressedInStatement(element, toolId, GoFunctionOrMethodDeclaration.class) ||
           SuppressionUtil.isSuppressedInStatement(element, toolId, GoImportDeclaration.class);
  }

  @Override
  public SuppressQuickFix[] getSuppressActions(PsiElement element, String toolShortName) {
    return new SuppressQuickFix[]{
      new GoSuppressInspectionFix("Suppress all inspections for function", GoFunctionOrMethodDeclaration.class),
      new GoSuppressInspectionFix(toolShortName, "Suppress for function", GoFunctionOrMethodDeclaration.class),
      new GoSuppressInspectionFix("Suppress all inspections for statement", GoStatement.class),
      new GoSuppressInspectionFix(toolShortName, "Suppress for statement", GoStatement.class),
      new GoSuppressInspectionFix("Suppress all inspections for import", GoImportDeclaration.class),
      new GoSuppressInspectionFix(toolShortName, "Suppress for import", GoImportDeclaration.class),
    };
  }

  public static class GoSuppressInspectionFix extends AbstractBatchSuppressByNoInspectionCommentFix {
    private final Class<? extends GoCompositeElement> myContainerClass;
  
    public GoSuppressInspectionFix(@NotNull String text, Class<? extends GoCompositeElement> containerClass) {
      super(SuppressionUtil.ALL, true);
      setText(text);
      myContainerClass = containerClass;
    }
  
    public GoSuppressInspectionFix(@NotNull String ID, @NotNull String text, Class<? extends GoCompositeElement> containerClass) {
      super(ID, false);
      setText(text);
      myContainerClass = containerClass;
    }
  
    @Override
    @Nullable
    public PsiElement getContainer(PsiElement context) {
      return PsiTreeUtil.getNonStrictParentOfType(context, myContainerClass);
    }
  }
}
