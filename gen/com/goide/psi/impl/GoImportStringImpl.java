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
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiReference;

public class GoImportStringImpl extends GoCompositeElementImpl implements GoImportString {

  public GoImportStringImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull GoVisitor visitor) {
    visitor.visitImportString(this);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GoVisitor) accept((GoVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public PsiElement getRawString() {
    return findChildByType(RAW_STRING);
  }

  @Override
  @Nullable
  public PsiElement getString() {
    return findChildByType(STRING);
  }

  @NotNull
  public PsiReference[] getReferences() {
    return GoPsiImplUtil.getReferences(this);
  }

  @Nullable
  public PsiDirectory resolve() {
    return GoPsiImplUtil.resolve(this);
  }

  @NotNull
  public String getPath() {
    return GoPsiImplUtil.getPath(this);
  }

  @NotNull
  public TextRange getPathTextRange() {
    return GoPsiImplUtil.getPathTextRange(this);
  }

}
