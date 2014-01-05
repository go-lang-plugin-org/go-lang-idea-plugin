package com.goide.psi.impl;

import com.goide.psi.GoFile;
import com.goide.psi.GoTypeReferenceExpression;
import com.goide.psi.GoTypeSpec;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

class GoTypeReference extends PsiReferenceBase<PsiElement> {
  @NotNull private final PsiElement myIdentifier;
  @NotNull private final GoTypeReferenceExpression myRefExpression;

  public GoTypeReference(@NotNull GoTypeReferenceExpression refExpression) {
    super(refExpression.getIdentifier(), TextRange.from(0, refExpression.getIdentifier().getTextLength()));
    myIdentifier = refExpression.getIdentifier();
    myRefExpression = refExpression;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    GoTypeReferenceExpression qualifier = myRefExpression.getQualifier();
    if (qualifier == null) {
      PsiFile file = myRefExpression.getContainingFile();
      if (file instanceof GoFile) {
        for (GoTypeSpec t : ((GoFile)file).getTypes()) {
          if (myIdentifier.getText().equals(t.getName())) return t;
        }
      }
    }
    return null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    ArrayList<LookupElement> result = ContainerUtil.newArrayList();
    GoTypeReferenceExpression qualifier = myRefExpression.getQualifier();
    if (qualifier == null) {
      PsiFile file = myRefExpression.getContainingFile();
      if (file instanceof GoFile) {
        for (GoTypeSpec t : ((GoFile)file).getTypes()) {
          result.add(GoPsiImplUtil.createTypeLookupElement(t));
        }
      }
    }
    return ArrayUtil.toObjectArray(result);
  }
}
