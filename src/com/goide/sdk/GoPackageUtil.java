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

package com.goide.sdk;

import com.goide.GoConstants;
import com.goide.psi.GoFile;
import com.goide.runconfig.testing.GoTestFinder;
import com.goide.util.GoUtil;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.Function;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GoPackageUtil {
  private static final Key<CachedValue<Collection<String>>> PACKAGES_CACHE = Key.create("packages_cache");
  private static final Key<CachedValue<Collection<String>>> PACKAGES_TEST_TRIMMED_CACHE = Key.create("packages_test_trimmed_cache");

  private GoPackageUtil() {}

  @Contract("null -> false")
  public static boolean isBuiltinPackage(@Nullable PsiFileSystemItem directory) {
    return directory instanceof PsiDirectory
           && GoConstants.BUILTIN_PACKAGE_NAME.equals(directory.getName())
           && GoConstants.BUILTIN_PACKAGE_NAME.equals(GoSdkUtil.getImportPath((PsiDirectory)directory));
  }

  @NotNull
  public static List<GoFile> getAllPackageFiles(@NotNull GoFile file) {
    String name = file.getPackageName();
    PsiDirectory parent = file.getParent();
    if (parent == null || StringUtil.isEmpty(name)) return ContainerUtil.list(file);
    PsiElement[] children = parent.getChildren();
    List<GoFile> files = ContainerUtil.newArrayListWithCapacity(children.length);
    for (PsiElement element : children) {
      if (element instanceof GoFile && Comparing.equal(((GoFile)element).getPackageName(), name)) {
        files.add((GoFile)element);
      }
    }
    return files;
  }

  @NotNull
  public static GlobalSearchScope packageScope(@NotNull GoFile file) {
    List<GoFile> files = getAllPackageFiles(file);
    return GlobalSearchScope.filesScope(file.getProject(), ContainerUtil.map(files, new Function<GoFile, VirtualFile>() {
      @Override
      public VirtualFile fun(GoFile file) {
        return file.getVirtualFile();
      }
    }));
  }

  @NotNull
  public static Collection<String> getAllPackagesInDirectory(@Nullable PsiDirectory dir, boolean trimTestSuffices) {
    if (dir == null) return Collections.emptyList();
    Key<CachedValue<Collection<String>>> key = trimTestSuffices ? PACKAGES_TEST_TRIMMED_CACHE : PACKAGES_CACHE;
    return CachedValuesManager.getManager(dir.getProject()).getCachedValue(dir, key, new CachedValueProvider<Collection<String>>() {
      @Nullable
      @Override
      public Result<Collection<String>> compute() {
        Collection<String> set = ContainerUtil.newLinkedHashSet();
        for (PsiFile file : dir.getFiles()) {
          if (file instanceof GoFile && !GoUtil.directoryToIgnore(file.getName()) && GoUtil.allowed(file)) {
            String name = ((GoFile)file).getPackageName();
            if (StringUtil.isNotEmpty(name)) {
              set.add(trimTestSuffices && GoTestFinder.isTestFile(file) ? StringUtil.trimEnd(name, GoConstants.TEST_SUFFIX) : name);
            }
          }
        }
        return Result.create(set, dir);
      }
    }, false);
  }

  public static void processVendorDirectories(@NotNull PsiFile contextFile,
                                              @NotNull Collection<VirtualFile> sourceRoots,
                                              @NotNull Processor<VirtualFile> processor) {
    PsiDirectory containingDirectory = contextFile.getContainingDirectory();
    VirtualFile contextDirectory = containingDirectory != null ? containingDirectory.getVirtualFile() : null;
    if (contextDirectory != null) {
      processVendorDirectories(contextDirectory, sourceRoots, processor);
    }
  }

  public static void processVendorDirectories(@NotNull VirtualFile contextDirectory,
                                              @NotNull Collection<VirtualFile> sourceRoots,
                                              @NotNull Processor<VirtualFile> processor) {
    VirtualFile directory = contextDirectory;
    while (directory != null) {
      VirtualFile vendorDirectory = directory.findChild(GoConstants.VENDOR);
      if (vendorDirectory != null) {
        if (!processor.process(vendorDirectory)) {
          break;
        }
      }
      if (sourceRoots.contains(directory)) {
        break;
      }
      directory = directory.getParent();
    }
  }

  public static boolean isPackageShadowedByVendoring(@NotNull VirtualFile packageDirectory,
                                                     @NotNull VirtualFile contextFile,
                                                     @NotNull Collection<VirtualFile> sourceRoots,
                                                     @NotNull Collection<VirtualFile> vendorDirectories) {
    if (vendorDirectories.isEmpty()) {
      return false;
    }
    String importPath = GoSdkUtil.getPathRelativeToSdkAndLibrariesAndVendor(packageDirectory, sourceRoots, contextFile);
    if (importPath != null) {
      for (VirtualFile vendorDirectory : vendorDirectories) {
        VirtualFile shadowPackage = vendorDirectory.findFileByRelativePath(importPath);
        if (shadowPackage != null) {
          return !shadowPackage.equals(packageDirectory);
        }
      }
    }
    return false;
  }
}
