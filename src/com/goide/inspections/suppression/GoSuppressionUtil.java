package com.goide.inspections.suppression;

import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.psi.GoStatement;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.codeInspection.SuppressionUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoSuppressionUtil {
  public static boolean isSuppressedFor(@Nullable final PsiElement element, @NotNull final String toolId) {
    if (element == null) {
      return true;
    }

    return SuppressionUtil.isSuppressedInStatement(element, toolId, GoStatement.class) ||
           SuppressionUtil.isSuppressedInStatement(element, toolId, GoFunctionOrMethodDeclaration.class);
  }

  @NotNull
  public static SuppressQuickFix[] getSuppressQuickFixes(@NotNull String inspectionShortName) {
    return new SuppressQuickFix[]{
      new GoSuppressInspectionFix("Suppress all inspections for function", GoFunctionOrMethodDeclaration.class),
      new GoSuppressInspectionFix(inspectionShortName, "Suppress for function", GoFunctionOrMethodDeclaration.class),
      new GoSuppressInspectionFix("Suppress all inspections for statement", GoStatement.class),
      new GoSuppressInspectionFix(inspectionShortName, "Suppress for statement", GoStatement.class)
    };
  }

  private GoSuppressionUtil() {
  }
}
