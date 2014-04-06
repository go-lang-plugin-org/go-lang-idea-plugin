package com.goide.stubs;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.Nullable;

abstract public class StubWithText<T extends PsiElement> extends StubBase<T> implements TextHolder {
  protected StubWithText(StubElement parent, IStubElementType elementType) {
    super(parent, elementType);
  }
  
  @Nullable
  public String getText() {
    return null;
  }
}