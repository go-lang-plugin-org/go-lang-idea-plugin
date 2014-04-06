package com.goide.stubs;

import com.goide.psi.GoSignature;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;

public class GoSignatureStub extends StubWithText<GoSignature> {
  public GoSignatureStub(StubElement parent, IStubElementType elementType) {
    super(parent, elementType);
  }
}
