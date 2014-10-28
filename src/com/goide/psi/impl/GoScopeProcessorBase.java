package com.goide.psi.impl;

import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.psi.GoNamedElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.OrderedSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class GoScopeProcessorBase extends BaseScopeProcessor {
  @NotNull protected OrderedSet<GoNamedElement> myResult = new OrderedSet<GoNamedElement>();

  private final String myRequestedName;
  private final PsiElement myOrigin;
  private final boolean myIsCompletion;

  public GoScopeProcessorBase(String requestedName, PsiElement origin, boolean completion) {
    myRequestedName = requestedName;
    myOrigin = origin;
    myIsCompletion = completion;
  }

  @Override
  public boolean execute(@NotNull PsiElement psiElement, @NotNull ResolveState resolveState) {
    if (psiElement instanceof GoFunctionOrMethodDeclaration) return false;
    if (!(psiElement instanceof GoNamedElement)) return true;
    if (condition(psiElement)) return true;
    if (!myIsCompletion && !myRequestedName.equals(((GoNamedElement)psiElement).getName())) return true;
    if (psiElement.equals(myOrigin)) return true;

    boolean add = add((GoNamedElement)psiElement);
    return myIsCompletion || !add;
  }

  protected boolean add(@NotNull GoNamedElement psiElement) {
    return myResult.add(psiElement);
  }

  @Nullable
  public GoNamedElement getResult() {
    return ContainerUtil.getFirstItem(myResult);
  }

  @NotNull
  public List<GoNamedElement> getVariants() {
    return myResult;
  }

  protected abstract boolean condition(@NotNull PsiElement element);
}
