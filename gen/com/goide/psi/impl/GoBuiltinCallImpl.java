// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import com.goide.psi.GoBuiltinArgs;
import com.goide.psi.GoBuiltinCall;
import com.goide.psi.GoVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.GoTypes.IDENTIFIER;

public class GoBuiltinCallImpl extends GoCompositeElementImpl implements GoBuiltinCall {

  public GoBuiltinCallImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitBuiltinCall(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoBuiltinArgs getBuiltinArgs() {
    return findChildByClass(GoBuiltinArgs.class);
  }

  @Override
  @NotNull
  public PsiElement getIdentifier() {
    return findNotNullChildByType(IDENTIFIER);
  }

}
