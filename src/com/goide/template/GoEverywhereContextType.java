package com.goide.template;

import com.goide.GoTypes;
import com.intellij.codeInsight.template.EverywhereContextType;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.NotNull;

public class GoEverywhereContextType extends GoLiveTemplateContextType {
  protected GoEverywhereContextType() {
    super("GO", "Go", EverywhereContextType.class);
  }

  @Override
  protected boolean isInContext(@NotNull PsiElement element) {
    return !(element instanceof PsiComment ||
             element instanceof LeafPsiElement && ((LeafPsiElement)element).getElementType() == GoTypes.STRING);
  }
}
