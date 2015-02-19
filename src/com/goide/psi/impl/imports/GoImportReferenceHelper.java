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

package com.goide.psi.impl.imports;

import com.goide.codeInsight.imports.GoGetPackageFix;
import com.goide.psi.GoFile;
import com.goide.sdk.GoSdkUtil;
import com.intellij.codeInsight.daemon.quickFix.CreateFileFix;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.openapi.project.Project;
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
import java.util.Set;

public class GoImportReferenceHelper extends FileReferenceHelper {
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
      result.add(new CreateFileFix(true, fileNameToCreate, (PsiDirectory)context));
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
      @Nullable
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

  @NotNull
  private static Collection<? extends VirtualFile> getPathsToLookup(@NotNull PsiElement element) {
    Set<VirtualFile> result = ContainerUtil.newLinkedHashSet(GoSdkUtil.getGoPathsSources(element));
    ContainerUtil.addIfNotNull(result, GoSdkUtil.getSdkSrcDir(element));
    return result;
  }
}
