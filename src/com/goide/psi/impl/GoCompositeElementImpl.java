package com.goide.psi.impl;

import com.goide.psi.GoCompositeElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.extapi.psi.PsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.CompositePsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class GoCompositeElementImpl extends ASTWrapperPsiElement implements GoCompositeElement {
  public GoCompositeElementImpl(@NotNull ASTNode node) {
    super(node);
  }
}
