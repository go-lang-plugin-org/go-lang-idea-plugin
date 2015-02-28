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
import com.goide.project.GoLibrariesService;
import com.goide.psi.GoFile;
import com.goide.util.GoExecutor;
import com.google.common.collect.Lists;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.Function;
import com.intellij.util.ObjectUtils;
import com.intellij.util.SystemProperties;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoSdkUtil {

  private static final String GO_VERSION_PATTERN = "go version go([\\d.]+)";
  private static final String GO_DEVEL_VERSION_PATTERN = "go version (devel[\\d.]+)";

  @Nullable
  public static VirtualFile getSdkSrcDir(@NotNull PsiElement context) {
    Module module = ModuleUtilCore.findModuleForPsiElement(context);
    String sdkHomePath = GoSdkService.getInstance(context.getProject()).getSdkHomePath(module);
    String sdkVersionString = GoSdkService.getInstance(context.getProject()).getSdkVersion(module);
    VirtualFile sdkSrcDir = null;
    if (sdkHomePath != null && sdkVersionString != null) {
      File sdkSrcDirFile = new File(sdkHomePath, getSrcLocation(sdkVersionString));
      sdkSrcDir = LocalFileSystem.getInstance().findFileByIoFile(sdkSrcDirFile);
    }
    return sdkSrcDir != null ? sdkSrcDir : guessSkdSrcDir(context);
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
  public static String retrieveGoPath(@NotNull Project project) {
    Collection<String> parts = ContainerUtil.newLinkedHashSet();
    ContainerUtil.addIfNotNull(parts, GoEnvironmentUtil.retrieveGoPathFromEnvironment());
    ContainerUtil.addAll(parts, ContainerUtil.map(GoLibrariesService.getUserDefinedLibraries(project), new Function<VirtualFile, String>() {
      @Override
      public String fun(VirtualFile file) {
        return file.getPath();
      }
    }));
    return StringUtil.join(parts, File.pathSeparator);
  }

  @NotNull
  public static String getSrcLocation(@NotNull String version) {
    if (version.contains("devel")) {
      return "src";
    }
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
  public static VirtualFile findDirectoryByImportPath(@NotNull String importPath, @NotNull Module module) {
    for (VirtualFile root : getGoPathsSources(module)) {
      VirtualFile directory = root.findFileByRelativePath(importPath);
      if (directory != null && directory.isDirectory()) {
        return directory;
      }
    }
    return null;
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

  @Nullable
  public static VirtualFile suggestSdkDirectory() {
    if (SystemInfo.isWindows) {
      return ObjectUtils.chooseNotNull(LocalFileSystem.getInstance().findFileByPath("C:\\Go"),
                                       LocalFileSystem.getInstance().findFileByPath("C:\\cygwin"));
    }
    if (SystemInfo.isMac || SystemInfo.isLinux) {
      String fromEnv = suggestSdkDirectoryPathFromEnv();
      if (fromEnv != null) {
        return LocalFileSystem.getInstance().findFileByPath(fromEnv);
      }
      return LocalFileSystem.getInstance().findFileByPath("/usr/local/go");
    }
    if (SystemInfo.isMac) {
      String macPorts = "/opt/local/lib/go";
      return LocalFileSystem.getInstance().findFileByPath(macPorts);
    }
    return null;
  }

  @Nullable
  private static String suggestSdkDirectoryPathFromEnv() {
    File fileFromPath = PathEnvironmentVariableUtil.findInPath("go");
    if (fileFromPath != null) {
      File canonicalFile;
      try {
        canonicalFile = fileFromPath.getCanonicalFile();
        String path = canonicalFile.getPath();
        if (path.endsWith("bin/go")) {
          return StringUtil.trimEnd(path, "bin/go");
        }
      }
      catch (IOException ignore) {
      }
    }
    return null;
  }

  @Nullable
  public static String retrieveGoVersion(@NotNull final String sdkPath) {
    ProcessOutput output = new ProcessOutput();
    if (new GoExecutor().withGoRoot(sdkPath).withProcessOutput(output).execute(false, "version")) {
      Matcher matcher = Pattern.compile(GO_VERSION_PATTERN).matcher(output.getStdout());
      if (matcher.find()) {
        return matcher.group(1);
      }
      matcher = Pattern.compile(GO_DEVEL_VERSION_PATTERN).matcher(output.getStdout());
      if (matcher.find()) {
        return matcher.group(1);
      }
    }
    return null;
  }

  public static boolean isAppEngineSdkPath(@Nullable String path) {
    return path != null && new File(path, "appcfg.py").exists();
  }

  @NotNull
  public static String adjustSdkPath(@NotNull String path) {
    return isAppEngineSdkPath(path) ? path + "/" + "goroot" : path;
  }

  @NotNull
  public static Collection<VirtualFile> getSdkDirectoriesToAttach(@NotNull String sdkPath, @NotNull String versionString) {
    String srcPath = getSrcLocation(versionString);
    // scr is enough at the moment, possible process binaries from pkg
    VirtualFile src = VirtualFileManager.getInstance().findFileByUrl(VfsUtilCore.pathToUrl(FileUtil.join(sdkPath, srcPath)));
    if (src != null && src.isDirectory()) {
      return Collections.singletonList(src);
    }
    return Collections.emptyList();
  }
}
