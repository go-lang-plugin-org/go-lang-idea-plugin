package com.goide.stubs;

import com.goide.psi.GoConstDefinition;
import com.goide.psi.GoTypeSpec;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

public class GoTypeSpecStub extends GoNamedStub<GoTypeSpec> {
  public GoTypeSpecStub(StubElement parent, IStubElementType elementType, StringRef name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }

  public GoTypeSpecStub(StubElement parent, IStubElementType elementType, String name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }
}
