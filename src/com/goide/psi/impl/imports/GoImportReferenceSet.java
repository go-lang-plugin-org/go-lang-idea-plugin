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

package com.goide.psi.impl.imports;

import com.goide.project.GoVendoringUtil;
import com.goide.psi.GoImportString;
import com.goide.sdk.GoSdkUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.Function;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class GoImportReferenceSet extends FileReferenceSet {
  public GoImportReferenceSet(@NotNull GoImportString importString) {
    super(importString.getPath(), importString, importString.getPathTextRange().getStartOffset(), null, true);
  }

  @NotNull
  @Override
  public Collection<PsiFileSystemItem> computeDefaultContexts() {
    PsiFile file = getContainingFile();
    if (file == null || !file.isValid()) {
      return Collections.emptyList();
    }

    PsiManager psiManager = file.getManager();
    Module module = ModuleUtilCore.findModuleForPsiElement(file);
    Collection<VirtualFile> sourcesPaths = GoSdkUtil.getSourcesPathsToLookup(file.getProject(), module);
    Collection<PsiFileSystemItem> result = ContainerUtil.newArrayList();
    if (GoVendoringUtil.isVendoringEnabled(module)) {
      GoSdkUtil.processVendorDirectories(file, sourcesPaths, new Processor<VirtualFile>() {
        @Override
        public boolean process(VirtualFile file) {
          ContainerUtil.addIfNotNull(result, psiManager.findDirectory(file));
          return true;
        }
      });
    }

    result.addAll(ContainerUtil.mapNotNull(sourcesPaths,
                                           new Function<VirtualFile, PsiFileSystemItem>() {
                                             @Nullable
                                             @Override
                                             public PsiFileSystemItem fun(VirtualFile file) {
                                               return psiManager.findDirectory(file);
                                             }
                                           }));
    return result;
  }

  @Override
  protected Condition<PsiFileSystemItem> getReferenceCompletionFilter() {
    if (!isRelativeImport()) {
      return Conditions.alwaysFalse();
    }
    return super.getReferenceCompletionFilter();
  }

  @Nullable
  @Override
  public PsiFileSystemItem resolve() {
    return isAbsolutePathReference() ? null : super.resolve();
  }

  @Override
  public boolean absoluteUrlNeedsStartSlash() {
    return false;
  }

  @Override
  public boolean isEndingSlashNotAllowed() {
    return !isRelativeImport();
  }

  @NotNull
  @Override
  public FileReference createFileReference(TextRange range, int index, String text) {
    return new GoImportReference(this, range, index, text);
  }

  public boolean isRelativeImport() {
    return getPathString().startsWith("./") || getPathString().startsWith("../");
  }

  @Override
  public boolean couldBeConvertedTo(boolean relative) {
    return false;
  }
}
