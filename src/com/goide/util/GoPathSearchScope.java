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

import com.goide.project.GoVendoringUtil;
import com.goide.sdk.GoSdkUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class GoPathSearchScope extends GlobalSearchScope {
  private final Set<VirtualFile> myRoots;
  private final VirtualFile myContextDirectory;

  public static GoPathSearchScope create(@NotNull Project project, @Nullable Module module, @Nullable PsiElement context) {
    VirtualFile contextDirectory = null;
    if (context != null) {
      if (GoVendoringUtil.isVendoringEnabled(module)) {
        PsiDirectory psiDirectory = context.getContainingFile().getContainingDirectory();
        if (psiDirectory != null) {
          contextDirectory = psiDirectory.getVirtualFile();
        }
      }
      else {
        context = null;
      }
    }
    return new GoPathSearchScope(project, GoSdkUtil.getSourcesPathsToLookup(project, module, context), contextDirectory);
  }

  private GoPathSearchScope(@NotNull Project project, @NotNull Set<VirtualFile> roots, @Nullable VirtualFile contextDirectory) {
    super(project);
    if (contextDirectory != null && !contextDirectory.isDirectory()) {
      throw new IllegalArgumentException("Context should be a directory, given " + contextDirectory);
    }
    myRoots = roots;
    myContextDirectory = contextDirectory;
  }

  @Override
  public boolean contains(@NotNull VirtualFile file) {
    VirtualFile packageDirectory = file.isDirectory() ? file : file.getParent();
    if (packageDirectory == null) {
      return false;
    }

    if (ApplicationManager.getApplication().isUnitTestMode() && myRoots.contains(packageDirectory)) {
      return true;
    }

    String importPath = GoSdkUtil.getRelativePathToRoots(packageDirectory, myRoots);
    if (importPath == null) {
      return false;
    }
    if (importPath.isEmpty()) {
      return true;
    }

    if (myContextDirectory != null && GoSdkUtil.isUnreachableVendoredPackage(packageDirectory, myContextDirectory, myRoots)) {
      return false;
    }

    for (VirtualFile root : myRoots) {
      // returns false if directory was shadowed
      VirtualFile realDirectoryToResolve = root.findFileByRelativePath(importPath);
      if (realDirectoryToResolve != null) {
        return packageDirectory.equals(realDirectoryToResolve);
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
    return true;
  }

  @Override
  public boolean isSearchInLibraries() {
    return false;
  }
}
