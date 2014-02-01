package com.goide.inspections.suppression;

import com.goide.psi.GoCompositeElement;
import com.intellij.codeInsight.daemon.impl.actions.AbstractBatchSuppressByNoInspectionCommentFix;
import com.intellij.codeInspection.SuppressionUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

public class GoSuppressInspectionFix extends AbstractBatchSuppressByNoInspectionCommentFix {
  private Class<? extends GoCompositeElement> myContainerClass;

  public GoSuppressInspectionFix(String text, Class<? extends GoCompositeElement> containerClass) {
    super(SuppressionUtil.ALL, true);
    setText(text);
    myContainerClass = containerClass;
  }

  public GoSuppressInspectionFix(String ID, String text, Class<? extends GoCompositeElement> containerClass) {
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
