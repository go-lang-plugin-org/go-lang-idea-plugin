package com.goide.stubs;

import com.goide.psi.GoLabelDefinition;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

public class GoLabelDefinitionStub extends GoNamedStub<GoLabelDefinition> {
  public GoLabelDefinitionStub(StubElement parent, IStubElementType elementType, StringRef name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }

  public GoLabelDefinitionStub(StubElement parent, IStubElementType elementType, String name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }
}
