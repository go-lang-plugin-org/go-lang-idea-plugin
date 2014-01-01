// This is a generated file. Not intended for manual editing.
package com.goide.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.goide.GoTypes.*;
import com.goide.psi.*;

public class GoResultImpl extends GoCompositeElementImpl implements GoResult {

  public GoResultImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitResult(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoParameters getParameters() {
    return findChildByClass(GoParameters.class);
  }

  @Override
  @NotNull
  public List<GoType> getTypeList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoType.class);
  }

}
