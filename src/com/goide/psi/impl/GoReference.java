package com.goide.psi.impl;

import com.goide.psi.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class GoReference extends GoReferenceBase {
  private static final Set RESERVED_NAMES = ContainerUtil.newHashSet("print", "println");
  @NotNull private final PsiElement myIdentifier;
  @NotNull private final GoReferenceExpression myRefExpression;

  public GoReference(@NotNull GoReferenceExpression element) {
    super(element, TextRange.from(element.getIdentifier().getStartOffsetInParent(), element.getIdentifier().getTextLength()));
    myIdentifier = element.getIdentifier();
    myRefExpression = element;
  }

  @Nullable
  @Override
  protected PsiElement getQualifier() {
    return myRefExpression.getQualifier();
  }

  @Nullable
  @Override
  protected PsiElement processUnqualified(@NotNull GoFile file, boolean localResolve) {
    String id = myIdentifier.getText();
    GoVarProcessor processor = new GoVarProcessor(id, myRefExpression, false);
    ResolveUtil.treeWalkUp(myRefExpression, processor);
    for (GoConstDefinition definition : file.getConsts()) {
      if (isPublic(definition) || localResolve) processor.execute(definition, ResolveState.initial());
    }
    for (GoVarDefinition definition : file.getVars()) {
      if (isPublic(definition) || localResolve) processor.execute(definition, ResolveState.initial());
    }
    processReceiver(processor);
    processFunctionParameters(processor);
    GoNamedElement result = processor.getResult();
    if (result != null) return result;
    for (GoFunctionDeclaration f : file.getFunctions()) {
      if ((isPublic(f) || localResolve) && id.equals(f.getName())) return f;
    }

    if (RESERVED_NAMES.contains(id)) return myElement;

    return resolveImportOrPackage(file, id);
  }

  private void processFunctionParameters(@NotNull GoVarProcessor processor) {
    GoFunctionDeclaration function = PsiTreeUtil.getParentOfType(myRefExpression, GoFunctionDeclaration.class); // todo: nested functions?
    GoSignature signature = function != null ? function.getSignature() : null;
    GoParameters parameters = signature != null ? signature.getParameters() : null;
    if (parameters != null) parameters.processDeclarations(processor, ResolveState.initial(), null, myRefExpression);
  }

  private void processReceiver(@NotNull GoVarProcessor processor) {
    GoMethodDeclaration method = PsiTreeUtil.getParentOfType(myRefExpression, GoMethodDeclaration.class); // todo: nested methods?
    GoReceiver receiver = method != null ? method.getReceiver() : null;
    if (receiver != null) receiver.processDeclarations(processor, ResolveState.initial(), null, myRefExpression);
  }

  @Override
  protected void processFile(@NotNull List<LookupElement> result, @NotNull GoFile file, boolean localCompletion) {
    GoVarProcessor processor = new GoVarProcessor(myIdentifier.getText(), myRefExpression, true);
    ResolveUtil.treeWalkUp(myRefExpression, processor);
    processReceiver(processor);
    processFunctionParameters(processor);
    for (GoNamedElement v : processor.getVariants()) {
      if (isPublic(v) || localCompletion) result.add(GoPsiImplUtil.createVariableLikeLookupElement(v));
    }
    for (GoConstDefinition c : file.getConsts()) {
      if (isPublic(c) || localCompletion) result.add(GoPsiImplUtil.createVariableLikeLookupElement(c));
    }
    for (GoVarDefinition v : file.getVars()) {
      if (isPublic(v) || localCompletion) result.add(GoPsiImplUtil.createVariableLikeLookupElement(v));
    }
    for (GoFunctionDeclaration f : file.getFunctions()) {
      if (isPublic(f) || localCompletion) result.add(GoPsiImplUtil.createFunctionLookupElement(f));
    }
    processImports(result, file, localCompletion);
  }

  @NotNull
  @Override
  public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
    myRefExpression.replace(GoElementFactory.createReferenceFromText(myElement.getProject(), newElementName));
    return myRefExpression;
  }
}
