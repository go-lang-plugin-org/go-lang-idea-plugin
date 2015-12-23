// This is a generated file. Not intended for manual editing.
package com.plan9.intel.lang.core.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.plan9.intel.lang.core.psi.AsmIntelTypes.*;
import com.plan9.intel.lang.core.psi.*;

public class AsmIntelFunctionHeaderImpl extends AsmIntelElementImpl implements AsmIntelFunctionHeader {

  public AsmIntelFunctionHeaderImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof AsmIntelVisitor) ((AsmIntelVisitor)visitor).visitFunctionHeader(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public AsmIntelFrameSize getFrameSize() {
    return findChildByClass(AsmIntelFrameSize.class);
  }

  @Override
  @NotNull
  public AsmIntelFunctionFlags getFunctionFlags() {
    return findNotNullChildByClass(AsmIntelFunctionFlags.class);
  }

}
