package com.goide.psi.impl;

import com.goide.psi.*;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class GoVarProcessor extends GoScopeProcessorBase {
  public GoVarProcessor(String requestedName, PsiElement origin, boolean completion) {
    super(requestedName, origin, completion);
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