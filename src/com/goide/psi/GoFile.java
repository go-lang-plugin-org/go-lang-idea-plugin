package com.goide.psi;

import com.goide.GoFileType;
import com.goide.GoLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

public class GoFile extends PsiFileBase {
  public GoFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, GoLanguage.GO);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return GoFileType.INSTANCE;
  }
}
