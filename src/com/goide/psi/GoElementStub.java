package com.goide.psi;

import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class GoElementStub extends CompositePsiElement implements GoElement {
  public GoElementStub(@NotNull IElementType type) {
    super(type);
  }
}
