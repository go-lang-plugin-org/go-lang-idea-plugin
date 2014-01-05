package com.goide.psi.impl;

import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoNamedElement;
import com.goide.psi.GoParamDefinition;
import com.goide.psi.GoVarDefinition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GoVarProcessor extends BaseScopeProcessor {
  private List<GoNamedElement> myVarList = new ArrayList<GoNamedElement>(0);

  private final String myRequestedName;
  private final PsiElement myOrigin;
  private final boolean myIsCompletion;

  public GoVarProcessor(String requestedName, PsiElement origin, boolean completion) {
    myRequestedName = requestedName;
    myOrigin = origin;
    myIsCompletion = completion;
  }

  @Override
  public boolean execute(@NotNull PsiElement psiElement, ResolveState resolveState) {
    if (psiElement instanceof GoFunctionDeclaration) return false;
    if (!(psiElement instanceof GoNamedElement)) return true;
    if (!(psiElement instanceof GoVarDefinition) && !(psiElement instanceof GoParamDefinition)) return true;
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
  public List<GoNamedElement> getVariants() {
    return myVarList;
  }
}