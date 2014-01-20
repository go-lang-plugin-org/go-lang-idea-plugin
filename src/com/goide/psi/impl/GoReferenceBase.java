package com.goide.psi.impl;

import com.goide.GoSdkType;
import com.goide.psi.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class GoReferenceBase extends PsiReferenceBase<PsiElement> {
  public GoReferenceBase(PsiElement element, TextRange range) {
    super(element, range);
  }

  protected static void processImports(List<LookupElement> result, GoFile file, boolean localCompletion) {
    if (localCompletion) {
      for (String i : file.getImportMap().keySet()) {
        result.add(GoPsiImplUtil.createImportLookupElement(i));
      }
    }
  }

  @Nullable
  protected PsiDirectory getDirectory(@NotNull PsiElement qualifier) {
    PsiElement resolve = calcQualifierResolve(qualifier);

    PsiDirectory dir = null;
    if (resolve instanceof GoImportSpec) {
      dir = resolvePackage(StringUtil.unquoteString(((GoImportSpec)resolve).getImportString().getText()));
    }
    else if (resolve instanceof PsiDirectory) {
      dir = (PsiDirectory)resolve;
    }
    return dir;
  }

  private static PsiElement calcQualifierResolve(PsiElement qualifier) {
    PsiReference reference = qualifier.getReference();
    return reference != null ? reference.resolve() : null;
  }

  @Nullable
  protected PsiDirectory resolvePackage(@NotNull String str) {
    if (str.startsWith("/") || str.isEmpty()) return null;
    for (VirtualFile file : getPathsToLookup()) {
      VirtualFile child = file != null ? file.findFileByRelativePath(str) : null;
      if (child != null) return PsiManager.getInstance(myElement.getProject()).findDirectory(child);
    }
    return null;
  }

  @NotNull
  private List<VirtualFile> getPathsToLookup() {
    List<VirtualFile> result = ContainerUtil.newArrayList();
    VirtualFile sdkHome = getSdkHome();
    ContainerUtil.addIfNotNull(result, sdkHome);
    result.addAll(GoSdkType.getGoPathsSources());
    return result;
  }

  @Nullable
  protected VirtualFile getSdkHome() {
    Module module = ModuleUtilCore.findModuleForPsiElement(myElement);
    Sdk sdk  = module == null ? null : ModuleRootManager.getInstance(module).getSdk();
    return sdk == null ? null : LocalFileSystem.getInstance().findFileByPath(sdk.getHomePath() + "/src/pkg");
  }

  protected void processDirectory(@NotNull List<LookupElement> result, @Nullable PsiDirectory dir, @Nullable String packageName, boolean localCompletion) {
    if (dir != null) {
      for (PsiFile psiFile : dir.getFiles()) {
        if (psiFile instanceof GoFile) {
          if (packageName != null && !Comparing.equal(((GoFile)psiFile).getPackageName(), packageName)) continue;
          processFile(result, (GoFile)psiFile, localCompletion);
        }
      }
    }
  }

  @Nullable
  protected PsiElement processDirectory(@Nullable PsiDirectory dir, @Nullable String packageName, boolean localResolve) {
    if (dir != null) {
      for (PsiFile psiFile : dir.getFiles()) {
        if (psiFile instanceof GoFile) {
          GoFile goFile = (GoFile)psiFile;
          if (packageName != null && !Comparing.equal(goFile.getPackageName(), packageName)) continue;
          PsiElement element = processUnqualified(goFile, localResolve);
          if (element != null) return element;
        }
      }
    }
    return null;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    PsiElement qualifier = getQualifier();
    PsiFile file = myElement.getContainingFile();
    if (file instanceof GoFile) {
      if (qualifier == null) {
        PsiElement unqualified = processUnqualified((GoFile)file, true);
        if (unqualified != null) return unqualified;

        VirtualFile vfile = file.getOriginalFile().getVirtualFile();
        VirtualFile localDir = vfile == null ? null : vfile.getParent();
        PsiDirectory localPsiDir = localDir == null ? null : PsiManager.getInstance(myElement.getProject()).findDirectory(localDir);
        PsiElement result = processDirectory(localPsiDir, ((GoFile)file).getPackageName(), true);
        if (result != null) return result;

        if (!file.getName().equals("builtin.go")) {
          VirtualFile home = getSdkHome();
          VirtualFile vBuiltin = home != null ? home.findFileByRelativePath("builtin/builtin.go") : null;
          if (vBuiltin != null) {
            PsiFile psiBuiltin = PsiManager.getInstance(file.getProject()).findFile(vBuiltin);
            PsiElement r = psiBuiltin instanceof GoFile ? processUnqualified((GoFile)psiBuiltin, true) : null;
            if (r != null) return r;
          }
        }
      }
      else {
        PsiElement qualifierResolve = calcQualifierResolve(qualifier);
        if (qualifierResolve instanceof GoNamedElement) { // todo: create a separate interface, e.g. GoTypeHolder
          GoType type = ((GoNamedElement)qualifierResolve).getGoType();
          PsiElement fromType = processGoType(type);
          if (fromType != null) return fromType;
        }
        PsiDirectory dir = getDirectory(qualifier);
        PsiElement result = processDirectory(dir, null, false);
        if (result != null) return result;
      }
    }
    return null;
  }

  @Nullable
  private PsiElement processGoType(@Nullable GoType type) {
    if (type == null) return null;

    PsiElement fromExistingType = processExistingType(type);
    if (fromExistingType != null) return fromExistingType;

    if (type instanceof GoPointerType) type = ((GoPointerType)type).getType();

    GoTypeReferenceExpression refExpr = type != null ? type.getTypeReferenceExpression() : null;
    PsiReference reference = refExpr != null ? refExpr.getReference() : null;
    PsiElement resolve = reference != null ? reference.resolve() : null;
    if (resolve instanceof GoTypeSpec) {
      GoType resolveType = ((GoTypeSpec)resolve).getType();
      PsiElement element = processExistingType(resolveType);
      return element != null ? element : null;
    }
    return null;
  }

  @Nullable
  private PsiElement processExistingType(@Nullable GoType type) {
    if (type == null) return null;
    if (type instanceof GoStructType) {
      GoVarProcessor processor = createProcessor();
      if (processor != null) {
        type.processDeclarations(processor, ResolveState.initial(), null, myElement);
        GoNamedElement result = processor.getResult();
        if (result != null) return result;
      }
    }
    PsiFile file = type.getContainingFile().getOriginalFile();
    if (file instanceof GoFile) { // todo: process the whole package
      List<GoMethodDeclaration> methods = ((GoFile)file).getMethods();
      for (GoMethodDeclaration method : methods) {
        if (!Comparing.equal(getIdentifier().getText(), method.getName())) continue;
        GoTypeReferenceExpression e = method.getReceiver().getType().getTypeReferenceExpression();
        PsiReference reference1 = e != null ? e.getReference() : null;
        PsiElement resolve1 = reference1 != null ? reference1.resolve() : null;
        if (type.getParent().equals(resolve1)) return method;
      }
    }
    return null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    List<LookupElement> result = ContainerUtil.newArrayList();
    PsiElement qualifier = getQualifier();
    PsiFile file = myElement.getContainingFile();
    if (file instanceof GoFile) {
      if (qualifier == null) {
        processFile(result, (GoFile)file, true);

        VirtualFile vfile = file.getOriginalFile().getVirtualFile();
        VirtualFile localDir = vfile == null ? null : vfile.getParent();
        PsiDirectory localPsiDir = localDir == null ? null : PsiManager.getInstance(myElement.getProject()).findDirectory(localDir);
        processDirectory(result, localPsiDir, ((GoFile)file).getPackageName(), true);

        if (!file.getName().equals("builtin.go")) {
          VirtualFile home = getSdkHome();
          VirtualFile vBuiltin = home != null ? home.findFileByRelativePath("builtin/builtin.go") : null;
          if (vBuiltin != null) {
            PsiFile psiBuiltin = PsiManager.getInstance(file.getProject()).findFile(vBuiltin);
            if (psiBuiltin instanceof GoFile) {
              processFile(result, (GoFile)psiBuiltin, true);
            }
          }
        }
      }
      else {
        PsiElement qualifierResolve = calcQualifierResolve(qualifier);
        if (qualifierResolve instanceof GoNamedElement) {
          GoType goType = ((GoNamedElement)qualifierResolve).getGoType();
          if (goType instanceof GoPointerType) goType = ((GoPointerType)goType).getType();
          GoTypeReferenceExpression expression = goType != null ? goType.getTypeReferenceExpression() : null;
          PsiReference reference = expression != null ? expression.getReference() : null;
          PsiElement resolve = reference != null ? reference.resolve() : null;
          if (resolve instanceof GoTypeSpec) {
            GoType type = ((GoTypeSpec)resolve).getType();
            if (type instanceof GoStructType) {
              for (GoFieldDeclaration declaration : ((GoStructType)type).getFieldDeclarationList()) {
                for (GoFieldDefinition d : declaration.getFieldDefinitionList()) {
                  result.add(GoPsiImplUtil.createVariableLikeLookupElement(d));
                }
              }
            }
            
            PsiFile typeFile = resolve.getContainingFile().getOriginalFile();
            if (typeFile instanceof GoFile) { // todo: process the whole package
              List<GoMethodDeclaration> methods = ((GoFile)typeFile).getMethods();
              for (GoMethodDeclaration method : methods) {
                GoTypeReferenceExpression e = method.getReceiver().getType().getTypeReferenceExpression();
                PsiReference reference1 = e != null ? e.getReference() : null;
                PsiElement resolve1 = reference1 != null ? reference1.resolve() : null;
                if (resolve1 != null && resolve.textMatches(resolve1)) { // todo: better equality predicate
                  result.add(GoPsiImplUtil.createFunctionOrMethodLookupElement(method));
                }
              }
            }
          }
        }
        processDirectory(result, getDirectory(qualifier), null, false);
      }
    }
    return ArrayUtil.toObjectArray(result);
  }

  @Nullable
  protected PsiElement resolveImportOrPackage(@NotNull GoFile file, @NotNull String id) {
    Object o = file.getImportMap().get(id);
    if (o instanceof GoImportSpec) return (PsiElement)o;
    if (o instanceof String) return resolvePackage((String)o);
    return null;
  }

  @Nullable
  protected GoVarProcessor createProcessor() {
    return null;
  }

  protected void processFile(@NotNull List<LookupElement> result, @NotNull GoFile file, boolean localCompletion) {
  }

  @NotNull
  public abstract PsiElement getIdentifier();

  @Nullable
  protected PsiElement getQualifier() {
    return null;
  }

  @Nullable
  protected PsiElement processUnqualified(@NotNull GoFile file, boolean localResolve) {
    return null;
  }
}
