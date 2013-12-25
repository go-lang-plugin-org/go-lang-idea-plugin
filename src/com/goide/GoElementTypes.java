package com.goide;

import com.intellij.psi.tree.IFileElementType;

public interface GoElementTypes {
  IFileElementType FILE = new IFileElementType("GO_FILE", GoLanguage.INSTANCE);
}
