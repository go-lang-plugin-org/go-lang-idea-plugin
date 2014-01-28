package com.goide.psi.impl;

import com.goide.psi.GoTypeSpec;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class GoTypeProcessor extends GoScopeProcessorBase {
  public GoTypeProcessor(String requestedName, PsiElement origin, boolean completion) {
    super(requestedName, origin, completion);
  }

  @Override
  protected boolean condition(@NotNull PsiElement element) {
    return !(element instanceof GoTypeSpec);
  }
}
