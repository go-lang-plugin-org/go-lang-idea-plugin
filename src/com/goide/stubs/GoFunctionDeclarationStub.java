package com.goide.stubs;

import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.impl.GoFunctionDeclarationImpl;
import com.goide.psi.impl.GoMethodDeclarationImpl;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

public class GoFunctionDeclarationStub extends GoFunctionOrMethodDeclarationStub<GoFunctionDeclaration> {
  public GoFunctionDeclarationStub(StubElement parent, IStubElementType elementType, StringRef name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }

  public GoFunctionDeclarationStub(StubElement parent, IStubElementType elementType, String name, boolean isPublic) {
    super(parent, elementType, name, isPublic);
  }
}
