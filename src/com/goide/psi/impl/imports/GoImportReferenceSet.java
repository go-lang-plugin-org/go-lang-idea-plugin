package com.goide.psi.impl.imports;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.openapi.util.text.StringUtil.stripQuotesAroundValue;

public class GoImportReferenceSet extends FileReferenceSet {
  public GoImportReferenceSet(@NotNull PsiElement element) {
    super(stripQuotesAroundValue(element.getText()), element, 1, null, true);
  }

  @Override
  protected Condition<PsiFileSystemItem> getReferenceCompletionFilter() {
    return DIRECTORY_FILTER;
  }

  @Nullable
  @Override
  public PsiFileSystemItem resolve() {
    if (isAbsolutePathReference()) {
      return null;
    }

    return super.resolve();
  }

  @Override
  public boolean absoluteUrlNeedsStartSlash() {
    return false;
  }

  @Override
  public FileReference createFileReference(TextRange range, int index, String text) {
    return new GoImportReference(this, range, index, text);
  }
}
