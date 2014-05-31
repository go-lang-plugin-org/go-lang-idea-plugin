package com.goide.psi;

import com.goide.GoLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class GoTokenType extends IElementType {
  public GoTokenType(@NotNull String debug) {
    super(debug, GoLanguage.INSTANCE);
  }
}
