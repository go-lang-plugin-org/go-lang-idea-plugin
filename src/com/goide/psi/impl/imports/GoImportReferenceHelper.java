package com.goide.psi.impl.imports;

import com.goide.GoSdkType;
import com.goide.psi.GoFile;
import com.intellij.codeInsight.daemon.quickFix.CreateFileFix;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceHelper;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.Function;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GoImportReferenceHelper extends FileReferenceHelper {
  @NotNull
  @Override
  public Collection<PsiFileSystemItem> getRoots(@NotNull Module module) {
    return super.getRoots(module);
  }

  @NotNull
  @Override
  public List<? extends LocalQuickFix> registerFixes(FileReference reference) {
    LocalQuickFix goGetFix = new GoGetPackageFix(reference.getFileReferenceSet().getPathString());
    List<LocalQuickFix> result = ContainerUtil.newArrayList(goGetFix);
    int index = reference.getIndex();
    if (!(reference instanceof GoImportReference) || !reference.isLast() || index < 0) {
      return result;
    }

    FileReferenceSet referenceSet = reference.getFileReferenceSet();
    PsiFileSystemItem context;
    if (index > 0) {
      context = referenceSet.getReference(index - 1).resolve();
    }
    else {
      context = ContainerUtil.getFirstItem(referenceSet.getDefaultContexts());
    }

    String fileNameToCreate = reference.getFileNameToCreate();
    if (context == null || !(context instanceof PsiDirectory)) {
      return result;
    }

    try {
      ((PsiDirectory)context).checkCreateSubdirectory(fileNameToCreate);
      result.add(0, new CreateFileFix(true, fileNameToCreate, (PsiDirectory)context));
    }
    catch (IncorrectOperationException ignore) {
    }
    return result;
  }

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
  private static List<VirtualFile> getPathsToLookup(@NotNull PsiElement element) {
    List<VirtualFile> result = ContainerUtil.newArrayList();
    VirtualFile sdkHome = getSdkHome(element);
    ContainerUtil.addIfNotNull(result, sdkHome);
    result.addAll(GoSdkType.getGoPathsSources());
    return result;
  }
}
