package com.goide.psi.impl;

import com.goide.psi.GoCompositeElement;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class GoCompositeElementImpl extends CompositePsiElement implements GoCompositeElement {
  public GoCompositeElementImpl(@NotNull IElementType type) {
    super(type);
  }
}
