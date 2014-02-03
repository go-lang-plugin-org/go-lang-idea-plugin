package com.goide.stubs;

import com.goide.psi.GoMethodSpec;
import com.goide.psi.GoTypeSpec;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

public class GoMethodSpecStub extends GoNamedStub<GoMethodSpec> {
  public GoMethodSpecStub(StubElement parent, IStubElementType elementType, StringRef name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }

  public GoMethodSpecStub(StubElement parent, IStubElementType elementType, String name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }
}
