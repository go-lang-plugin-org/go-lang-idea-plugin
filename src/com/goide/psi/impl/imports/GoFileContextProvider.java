package com.goide.psi.impl.imports;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileContextProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class GoFileContextProvider extends FileContextProvider {
  @Override
  protected boolean isAvailable(PsiFile file) {
    VirtualFile virtualFile = file.getVirtualFile();
    return virtualFile != null && new GoImportReferenceHelper().isMine(file.getProject(), virtualFile);
  }

  @NotNull
  @Override
  public Collection<PsiFileSystemItem> getContextFolders(PsiFile file) {
    VirtualFile virtualFile = file.getVirtualFile();
    return virtualFile != null
           ? new GoImportReferenceHelper().getContexts(file.getProject(), virtualFile)
           : Collections.<PsiFileSystemItem>emptyList();
  }

  @Nullable
  @Override
  public PsiFile getContextFile(PsiFile file) {
    return null;
  }
}
