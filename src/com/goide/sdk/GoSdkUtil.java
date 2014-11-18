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
  public static VirtualFile getSdkHome(@NotNull PsiElement context) {
    Module module = ModuleUtilCore.findModuleForPsiElement(context);
    Sdk sdk = module == null ? null : ModuleRootManager.getInstance(module).getSdk();
    VirtualFile result = sdk == null ? null : LocalFileSystem.getInstance().findFileByPath(sdk.getHomePath() + "/src/pkg");
    return result != null ? result : guessSkdHome(context);
  }

  @Nullable
  public static GoFile findBuiltinFile(@NotNull PsiElement context) {
    VirtualFile home = getSdkHome(context);
    VirtualFile vBuiltin = home != null ? home.findFileByRelativePath("builtin/builtin.go") : null;
    if (vBuiltin != null) {
      PsiFile psiBuiltin = context.getManager().findFile(vBuiltin);
      if (psiBuiltin instanceof GoFile) return ((GoFile)psiBuiltin);
    }
    return null;
  }

  @Nullable
  private static VirtualFile guessSkdHome(@NotNull PsiElement context) {
    VirtualFile virtualFile = context.getContainingFile().getOriginalFile().getVirtualFile();
    return ProjectRootManager.getInstance(context.getProject()).getFileIndex().getClassRootForFile(virtualFile);
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
}
