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

import com.goide.GoConstants;
import com.goide.project.GoExcludedPathsSettings;
import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.runconfig.testing.GoTestFinder;
import com.goide.sdk.GoPackageUtil;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.DelegatingGlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.ThreeState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoUtil {
  private static final String PLUGIN_ID = "ro.redeul.google.go";

  private GoUtil() {}

  public static boolean matchedForModuleBuildTarget(@NotNull PsiFile file, @Nullable Module module) {
    return module == null || new GoBuildMatcher(GoTargetSystem.forModule(module)).matchFile(file);
  }

  public static boolean isExcludedFile(@NotNull GoFile file) {
    return CachedValuesManager.getCachedValue(file, () -> {
      String importPath = file.getImportPath(false);
      GoExcludedPathsSettings excludedSettings = GoExcludedPathsSettings.getInstance(file.getProject());
      return CachedValueProvider.Result.create(importPath != null && excludedSettings.isExcluded(importPath), file, excludedSettings);
    });
  }

  @NotNull
  public static String systemOS() {
    // TODO android? dragonfly nacl? netbsd openbsd plan9
    if (SystemInfo.isMac) {
      return "darwin";
    }
    if (SystemInfo.isFreeBSD) {
      return "freebsd";
    }
    if (SystemInfo.isLinux) {
      return GoConstants.LINUX_OS;
    }
    if (SystemInfo.isSolaris) {
      return "solaris";
    }
    if (SystemInfo.isWindows) {
      return "windows";
    }
    return "unknown";
  }

  @NotNull
  public static String systemArch() {
    if (SystemInfo.is64Bit) {
      return GoConstants.AMD64;
    }
    if (SystemInfo.isWindows) {
      String arch = System.getenv("PROCESSOR_ARCHITECTURE");
      String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
      return arch.endsWith("64") || wow64Arch != null && wow64Arch.endsWith("64") ? GoConstants.AMD64 : "386";
    }
    if (SystemInfo.is32Bit) {
      return "386";
    }
    return "unknown";
  }

  @NotNull
  public static ThreeState systemCgo(@NotNull String os, @NotNull String arch) {
    return GoConstants.KNOWN_CGO.contains(os + "/" + arch) ? ThreeState.YES : ThreeState.NO;
  }

  public static boolean fileToIgnore(@NotNull String fileName) {
    return StringUtil.startsWithChar(fileName, '_') || StringUtil.startsWithChar(fileName, '.');
  }
  
  public static GlobalSearchScope goPathUseScope(@NotNull PsiElement context, boolean filterByImportList) {
    return GoPathUseScope.create(context, filterByImportList);
  }

  public static GlobalSearchScope goPathResolveScope(@NotNull PsiElement context) {
    // it's important to ask module on file, otherwise module won't be found for elements in libraries files [zolotov]
    Module module = ModuleUtilCore.findModuleForPsiElement(context.getContainingFile());
    return GoPathResolveScope.create(context.getProject(), module, context);
  }

  public static GlobalSearchScope goPathResolveScope(@NotNull Module module, @Nullable PsiElement context) {
    return GoPathResolveScope.create(module.getProject(), module, context);
  }

  @NotNull
  public static GlobalSearchScope moduleScope(@NotNull Module module) {
    return GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module).uniteWith(module.getModuleContentWithDependenciesScope());
  }

  @NotNull
  public static GlobalSearchScope moduleScopeWithoutLibraries(@NotNull Project project, @Nullable Module module) {
    return module != null ? GlobalSearchScope.moduleWithDependenciesScope(module).uniteWith(module.getModuleContentWithDependenciesScope())
                          : GlobalSearchScope.projectScope(project);
  }

  @NotNull
  @SuppressWarnings("ConstantConditions")
  public static IdeaPluginDescriptor getPlugin() {
    return PluginManager.getPlugin(PluginId.getId(PLUGIN_ID));
  }

  /**
   * isReferenceTo optimization. Before complex checking via resolve we can say for sure that element
   * can't be a reference to given declaration in following cases:<br/>
   * – Blank definitions can't be used as value, so this method return false for all named elements with '_' name<br/>
   * – GoLabelRef can't be resolved to anything but GoLabelDefinition<br/>
   * – GoTypeReferenceExpression (not from receiver type) can't be resolved to anything but GoTypeSpec or GoImportSpec<br/>
   * – Definition is private and reference in different package<br/>
   */
  public static boolean couldBeReferenceTo(@NotNull PsiElement definition, @NotNull PsiElement reference) {
    if (definition instanceof PsiDirectory && reference instanceof GoReferenceExpressionBase) return true;
    if (reference instanceof GoLabelRef && !(definition instanceof GoLabelDefinition)) return false;
    if (reference instanceof GoTypeReferenceExpression &&
        !(definition instanceof GoTypeSpec || definition instanceof GoImportSpec)) {
      return false;
    }

    PsiFile definitionFile = definition.getContainingFile();
    PsiFile referenceFile = reference.getContainingFile();
    // todo: zolotov, are you sure? cross refs, for instance?
    if (!(definitionFile instanceof GoFile) || !(referenceFile instanceof GoFile)) return false;

    boolean inSameFile = definitionFile.isEquivalentTo(referenceFile);
    if (inSameFile) return true;

    if (inSamePackage(referenceFile, definitionFile)) return true;
    return !(reference instanceof GoNamedElement && !((GoNamedElement)reference).isPublic());
  }

  public static boolean inSamePackage(@NotNull PsiFile firstFile, @NotNull PsiFile secondFile) {
    PsiDirectory containingDirectory = firstFile.getContainingDirectory();
    if (containingDirectory == null || !containingDirectory.equals(secondFile.getContainingDirectory())) {
      return false;
    }
    if (firstFile instanceof GoFile && secondFile instanceof GoFile) {
      String referencePackage = ((GoFile)firstFile).getPackageName();
      String definitionPackage = ((GoFile)secondFile).getPackageName();
      return referencePackage != null && referencePackage.equals(definitionPackage);
    }
    return true;
  }

  @NotNull
  public static String suggestPackageForDirectory(@Nullable PsiDirectory directory) {
    String packageName = GoPsiImplUtil.getLocalPackageName(directory != null ? directory.getName() : "");
    for (String p : GoPackageUtil.getAllPackagesInDirectory(directory, null, true)) {
      if (!GoConstants.MAIN.equals(p)) {
        return p;
      }
    }
    return packageName;
  }

  public static class ExceptTestsScope extends DelegatingGlobalSearchScope {
    public ExceptTestsScope(@NotNull GlobalSearchScope baseScope) {
      super(baseScope);
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
      return !GoTestFinder.isTestFile(file) && super.contains(file);
    }
  }
  
  public static class TestsScope extends DelegatingGlobalSearchScope {
    public TestsScope(@NotNull GlobalSearchScope baseScope) {
      super(baseScope);
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
      return GoTestFinder.isTestFile(file) && super.contains(file);
    }
  }

  public static class ExceptChildOfDirectory extends DelegatingGlobalSearchScope {
    @NotNull private final VirtualFile myParent;
    @Nullable private final String myAllowedPackageInExcludedDirectory;

    public ExceptChildOfDirectory(@NotNull VirtualFile parent,
                                  @NotNull GlobalSearchScope baseScope,
                                  @Nullable String allowedPackageInExcludedDirectory) {
      super(baseScope);
      myParent = parent;
      myAllowedPackageInExcludedDirectory = allowedPackageInExcludedDirectory;
    }

    @Override
    public boolean contains(@NotNull VirtualFile file) {
      if (myParent.equals(file.getParent())) {
        if (myAllowedPackageInExcludedDirectory == null) {
          return false;
        }
        Project project = getProject();
        PsiFile psiFile = project != null ? PsiManager.getInstance(project).findFile(file) : null;
        if (!(psiFile instanceof GoFile)) {
          return false;
        }
        if (!myAllowedPackageInExcludedDirectory.equals(((GoFile)psiFile).getPackageName())) {
          return false;
        }
      }
      return super.contains(file);
    }
  }
}