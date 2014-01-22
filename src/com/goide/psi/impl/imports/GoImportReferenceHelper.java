package com.goide.psi.impl.imports;

import com.goide.GoSdkType;
import com.goide.psi.GoFile;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceHelper;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GoImportReferenceHelper extends FileReferenceHelper {
  @NotNull
  @Override
  public Collection<PsiFileSystemItem> getContexts(final Project project, @NotNull VirtualFile file) {
    PsiFileSystemItem psiFile = getPsiFileSystemItem(project, file);
    if (psiFile == null) {
      return Collections.emptyList();
    }
    Collection<PsiFileSystemItem> result = ContainerUtil.newArrayList();
    ContainerUtil.addAllNotNull(result, ContainerUtil.map(getPathsToLookup(psiFile), new Function<VirtualFile, PsiFileSystemItem>() {
      @Override
      public PsiFileSystemItem fun(VirtualFile file) {
        return getPsiFileSystemItem(project, file);
      }
    }));
    return result;
  }

  @Override
  public boolean isMine(Project project, @NotNull VirtualFile file) {
    PsiFileSystemItem psiFile = getPsiFileSystemItem(project, file);
    return psiFile != null && psiFile instanceof GoFile;
  }

  @Nullable
  public static VirtualFile getSdkHome(@NotNull PsiElement element) {
    Module module = ModuleUtilCore.findModuleForPsiElement(element);
    Sdk sdk = module == null ? null : ModuleRootManager.getInstance(module).getSdk();
    return sdk == null ? null : LocalFileSystem.getInstance().findFileByPath(sdk.getHomePath() + "/src/pkg");
  }

  @NotNull
  public static List<VirtualFile> getPathsToLookup(@NotNull PsiElement element) {
    List<VirtualFile> result = ContainerUtil.newArrayList();
    VirtualFile sdkHome = getSdkHome(element);
    ContainerUtil.addIfNotNull(result, sdkHome);
    result.addAll(GoSdkType.getGoPathsSources());
    return result;
  }
}
