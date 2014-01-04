package com.goide.psi.impl;

import com.goide.psi.GoFile;
import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoReferenceExpression;
import com.goide.psi.GoVarDefinition;
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

class GoReference extends PsiReferenceBase<PsiElement> {
  @NotNull private final PsiElement myIdentifier;
  @NotNull private final GoReferenceExpression myRefExpression;

  public GoReference(@NotNull GoReferenceExpression refExpression) {
    super(refExpression.getIdentifier(), TextRange.from(0, refExpression.getIdentifier().getTextLength()));
    myIdentifier = refExpression.getIdentifier();
    myRefExpression = refExpression;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    GoReferenceExpression qualifier = myRefExpression.getQualifier();
    if (qualifier == null) {
      PsiFile file = myRefExpression.getContainingFile();
      if (file instanceof GoFile) {
        GoVarProcessor processor = new GoVarProcessor(myIdentifier.getText(), myRefExpression, false);
        ResolveUtil.treeWalkUp(myRefExpression, processor);
        GoVarDefinition result = processor.getResult();
        if (result != null) return result;
        for (GoFunctionDeclaration f : ((GoFile)file).getFunctions()) {
          if (myIdentifier.getText().equals(f.getName())) return f;
        }
      }
    }
    return null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    ArrayList<LookupElement> result = ContainerUtil.newArrayList();
    GoReferenceExpression qualifier = myRefExpression.getQualifier();
    if (qualifier == null) {
      PsiFile file = myRefExpression.getContainingFile();
      if (file instanceof GoFile) {
        GoVarProcessor processor = new GoVarProcessor(myIdentifier.getText(), myRefExpression, true);
        ResolveUtil.treeWalkUp(myRefExpression, processor);
        for (GoVarDefinition v : processor.getVariants()) {
          result.add(GoPsiImplUtil.createVariableLookupElement(v));
        }
        for (GoFunctionDeclaration f : ((GoFile)file).getFunctions()) {
          result.add(GoPsiImplUtil.createFunctionLookupElement(f));
        }
      }
    }
    return ArrayUtil.toObjectArray(result);
  }
}
