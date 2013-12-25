package com.goide.psi;

import com.goide.GoLanguage;
import com.intellij.psi.tree.IElementType;

public class GoTokenType extends IElementType {
  public GoTokenType(String debug) {
    super(debug, GoLanguage.INSTANCE);
  }
}
