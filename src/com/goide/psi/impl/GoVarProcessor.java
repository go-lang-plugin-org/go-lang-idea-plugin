package com.goide.psi.impl;

import com.goide.psi.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoVarProcessor extends GoScopeProcessorBase {
  public GoVarProcessor(String requestedName, PsiElement origin, boolean completion) {
    super(requestedName, origin, completion);
  }

  @Override
  protected boolean add(@NotNull GoNamedElement o) {
    boolean add = super.add(o);
    return PsiTreeUtil.getParentOfType(o, GoShortVarDeclaration.class) == null && add;
  }

  @NotNull
  @Override
  public List<GoNamedElement> getVariants() {
    return ContainerUtil.reverse(myResult);
  }

  protected boolean condition(@NotNull PsiElement psiElement) {
    return !(psiElement instanceof GoVarDefinition) &&
           !(psiElement instanceof GoParamDefinition) &&
           !(psiElement instanceof GoReceiver) &&
           !(psiElement instanceof GoFieldDefinition) &&
           !(psiElement instanceof GoAnonymousFieldDefinition) &&
           !(psiElement instanceof GoConstDefinition);
  }
}