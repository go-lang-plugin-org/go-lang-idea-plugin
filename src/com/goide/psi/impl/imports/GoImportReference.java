package com.goide.psi.impl.imports;

import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.codeInsight.completion.CompletionUtil;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoImportReference extends FileReference {
  public GoImportReference(@NotNull FileReferenceSet fileReferenceSet, TextRange range, int index, String text) {
    super(fileReferenceSet, range, index, text);
  }

  @Override
  protected Object createLookupItem(PsiElement candidate) {
    if (candidate instanceof PsiDirectory) {
      return GoPsiImplUtil.createDirectoryLookupElement((PsiDirectory)candidate);
    }
    return super.createLookupItem(candidate);
  }

  @NotNull
  @Override
  protected ResolveResult[] innerResolve(boolean caseSensitive) {
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
      List<ResolveResult> filtered = ContainerUtil.filter(super.innerResolve(caseSensitive), new Condition<ResolveResult>() {
        @Override
        public boolean value(ResolveResult resolveResult) {
          PsiElement element = resolveResult.getElement();
          return element != null && element instanceof PsiDirectory;
        }
      });
      return filtered.toArray(new ResolveResult[filtered.size()]);
    }
    return super.innerResolve(caseSensitive);
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    if (super.isReferenceTo(element)) {
      return true;
    }

    if (element instanceof PsiPackage) {
      for (PsiDirectory directory : ((PsiPackage)element).getDirectories()) {
        if (super.isReferenceTo(directory)) {
          return true;
        }
      }
    }
    return false;
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
