package com.goide.stubs;

import com.goide.psi.impl.GoFunctionDeclarationImpl;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

public class GoFunctionDeclarationStub extends NamedStubBase<GoFunctionDeclarationImpl> {
  private final boolean myIsPublic;

  public GoFunctionDeclarationStub(StubElement parent, IStubElementType elementType, StringRef name, boolean isPublic) {
    super(parent, elementType, name);
    myIsPublic = isPublic;
  }

  public GoFunctionDeclarationStub(StubElement parent, IStubElementType elementType, String name, boolean isPublic) {
    super(parent, elementType, name);
    myIsPublic = isPublic;
  }

  public boolean isPublic() {
    return myIsPublic;
  }
}
