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

public class GoFieldDeclarationImpl extends GoCompositeElementImpl implements GoFieldDeclaration {

  public GoFieldDeclarationImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitFieldDeclaration(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoAnonymousFieldDefinition getAnonymousFieldDefinition() {
    return findChildByClass(GoAnonymousFieldDefinition.class);
  }

  @Override
  @NotNull
  public List<GoFieldDefinition> getFieldDefinitionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, GoFieldDefinition.class);
  }

  @Override
  @Nullable
  public GoTag getTag() {
    return findChildByClass(GoTag.class);
  }

  @Override
  @Nullable
  public GoType getType() {
    return findChildByClass(GoType.class);
  }

}
