/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.util;

import com.goide.GoFileType;
import com.goide.psi.GoFile;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.sdk.GoPackageUtil;
import com.goide.sdk.GoSdkService;
import com.goide.sdk.GoSdkUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;

public class GoPathUseScope extends GlobalSearchScope {
  public static GlobalSearchScope create(@NotNull PsiElement declarationContext) {
    PsiFile declarationPsiFile = declarationContext.getContainingFile();
    if (!(declarationPsiFile instanceof GoFile)) {
      return GlobalSearchScope.fileScope(declarationPsiFile);
    }
    if (GoPsiImplUtil.isBuiltinFile(declarationPsiFile)) {
      return GlobalSearchScope.allScope(declarationContext.getProject());
    }

    PsiDirectory declarationPsiDirectory = declarationPsiFile.getContainingDirectory();
    VirtualFile declarationDirectory = declarationPsiDirectory != null ? declarationPsiDirectory.getVirtualFile() : null;
    if (declarationDirectory == null) {
      return GlobalSearchScope.fileScope(declarationPsiFile);
    }

    return new GoPathUseScope(declarationPsiFile.getProject(), declarationDirectory);
  }

  @NotNull private final VirtualFile myDeclarationDirectory;

  private GoPathUseScope(@NotNull Project project, @NotNull VirtualFile declarationDirectory) {
    super(project);
    myDeclarationDirectory = declarationDirectory;
  }

  @Override
  public boolean contains(@NotNull VirtualFile file) {
    VirtualFile referenceDirectory = file.isDirectory() ? file : file.getParent();
    if (referenceDirectory == null) {
      return false;
    }
    if (referenceDirectory.equals(myDeclarationDirectory)) {
      return true;
    }

    Project project = ObjectUtils.assertNotNull(getProject());
    PsiManager psiManager = PsiManager.getInstance(project);
    PsiDirectory referencePsiDirectory = psiManager.findDirectory(referenceDirectory);
    Module module = referencePsiDirectory != null ? ModuleUtilCore.findModuleForPsiElement(referencePsiDirectory) : null;

    GoPathScopeHelper scopeHelper = GoPathScopeHelper.fromReferenceDirectory(project, module, referenceDirectory);
    if (!scopeHelper.couldBeReferenced(myDeclarationDirectory, referenceDirectory)) {
      return false;
    }

    if (file.getFileType() == GoFileType.INSTANCE) {
      PsiFile referencePsiFile = psiManager.findFile(file);
      if (referencePsiFile instanceof GoFile) {
        PsiDirectory declarationDirectory = psiManager.findDirectory(myDeclarationDirectory);
        if (declarationDirectory != null) {
          String importPath = GoSdkUtil.getImportPath(referencePsiDirectory, scopeHelper.isVendoringEnabled());
          if (((GoFile)referencePsiFile).getImportedPackagesMap().containsKey(importPath)) {
            return true;
          }
          for (GoFile packageFile : GoPackageUtil.getAllPackageFiles((GoFile)referencePsiFile)) {
            if (packageFile != referencePsiFile && referencePsiFile.getOriginalFile() != packageFile) {
              if (packageFile.getImportedPackagesMap().containsKey(importPath)) {
                return true;
              }
            }
          }
        }
      }
    }
    return true;
  }

  @Override
  public int compare(@NotNull VirtualFile file1, @NotNull VirtualFile file2) {
    return 0;
  }

  @Override
  public boolean isSearchInModuleContent(@NotNull Module aModule) {
    return GoSdkService.getInstance(ObjectUtils.assertNotNull(getProject())).isGoModule(aModule);
  }

  @Override
  public boolean isSearchInLibraries() {
    return false;
  }
}
