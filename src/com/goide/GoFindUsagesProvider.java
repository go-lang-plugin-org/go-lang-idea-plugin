package com.goide;

import com.goide.psi.GoNamedElement;
import com.intellij.lang.findUsages.EmptyFindUsagesProvider;
import com.intellij.psi.ElementDescriptionUtil;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewNodeTextLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import org.jetbrains.annotations.NotNull;

public class GoFindUsagesProvider extends EmptyFindUsagesProvider {
  @Override
  public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
    return psiElement instanceof GoNamedElement;
  }

  @NotNull
  @Override
  public String getType(@NotNull PsiElement element) {
    return ElementDescriptionUtil.getElementDescription(element, UsageViewTypeLocation.INSTANCE);
  }

  @NotNull
  @Override
  public String getDescriptiveName(@NotNull PsiElement element) {
    return ElementDescriptionUtil.getElementDescription(element, UsageViewLongNameLocation.INSTANCE);
  }

  @NotNull
  @Override
  public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
    return ElementDescriptionUtil.getElementDescription(element, UsageViewNodeTextLocation.INSTANCE);
  }
}