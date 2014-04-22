package com.goide.psi.impl;

import com.goide.psi.GoVarDefinition;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoVarReference extends PsiReferenceBase<GoVarDefinition> {
  public GoVarReference(@NotNull GoVarDefinition element) {
    super(element, TextRange.from(0, element.getTextLength()));
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    GoScopeProcessorBase p = new GoVarProcessor(myElement.getText(), myElement, false);
    ResolveUtil.treeWalkUp(myElement, p);
    GoReference.processFunctionParameters(myElement, p);
    return ContainerUtil.getLastItem(p.getVariants());
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    GoScopeProcessorBase p = new GoVarProcessor(myElement.getText(), myElement, true);
    ResolveUtil.treeWalkUp(myElement, p);
    return ArrayUtil.toObjectArray(p.getVariants());
  }
  
  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    myElement.replace(GoElementFactory.createVarDefinitionFromText(myElement.getProject(), newElementName));
    return myElement;
  }
}
