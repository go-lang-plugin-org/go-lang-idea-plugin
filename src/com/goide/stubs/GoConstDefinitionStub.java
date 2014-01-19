package com.goide.stubs;

import com.goide.psi.GoConstDefinition;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

public class GoConstDefinitionStub extends GoNamedStub<GoConstDefinition> {
  public GoConstDefinitionStub(StubElement parent, IStubElementType elementType, StringRef name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }

  public GoConstDefinitionStub(StubElement parent, IStubElementType elementType, String name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }
}
