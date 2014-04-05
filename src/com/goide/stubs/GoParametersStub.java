package com.goide.stubs;

import com.goide.psi.GoParameters;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;

public class GoParametersStub extends StubBase<GoParameters> {
  public GoParametersStub(StubElement parent, IStubElementType elementType) {
    super(parent, elementType);
  }
}
