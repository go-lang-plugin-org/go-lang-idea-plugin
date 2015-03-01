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

import com.goide.completion.GoCompletionUtil;
import com.intellij.codeInsight.completion.CompletionUtil;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class GoImportReference extends FileReference {
  public GoImportReference(@NotNull FileReferenceSet fileReferenceSet, TextRange range, int index, String text) {
    super(fileReferenceSet, range, index, text);
  }

  @Override
  protected Object createLookupItem(PsiElement candidate) {
    if (candidate instanceof PsiDirectory) {
      return GoCompletionUtil.createDirectoryLookupElement((PsiDirectory)candidate);
    }
    return super.createLookupItem(candidate);
  }

  @NotNull
  @Override
  protected ResolveResult[] innerResolve(boolean caseSensitive, @NotNull PsiFile file) {
    if (isFirst() && isLast() && "builtin".equals(getFileReferenceSet().getPathString())) {
      // import "builtin" can't be resolved
      return ResolveResult.EMPTY_ARRAY;
    }
    if (isFirst()) {
      if (".".equals(getCanonicalText())) {
        PsiDirectory directory = getDirectory();
        return directory != null ? new PsiElementResolveResult[]{new PsiElementResolveResult(directory)} : ResolveResult.EMPTY_ARRAY;
      }
      else if ("..".equals(getCanonicalText())) {
        PsiDirectory directory = getDirectory();
        PsiDirectory grandParent = directory != null ? directory.getParentDirectory() : null;
        return grandParent != null ? new PsiElementResolveResult[]{new PsiElementResolveResult(grandParent)} : ResolveResult.EMPTY_ARRAY;
      }
    }

    if (isLast()) {
      List<ResolveResult> filtered = ContainerUtil.filter(super.innerResolve(caseSensitive, file), new Condition<ResolveResult>() {
        @Override
        public boolean value(@NotNull ResolveResult resolveResult) {
          PsiElement element = resolveResult.getElement();
          return element != null && element instanceof PsiDirectory;
        }
      });
      return filtered.toArray(new ResolveResult[filtered.size()]);
    }
    return super.innerResolve(caseSensitive, file);
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    if (super.isReferenceTo(element)) {
      return true;
    }

    if (element instanceof PsiDirectoryContainer) {
      for (PsiDirectory directory : ((PsiDirectoryContainer)element).getDirectories()) {
        if (super.isReferenceTo(directory)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public PsiElement bindToElement(@NotNull PsiElement element, boolean absolute) throws IncorrectOperationException {
    if (!absolute) {
      FileReference firstReference = ArrayUtil.getFirstElement(getFileReferenceSet().getAllReferences());
      if (firstReference != null) {
        Collection<PsiFileSystemItem> contexts = getFileReferenceSet().getDefaultContexts();
        for (ResolveResult resolveResult : firstReference.multiResolve(false)) {
          PsiElement resolveResultElement = resolveResult.getElement();
          if (resolveResultElement != null && resolveResultElement instanceof PsiFileSystemItem) {
            PsiFileSystemItem parentDirectory = ((PsiFileSystemItem)resolveResultElement).getParent();
            if (parentDirectory != null && contexts.contains(parentDirectory)) {
              return getElement();
            }
          }
        }
      }
    }
    return super.bindToElement(element, absolute);
  }

  private boolean isFirst() {
    return getIndex() <= 0;
  }

  @Nullable
  private PsiDirectory getDirectory() {
    PsiElement originalElement = CompletionUtil.getOriginalElement(getElement());
    PsiFile file = originalElement != null ? originalElement.getContainingFile() : getElement().getContainingFile();
    return file.getParent();
  }
}
