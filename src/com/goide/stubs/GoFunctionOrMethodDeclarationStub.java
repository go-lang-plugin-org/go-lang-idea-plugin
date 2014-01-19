package com.goide.stubs;

import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

abstract public class GoFunctionOrMethodDeclarationStub<T extends GoFunctionOrMethodDeclaration> extends GoNamedStub<T> {
  public GoFunctionOrMethodDeclarationStub(StubElement parent, IStubElementType elementType, StringRef name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }

  public GoFunctionOrMethodDeclarationStub(StubElement parent, IStubElementType elementType, String name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }
}
