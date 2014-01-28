package com.goide.psi.impl;

import com.goide.psi.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;

public abstract class GoScopeProcessorBase extends BaseScopeProcessor {
  private LinkedHashSet<GoNamedElement> myVarList = ContainerUtil.newLinkedHashSet();

  private final String myRequestedName;
  private final PsiElement myOrigin;
  private final boolean myIsCompletion;

  public GoScopeProcessorBase(String requestedName, PsiElement origin, boolean completion) {
    myRequestedName = requestedName;
    myOrigin = origin;
    myIsCompletion = completion;
  }

  @Override
  public boolean execute(@NotNull PsiElement psiElement, ResolveState resolveState) {
    if (psiElement instanceof GoFunctionOrMethodDeclaration) return false;
    if (!(psiElement instanceof GoNamedElement)) return true;
    if (condition(psiElement)) return true;
    if (!myIsCompletion && !myRequestedName.equals(((GoNamedElement)psiElement).getName())) return true;
    if (psiElement.equals(myOrigin)) return true;

    boolean add = myVarList.add((GoNamedElement)psiElement);
    return myIsCompletion || !add;
  }

  @Nullable
  public GoNamedElement getResult() {
    return ContainerUtil.getFirstItem(myVarList);
  }

  @NotNull
  public Collection<GoNamedElement> getVariants() {
    return myVarList;
  }

  protected abstract boolean condition(@NotNull PsiElement element);
}
