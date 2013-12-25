package com.goide;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class GoElementType extends IElementType {
  public GoElementType(@NotNull String debugName) {
    super(debugName, GoLanguage.GO);
  }
}
