package com.goide.psi.impl;

import com.goide.GoSdkUtil;
import com.goide.psi.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
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

  @NotNull
  @Override
  public PsiElement getIdentifier() {
    return myIdentifier;
  }

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
    if ("_".equals(id)) return myElement; // todo: need a better solution
    GoScopeProcessorBase processor = createProcessor(false);
    ResolveUtil.treeWalkUp(myRefExpression, processor);
    processReceiver(processor);
    processFunctionParameters(processor);
    for (GoConstDefinition definition : file.getConsts()) {
      if (definition.isPublic() || localResolve) processor.execute(definition, ResolveState.initial());
    }
    for (GoVarDefinition definition : file.getVars()) {
      if (definition.isPublic() || localResolve) processor.execute(definition, ResolveState.initial());
    }
    GoNamedElement result = processor.getResult();
    if (result != null) return result;
    for (GoFunctionDeclaration f : file.getFunctions()) {
      if ((f.isPublic() || localResolve) && id.equals(f.getName())) return f;
    }

    if (RESERVED_NAMES.contains(id)) return myElement;

    PsiElement parent = myElement.getParent();
    if (parent instanceof GoSelectorExpr) {
      List<GoExpression> list = ((GoSelectorExpr)parent).getExpressionList();
      if (list.size() > 1 && list.get(1).isEquivalentTo(myElement)) {
        GoType type = list.get(0).getGoType();
        PsiElement element = processGoType(type);
        if (element != null) return element;
        return null;
      }
    }
    // todo: remove duplicate
    PsiElement grandPa = parent.getParent();
    if (grandPa instanceof GoSelectorExpr) {
      List<GoExpression> list = ((GoSelectorExpr)grandPa).getExpressionList();
      if (list.size() > 1 && list.get(1).isEquivalentTo(parent)) {
        GoType type = list.get(0).getGoType();
        PsiElement element = processGoType(type);
        if (element != null) return element;
      }
    }

    if (myElement.getParent() instanceof GoCallExpr && StringUtil.toLowerCase(id).equals(id)) {
      GoFile builtinFile = GoSdkUtil.findBuiltinFile(myElement);
      if (builtinFile != null) {
        List<GoTypeSpec> types = builtinFile.getTypes();
        for (GoTypeSpec type : types) {
          if (id.equals(type.getName())) return type;
        }
      }
    }

    return resolveImportOrPackage(file, id);
  }

  @Override
  @NotNull
  protected GoScopeProcessorBase createProcessor(boolean completion) {
    return new GoVarProcessor(myIdentifier.getText(), myRefExpression, completion);
  }

  private void processFunctionParameters(@NotNull GoScopeProcessorBase processor) {
    // todo: nested functions from FunctionLit
    GoFunctionOrMethodDeclaration function = PsiTreeUtil.getParentOfType(myRefExpression, GoFunctionOrMethodDeclaration.class);
    GoSignature signature = function != null ? function.getSignature() : null;
    GoParameters parameters;
    if (signature != null) {
      parameters = signature.getParameters();
      parameters.processDeclarations(processor, ResolveState.initial(), null, myRefExpression);
      GoResult result = signature.getResult();
      GoParameters resultParameters = result != null ? result.getParameters() : null;
      if (resultParameters!= null) resultParameters.processDeclarations(processor, ResolveState.initial(), null, myRefExpression);
    }
  }

  private void processReceiver(@NotNull GoScopeProcessorBase processor) {
    GoMethodDeclaration method = PsiTreeUtil.getParentOfType(myRefExpression, GoMethodDeclaration.class); // todo: nested methods?
    GoReceiver receiver = method != null ? method.getReceiver() : null;
    if (receiver != null) receiver.processDeclarations(processor, ResolveState.initial(), null, myRefExpression);
  }

  @Override
  protected void processFile(@NotNull List<LookupElement> result, @NotNull GoFile file, boolean localCompletion) {
    PsiElement parent = myElement.getParent();
    if (parent instanceof GoSelectorExpr) {
      List<GoExpression> list = ((GoSelectorExpr)parent).getExpressionList();
      if (list.size() > 1 && list.get(1).isEquivalentTo(myElement)) {
        GoType type = list.get(0).getGoType();
        if (type != null) {
          processInType(result, type.getParent(), type);
        }
      }
      return;
    }

    GoScopeProcessorBase processor = createProcessor(true);
    ResolveUtil.treeWalkUp(myRefExpression, processor);
    processReceiver(processor);
    processFunctionParameters(processor);
    for (GoNamedElement v : processor.getVariants()) {
      if (v.isPublic() || localCompletion) result.add(GoPsiImplUtil.createVariableLikeLookupElement(v));
    }
    for (GoConstDefinition c : file.getConsts()) {
      if (c.isPublic() || localCompletion) result.add(GoPsiImplUtil.createVariableLikeLookupElement(c));
    }
    for (GoVarDefinition v : file.getVars()) {
      if (v.isPublic() || localCompletion) result.add(GoPsiImplUtil.createVariableLikeLookupElement(v));
    }
    for (GoFunctionDeclaration f : file.getFunctions()) {
      if (f.isPublic() || localCompletion) result.add(GoPsiImplUtil.createFunctionOrMethodLookupElement(f));
    }

    GoFile builtinFile = GoSdkUtil.findBuiltinFile(myElement);
    if (builtinFile != null) {
      List<GoTypeSpec> types = builtinFile.getTypes();
      for (GoTypeSpec type : types) {
        if (!type.isPublic()) result.add(GoPsiImplUtil.createTypeConversionLookupElement(type));
      }
    }

    processImports(result, file, localCompletion);
  }

  @NotNull
  @Override
  public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
    myIdentifier.replace(GoElementFactory.createIdentifierFromText(myElement.getProject(), newElementName));
    return myRefExpression;
  }
}
