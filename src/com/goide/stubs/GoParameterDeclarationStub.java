package com.goide.stubs;

import com.goide.psi.GoParameterDeclaration;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;

public class GoParameterDeclarationStub extends StubBase<GoParameterDeclaration> {
  public GoParameterDeclarationStub(StubElement parent, IStubElementType elementType) {
    super(parent, elementType);
  }
}
