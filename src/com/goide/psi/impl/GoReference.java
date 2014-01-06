package com.goide.psi.impl;

import com.goide.psi.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class GoReference extends PsiReferenceBase<PsiElement> {
  @NotNull private final PsiElement myIdentifier;
  @NotNull private final GoReferenceExpression myRefExpression;

  public GoReference(@NotNull GoReferenceExpression element) {
    super(element, TextRange.from(element.getIdentifier().getStartOffsetInParent(), element.getIdentifier().getTextLength()));
    myIdentifier = element.getIdentifier();
    myRefExpression = element;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    GoReferenceExpression qualifier = myRefExpression.getQualifier();
    PsiFile file = myRefExpression.getContainingFile();
    if (file instanceof GoFile) {
      if (qualifier == null) {
        return processUnqualified((GoFile)file);
      }
      else {
        PsiDirectory dir = getDirectory(qualifier);
        PsiElement result = processDirectory(dir);
        if (result != null) return result;
      }
    }
    return null;
  }

  @Nullable
  private PsiDirectory getDirectory(@NotNull GoReferenceExpression qualifier) {
    PsiReference reference = qualifier.getReference();
    PsiElement resolve = reference != null ? reference.resolve() : null;

    PsiDirectory dir = null;
    if (resolve instanceof GoImportSpec) {
      dir = resolvePackage(StringUtil.unquoteString(((GoImportSpec)resolve).getString().getText()));
    }
    else if (resolve instanceof PsiDirectory) {
      dir = (PsiDirectory)resolve;
    }
    return dir;
  }

  @Nullable
  private PsiElement processDirectory(@Nullable PsiDirectory dir) {
    if (dir != null) {
      for (PsiFile psiFile : dir.getFiles()) {
        if (psiFile instanceof GoFile) {
          PsiElement element = processUnqualified((GoFile)psiFile);
          if (element != null) return element;
        }
      }
    }
    return null;
  }

  private void processDirectory(@NotNull List<LookupElement> result, @Nullable PsiDirectory dir) {
    if (dir != null) {
      for (PsiFile psiFile : dir.getFiles()) {
        if (psiFile instanceof GoFile) processFile(result, (GoFile)psiFile, false);
      }
    }
  }

  @Nullable
  private PsiElement processUnqualified(@NotNull GoFile file) {
    String id = myIdentifier.getText();
    GoVarProcessor processor = new GoVarProcessor(id, myRefExpression, false);
    ResolveUtil.treeWalkUp(myRefExpression, processor);
    for (GoConstDefinition definition : file.getConsts()) {
      processor.execute(definition, ResolveState.initial());
    }
    for (GoVarDefinition definition : file.getVars()) {
      processor.execute(definition, ResolveState.initial());
    }
    processFunctionParameters(processor);
    GoNamedElement result = processor.getResult();
    if (result != null) return result;
    for (GoFunctionDeclaration f : file.getFunctions()) {
      if (id.equals(f.getName())) return f;
    }

    Object o = file.getImportMap().get(id);
    if (o instanceof GoImportSpec) return (PsiElement)o;
    if (o instanceof String) return resolvePackage((String)o);
    return null;
  }

  @Nullable
  private PsiDirectory resolvePackage(@NotNull String str) {
    VirtualFile basePath = getSdkBasePath();
    VirtualFile child = basePath != null ? basePath.findFileByRelativePath(str) : null;
    return child == null ? null : PsiManager.getInstance(myRefExpression.getProject()).findDirectory(child);
  }

  @Nullable
  private VirtualFile getSdkBasePath() {
    Module module = ModuleUtilCore.findModuleForPsiElement(myRefExpression);
    Sdk sdk  = module == null ? null : ModuleRootManager.getInstance(module).getSdk();
    return sdk == null ? null : LocalFileSystem.getInstance().findFileByPath(sdk.getHomePath() + "/src/pkg");
  }

  private void processFunctionParameters(@NotNull GoVarProcessor processor) {
    GoFunctionDeclaration function = PsiTreeUtil.getParentOfType(myRefExpression, GoFunctionDeclaration.class);
    GoSignature signature = function != null ? function.getSignature() : null;
    GoParameters parameters = signature != null ? signature.getParameters() : null;
    if (parameters != null) parameters.processDeclarations(processor, ResolveState.initial(), null, myRefExpression);
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    List<LookupElement> result = ContainerUtil.newArrayList();
    GoReferenceExpression qualifier = myRefExpression.getQualifier();
    PsiFile file = myRefExpression.getContainingFile();
    if (file instanceof GoFile) {
      if (qualifier == null) processFile(result, (GoFile)file, true);
      else {
        PsiDirectory dir = getDirectory(qualifier);
        processDirectory(result, dir);
      }
    }
    return ArrayUtil.toObjectArray(result);
  }

  private void processFile(@NotNull List<LookupElement> result, @NotNull GoFile file, boolean localCompletion) {
    GoVarProcessor processor = new GoVarProcessor(myIdentifier.getText(), myRefExpression, true);
    ResolveUtil.treeWalkUp(myRefExpression, processor);
    processFunctionParameters(processor);
    for (GoNamedElement v : processor.getVariants()) {
      result.add(GoPsiImplUtil.createVariableLikeLookupElement(v));
    }
    for (GoConstDefinition c : file.getConsts()) {
      result.add(GoPsiImplUtil.createVariableLikeLookupElement(c));
    }
    for (GoVarDefinition v : file.getVars()) {
      result.add(GoPsiImplUtil.createVariableLikeLookupElement(v));
    }
    for (GoFunctionDeclaration f : file.getFunctions()) {
      result.add(GoPsiImplUtil.createFunctionLookupElement(f));
    }
    if (localCompletion) {
      for (String i : file.getImportMap().keySet()) {
        result.add(GoPsiImplUtil.createImportLookupElement(i));
      }
    }
  }

  @NotNull
  @Override
  public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
    myRefExpression.replace(GoElementFactory.createReferenceFromText(myElement.getProject(), newElementName));
    return myRefExpression;
  }
}
