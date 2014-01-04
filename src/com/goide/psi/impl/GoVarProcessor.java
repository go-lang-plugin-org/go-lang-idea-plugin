package com.goide.psi.impl;

import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoVarDefinition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GoVarProcessor extends BaseScopeProcessor {
  private List<GoVarDefinition> myVarList = new ArrayList<GoVarDefinition>(0);

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
    if (!(psiElement instanceof GoVarDefinition)) return true;
    if (psiElement instanceof GoFunctionDeclaration) return false;
    if (!myIsCompletion && !psiElement.getText().equals(myRequestedName)) return true;
    if (psiElement.equals(myOrigin)) return true;

    GoFunctionDeclaration functionDeclaration = PsiTreeUtil.getTopmostParentOfType(myOrigin, GoFunctionDeclaration.class);

    if (functionDeclaration != null) {
      boolean add = myVarList.add((GoVarDefinition)psiElement);
      return myIsCompletion || !add;
    }

    return true;
  }

  @Nullable
  public GoVarDefinition getResult() {
    return ContainerUtil.getFirstItem(myVarList);
  }

  public List<GoVarDefinition> getVariants() {
    return myVarList;
  }
}