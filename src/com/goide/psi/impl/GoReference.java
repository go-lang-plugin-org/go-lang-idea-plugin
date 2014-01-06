package com.goide.psi.impl;

import com.goide.psi.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
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
        for (GoConstDefinition definition : ((GoFile)file).getConsts()) {
          processor.execute(definition, ResolveState.initial());
        }
        for (GoVarDefinition definition : ((GoFile)file).getVars()) {
          processor.execute(definition, ResolveState.initial());
        }
        processFunctionParameters(processor);
        GoNamedElement result = processor.getResult();
        if (result != null) return result;
        for (GoFunctionDeclaration f : ((GoFile)file).getFunctions()) {
          if (myIdentifier.getText().equals(f.getName())) return f;
        }
      }
    }
    return null;
  }

  private void processFunctionParameters(GoVarProcessor processor) {
    GoFunctionDeclaration function = PsiTreeUtil.getParentOfType(myRefExpression, GoFunctionDeclaration.class);
    GoSignature signature = function != null ? function.getSignature() : null;
    GoParameters parameters = signature != null ? signature.getParameters() : null;
    if (parameters != null) parameters.processDeclarations(processor, ResolveState.initial(), null, myRefExpression);
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
        processFunctionParameters(processor);
        for (GoNamedElement v : processor.getVariants()) {
          result.add(GoPsiImplUtil.createVariableLikeLookupElement(v));
        }
        for (GoConstDefinition c : ((GoFile)file).getConsts()) {
          result.add(GoPsiImplUtil.createVariableLikeLookupElement(c));
        }
        for (GoVarDefinition v : ((GoFile)file).getVars()) {
          result.add(GoPsiImplUtil.createVariableLikeLookupElement(v));
        }
        for (GoFunctionDeclaration f : ((GoFile)file).getFunctions()) {
          result.add(GoPsiImplUtil.createFunctionLookupElement(f));
        }
      }
    }
    return ArrayUtil.toObjectArray(result);
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    myRefExpression.replace(GoElementFactory.createReferenceFromText(myElement.getProject(), newElementName));
    return myRefExpression;
  }
}
