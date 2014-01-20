package com.goide.template;

import com.intellij.codeInsight.template.EverywhereContextType;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class GoLiveTemplateGenericContextType extends GoLiveTemplateContextType {
  protected GoLiveTemplateGenericContextType() {
    super("GO", "Go", EverywhereContextType.class);
  }

  @Override
  protected boolean isInContext(@NotNull PsiElement element) {
    return true;
  }
}
