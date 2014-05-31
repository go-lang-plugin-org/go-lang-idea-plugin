package com.goide.spellchecker;

import com.goide.inspections.suppression.GoSuppressionUtil;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.psi.PsiElement;
import com.intellij.spellchecker.tokenizer.SuppressibleSpellcheckingStrategy;
import org.jetbrains.annotations.NotNull;

public class GoSpellcheckingStrategy extends SuppressibleSpellcheckingStrategy {
  @Override
  public boolean isSuppressedFor(@NotNull PsiElement element, @NotNull String name) {
    return GoSuppressionUtil.isSuppressedFor(element, name);
  }

  @NotNull
  @Override
  public SuppressQuickFix[] getSuppressActions(@NotNull PsiElement element, @NotNull String name) {
    return GoSuppressionUtil.getSuppressQuickFixes(name);
  }
}
