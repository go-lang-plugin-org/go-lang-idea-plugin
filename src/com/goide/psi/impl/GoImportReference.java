package com.goide.psi.impl;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GoImportReference extends GoReferenceBase {
  public GoImportReference(PsiElement element, TextRange range) {
    super(element, range);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    return resolvePackage(StringUtil.unquoteString(getElement().getText()));
  }

  @NotNull
  public List<LookupElement> complete(@NotNull String str) {
    if (!str.isEmpty() && !str.endsWith("/")) return Collections.emptyList();
    PsiManager instance = PsiManager.getInstance(getElement().getProject());
    List<LookupElement> result = ContainerUtil.newArrayList();
    for (VirtualFile base : getPathsToLookup()) {
      VirtualFile ch = str.isEmpty() ? base : base.findFileByRelativePath(str);
      if (ch == null) continue;
      for (VirtualFile vf : ch.getChildren()) {
        PsiDirectory dir = instance.findDirectory(vf);
        if (dir != null) {
          result.add(GoPsiImplUtil.createDirectoryLookupElement(dir));
        }
      }
    }
    return result;
  }


  @NotNull
  @Override
  public Object[] getVariants() {
    return new Object[0];
  }

  @NotNull
  @Override
  public PsiElement getIdentifier() {
    return getElement();
  }
}
