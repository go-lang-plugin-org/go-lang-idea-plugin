package com.goide.psi.impl;

import com.goide.GoSdkType;
import com.goide.psi.GoFile;
import com.goide.psi.GoImportSpec;
import com.goide.psi.GoNamedElement;
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

  public static boolean isPublic(@NotNull GoNamedElement o) {
    return StringUtil.isCapitalized(o.getName());
  }

  @Nullable
  protected PsiDirectory getDirectory(@NotNull PsiElement qualifier) {
    PsiReference reference = qualifier.getReference();
    PsiElement resolve = reference != null ? reference.resolve() : null;

    PsiDirectory dir = null;
    if (resolve instanceof GoImportSpec) {
      dir = resolvePackage(StringUtil.unquoteString(((GoImportSpec)resolve).getImportString().getText()));
    }
    else if (resolve instanceof PsiDirectory) {
      dir = (PsiDirectory)resolve;
    }
    return dir;
  }

  @Nullable
  protected PsiDirectory resolvePackage(@NotNull String str) {
    if (str.startsWith("/")) return null;
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

  protected void processDirectory(@NotNull List<LookupElement> result, @Nullable PsiDirectory dir) {
    if (dir != null) {
      for (PsiFile psiFile : dir.getFiles()) {
        if (psiFile instanceof GoFile) processFile(result, (GoFile)psiFile, false);
      }
    }
  }

  @Nullable
  protected PsiElement processDirectory(@Nullable PsiDirectory dir) {
    if (dir != null) {
      for (PsiFile psiFile : dir.getFiles()) {
        if (psiFile instanceof GoFile) {
          PsiElement element = processUnqualified((GoFile)psiFile, false);
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
        PsiDirectory dir = getDirectory(qualifier);
        PsiElement result = processDirectory(dir);
        if (result != null) return result;
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
        processDirectory(result, getDirectory(qualifier));
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

  protected void processFile(@NotNull List<LookupElement> result, @NotNull GoFile file, boolean localCompletion) {
  }

  @Nullable
  protected PsiElement getQualifier() {
    return null;
  }

  @Nullable
  protected PsiElement processUnqualified(@NotNull GoFile file, boolean localResolve) {
    return null;
  }
}
