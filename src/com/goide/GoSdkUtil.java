package com.goide;

import com.goide.psi.GoFile;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoSdkUtil {
  @Nullable
  public static VirtualFile getSdkHome(@NotNull PsiElement context) {
    Module module = ModuleUtilCore.findModuleForPsiElement(context);
    Sdk sdk = module == null ? null : ModuleRootManager.getInstance(module).getSdk();
    return sdk == null ? null : LocalFileSystem.getInstance().findFileByPath(sdk.getHomePath() + "/src/pkg");
  }

  @Nullable
  public static GoFile findBuiltinFile(@NotNull PsiElement context) {
    VirtualFile home = getSdkHome(context);
    VirtualFile vBuiltin = home != null ? home.findFileByRelativePath("builtin/builtin.go") : null;
    if (vBuiltin != null) {
      PsiFile psiBuiltin = PsiManager.getInstance(context.getProject()).findFile(vBuiltin);
      if (psiBuiltin instanceof GoFile) return ((GoFile)psiBuiltin);
    }
    return null;
  }
}
