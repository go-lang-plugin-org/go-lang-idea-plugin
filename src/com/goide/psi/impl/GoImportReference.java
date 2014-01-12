package com.goide.psi.impl;

import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoImportReference extends GoReferenceBase {
  public GoImportReference(PsiElement element, TextRange range) {
    super(element, range);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    return resolvePackage(StringUtil.unquoteString(getElement().getText()));
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }

  @NotNull
  @Override
  public PsiElement getIdentifier() {
    return getElement();
  }
}
