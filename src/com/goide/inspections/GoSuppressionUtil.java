package com.goide.inspections;

import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoSuppressionUtil {
  private GoSuppressionUtil() {
  }

  public static boolean isSuppressedFor(@Nullable PsiElement element, @NotNull String toolId) {
    if (element == null) {
      return true;
    }

    // todo: implement
    return false;
  }

  @NotNull
  public static SuppressQuickFix[] getSuppressQuickFixes(@NotNull String inspectionShortName) {
    // todo: implement
    return new SuppressQuickFix[0];
  }
}
