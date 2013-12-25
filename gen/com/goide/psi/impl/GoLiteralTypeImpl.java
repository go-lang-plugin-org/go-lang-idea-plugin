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

public class GoLiteralTypeImpl extends GoCompositeElementImpl implements GoLiteralType {

  public GoLiteralTypeImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) ((GoVisitor)visitor).visitLiteralType(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public GoArrayType getArrayType() {
    return findChildByClass(GoArrayType.class);
  }

  @Override
  @Nullable
  public GoElementType getElementType() {
    return findChildByClass(GoElementType.class);
  }

  @Override
  @Nullable
  public GoMapType getMapType() {
    return findChildByClass(GoMapType.class);
  }

  @Override
  @Nullable
  public GoSliceType getSliceType() {
    return findChildByClass(GoSliceType.class);
  }

  @Override
  @Nullable
  public GoStructType getStructType() {
    return findChildByClass(GoStructType.class);
  }

  @Override
  @Nullable
  public GoTypeName getTypeName() {
    return findChildByClass(GoTypeName.class);
  }

}
