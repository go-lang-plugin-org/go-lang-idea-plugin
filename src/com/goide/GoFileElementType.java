package com.goide;

import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;

public class GoFileElementType extends IStubFileElementType {
  public static final IFileElementType INSTANCE = new GoFileElementType();

  public GoFileElementType() {
    super("GO_FILE", GoLanguage.INSTANCE);
  }
}
