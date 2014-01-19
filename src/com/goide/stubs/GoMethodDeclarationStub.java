package com.goide.stubs;

import com.goide.psi.GoMethodDeclaration;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

public class GoMethodDeclarationStub extends GoFunctionOrMethodDeclarationStub<GoMethodDeclaration> {
  public GoMethodDeclarationStub(StubElement parent, IStubElementType elementType, StringRef name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }

  public GoMethodDeclarationStub(StubElement parent, IStubElementType elementType, String name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }
}
