/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

import com.goide.GoEnvironmentUtil;
import com.goide.GoModuleType;
import com.goide.project.GoLibrariesService;
import com.goide.psi.GoFile;
import com.google.common.collect.Lists;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.Function;
import com.intellij.util.PlatformUtils;
import com.intellij.util.SystemProperties;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GoSdkUtil {
  @Nullable
  public static VirtualFile getSdkSrcDir(@NotNull PsiElement context) {
    Sdk sdk = getSdk(context);
    if (sdk == null || sdk.getVersionString() == null) return guessSkdSrcDir(context);
    File sdkSrcDirFile = new File(sdk.getHomePath(), getSrcLocation(sdk.getVersionString()));
    VirtualFile sdkSrcDir = LocalFileSystem.getInstance().findFileByIoFile(sdkSrcDirFile);
    return sdkSrcDir != null ? sdkSrcDir : guessSkdSrcDir(context);
  }

  @Nullable
  public static Sdk getSdk(@NotNull PsiElement context) {
    Module module = ModuleUtilCore.findModuleForPsiElement(context);
    Sdk sdk = module == null ? null : ModuleRootManager.getInstance(module).getSdk();
    sdk = sdk == null ? ProjectRootManager.getInstance(context.getProject()).getProjectSdk() : sdk;
    if (sdk == null || sdk.getVersionString() == null) return null;
    if (sdk.getSdkType() instanceof GoSdkType) return sdk;
    return null;
  }

  @Nullable
  public static GoFile findBuiltinFile(@NotNull PsiElement context) {
    VirtualFile sdkSrcDir = getSdkSrcDir(context);
    if (sdkSrcDir == null) return null;
    VirtualFile vBuiltin = sdkSrcDir.findFileByRelativePath("builtin/builtin.go");
    if (vBuiltin == null) return null;
    PsiFile psiBuiltin = context.getManager().findFile(vBuiltin);
    return (psiBuiltin instanceof GoFile) ? (GoFile)psiBuiltin : null;
  }
  
  @NotNull
  public static Collection<VirtualFile> getGoPathsSources(@NotNull PsiElement context) {
    final Module module = ModuleUtilCore.findModuleForPsiElement(context);
    return module != null ? getGoPathsSources(module) : getGoPathsSources(context.getProject());
  }

  @NotNull
  public static Collection<VirtualFile> getGoPathsSources(@NotNull Module module) {
    final Collection<VirtualFile> result = getGoPathsSourcesFromEnvironment();
    for (VirtualFile file : GoLibrariesService.getUserDefinedLibraries(module)) {
      ContainerUtil.addIfNotNull(result, findSourceDirectory(file));
    }
    return result;
  }

  @NotNull
  public static Collection<VirtualFile> getGoPathsSources(@NotNull Project project) {
    final Collection<VirtualFile> result = getGoPathsSourcesFromEnvironment();
    for (VirtualFile file : GoLibrariesService.getUserDefinedLibraries(project)) {
      ContainerUtil.addIfNotNull(result, findSourceDirectory(file));
    }
    return result;
  }

  /**
   * Retrieves source directories from GOPATH env-variable. 
   * This method doesn't consider user defined libraries, 
   * for that case use {@link this#getGoPathsSources(PsiElement)} or {@link this#getGoPathsSources(Module)} or {@link this#getGoPathsSources(Project)}
   */
  @NotNull
  public static Collection<VirtualFile> getGoPathsSourcesFromEnvironment() {
    Set<VirtualFile> result = ContainerUtil.newLinkedHashSet();
    String goPath = GoEnvironmentUtil.retrieveGoPathFromEnvironment();
    if (goPath != null) {
      String home = SystemProperties.getUserHome();
      for (String s : StringUtil.split(goPath, File.pathSeparator)) {
        if (home != null) {
          s = s.replaceAll("\\$HOME", home);
        }
        ContainerUtil.addIfNotNull(result, findSourceDirectory(LocalFileSystem.getInstance().findFileByPath(s)));
      }
    }
    return result;
  }

  @Nullable
  private static VirtualFile findSourceDirectory(@Nullable VirtualFile file) {
    return file == null || FileUtil.namesEqual("src", file.getName()) ? file : file.findChild("src");
  }

  @NotNull
  public static String retrieveGoPath(@NotNull Module module) {
    Collection<String> parts = ContainerUtil.newLinkedHashSet();
    ContainerUtil.addIfNotNull(parts, GoEnvironmentUtil.retrieveGoPathFromEnvironment());
    ContainerUtil.addAll(parts, ContainerUtil.map(GoLibrariesService.getUserDefinedLibraries(module), new Function<VirtualFile, String>() {
      @Override
      public String fun(VirtualFile file) {
        return file.getPath();
      }
    }));
    return StringUtil.join(parts, File.pathSeparator);
  }

  @NotNull
  public static String getSrcLocation(@NotNull String version) {
    return compareVersions(version, "1.4") < 0 ? "src/pkg" : "src";
  }

  public static int compareVersions(@NotNull String lhs, @NotNull String rhs) {
    List<Integer> lhsParts = parseVersionString(lhs);
    List<Integer> rhsParts = parseVersionString(rhs);
    int commonParts = Math.min(lhsParts.size(), rhsParts.size());
    for (int i = 0; i < commonParts; i++) {
      int partResult = Comparing.compare(lhsParts.get(i), rhsParts.get(i));
      if (partResult != 0) {
        return partResult;
      }
    }
    return Comparing.compare(lhsParts.size(), rhsParts.size());
  }

  @NotNull
  private static List<Integer> parseVersionString(@NotNull String versionStr) {
    String[] strParts = StringUtil.trim(versionStr).split("\\.");
    List<Integer> parts = Lists.newArrayListWithExpectedSize(strParts.length);
    for (String strPart : strParts) {
      parts.add(StringUtil.parseInt(strPart, 0));
    }
    return parts;
  }

  @Nullable
  private static VirtualFile guessSkdSrcDir(@NotNull PsiElement context) {
    VirtualFile virtualFile = context.getContainingFile().getOriginalFile().getVirtualFile();
    return ProjectRootManager.getInstance(context.getProject()).getFileIndex().getClassRootForFile(virtualFile);
  }

  @Nullable
  public static String getPathRelativeToSdkAndLibraries(@NotNull VirtualFile file, @NotNull PsiElement context) {
    VirtualFile sdkSourceDir = getSdkSrcDir(context);
    Collection<VirtualFile> roots = ContainerUtil.newLinkedHashSet(getGoPathsSources(context));
    ContainerUtil.addIfNotNull(roots, sdkSourceDir);
    
    for (VirtualFile root : roots) {
      String relativePath = VfsUtilCore.getRelativePath(file, root, '/');
      if (StringUtil.isNotEmpty(relativePath)) {
        return relativePath;
      }
    }
    return null;
  }

  /**
   * Use this method in order to check whether the method is appropriate for providing Go-specific code insight
   */
  public static boolean isAppropriateModule(@NotNull Module module) {
    return !module.isDisposed() && (!PlatformUtils.isIntelliJ() || ModuleUtil.getModuleType(module) == GoModuleType.getInstance());
  }
}
