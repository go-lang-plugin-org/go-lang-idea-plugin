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

import com.goide.psi.GoFile;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.PathMacros;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.EnvironmentUtil;
import com.intellij.util.SystemProperties;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class GoSdkUtil {

  public static final String GOPATH = "GOPATH";

  @Nullable
  public static VirtualFile getSdkSrcDir(@NotNull PsiElement context) {
    final Module module = ModuleUtilCore.findModuleForPsiElement(context);
    if (module == null) {
      return null;
    }
    final Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
    if (sdk == null || sdk.getVersionString() == null) {
      return null;
    }
    final File sdkSrcDirFile = new File(sdk.getHomePath(), getSrcLocation(sdk.getVersionString()));
    final VirtualFile sdkSrcDir = LocalFileSystem.getInstance().findFileByIoFile(sdkSrcDirFile);
    return sdkSrcDir != null ? sdkSrcDir : guessSkdSrcDir(context);
  }

  @Nullable
  public static GoFile findBuiltinFile(@NotNull PsiElement context) {
    final VirtualFile sdkSrcDir = getSdkSrcDir(context);
    if (sdkSrcDir == null) {
      return null;
    }
    final VirtualFile vBuiltin = sdkSrcDir.findFileByRelativePath("builtin/builtin.go");
    if (vBuiltin == null) {
      return null;
    }
    final PsiFile psiBuiltin = context.getManager().findFile(vBuiltin);
    return (psiBuiltin instanceof GoFile) ? (GoFile)psiBuiltin : null;
  }

  @NotNull
  public static List<VirtualFile> getGoPathsSources() {
    List<VirtualFile> result = ContainerUtil.newArrayList();
    String gopath = retrieveGoPath();
    if (gopath != null) {
      List<String> split = StringUtil.split(gopath, File.pathSeparator);
      String home = SystemProperties.getUserHome();
      for (String s : split) {
        if (home != null) {
          s = s.replaceAll("\\$HOME", home);
        }
        VirtualFile path = LocalFileSystem.getInstance().findFileByPath(s + "/src");
        ContainerUtil.addIfNotNull(result, path);
      }
    }
    return result;
  }

  @Nullable
  public static String retrieveGoPath() {
    String path = EnvironmentUtil.getValue(GOPATH);
    return path != null ? path : PathMacros.getInstance().getValue(GOPATH);
  }

  @NotNull
  public static String getSrcLocation(@NotNull String version) {
    if (compareVersions(version, "1.4") < 0) {
      return "src/pkg";
    }
    return "src";
  }

  public static int compareVersions(@NotNull String lhs, @NotNull String rhs) {
    final List<Integer> lhsParts = parseVersionString(lhs);
    final List<Integer> rhsParts = parseVersionString(rhs);
    final int commonParts = Math.min(lhsParts.size(), rhsParts.size());
    for (int i = 0; i < commonParts; i++) {
      final int partResult = lhsParts.get(i).compareTo(rhsParts.get(i));
      if (partResult != 0) {
        return partResult;
      }
    }
    return Integer.valueOf(lhsParts.size()).compareTo(Integer.valueOf(rhsParts.size()));
  }

  @NotNull
  private static List<Integer> parseVersionString(@NotNull String versionStr) {
    final String[] strParts = versionStr.split("\\.");
    final List<Integer> parts = Lists.newArrayListWithExpectedSize(strParts.length);
    for (String strPart : strParts) {
      parts.add(Integer.valueOf(strPart));
    }
    return parts;
  }

  @Nullable
  private static VirtualFile guessSkdSrcDir(@NotNull PsiElement context) {
    VirtualFile virtualFile = context.getContainingFile().getOriginalFile().getVirtualFile();
    return ProjectRootManager.getInstance(context.getProject()).getFileIndex().getClassRootForFile(virtualFile);
  }
}
