package com.goide.stubs;

import com.goide.psi.GoMethodDeclaration;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

public class GoMethodDeclarationStub extends GoFunctionOrMethodDeclarationStub<GoMethodDeclaration> {
  private final StringRef myTypeName;

  public GoMethodDeclarationStub(StubElement parent, IStubElementType elementType, StringRef name, boolean isPublic, StringRef typeName) {
    super(parent, elementType, name, isPublic);
    myTypeName = typeName;
  }

  public GoMethodDeclarationStub(StubElement parent, IStubElementType elementType, String name, boolean isPublic, String typeName) {
    super(parent, elementType, name, isPublic);
    myTypeName = StringRef.fromString(typeName);
  }

  public String getTypeName() {
    return myTypeName.getString();
  }
}
