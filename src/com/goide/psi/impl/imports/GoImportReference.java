package com.goide.psi.impl.imports;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoImportReference extends FileReference {
  public GoImportReference(@NotNull FileReferenceSet fileReferenceSet,
                           TextRange range, int index, String text) {
    super(fileReferenceSet, range, index, text);
  }

  @NotNull
  @Override
  protected ResolveResult[] innerResolve(boolean caseSensitive) {
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
}
