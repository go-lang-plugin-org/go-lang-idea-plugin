package com.goide.stubs;

import com.goide.psi.GoVarDefinition;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

public class GoVarDefinitionStub extends GoNamedStub<GoVarDefinition> {
  public GoVarDefinitionStub(StubElement parent, IStubElementType elementType, StringRef name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }

  public GoVarDefinitionStub(StubElement parent, IStubElementType elementType, String name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }
}
