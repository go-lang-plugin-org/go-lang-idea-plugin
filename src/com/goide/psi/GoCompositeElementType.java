package com.goide.psi;

import com.goide.GoLanguage;
import com.intellij.psi.tree.IElementType;

public class GoCompositeElementType extends IElementType {
  public GoCompositeElementType(String debug) {
    super(debug, GoLanguage.INSTANCE);
  }
}
