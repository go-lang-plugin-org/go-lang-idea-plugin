package com.goide.stubs;

import com.goide.psi.GoNamedElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.Nullable;

abstract public class GoNamedStub<T extends GoNamedElement> extends NamedStubBase<T> implements TextHolder {
  private final boolean myIsPublic;

  public GoNamedStub(StubElement parent, IStubElementType elementType, StringRef name, boolean isPublic) {
    super(parent, elementType, name);
    myIsPublic = isPublic;
  }

  public GoNamedStub(StubElement parent, IStubElementType elementType, String name, boolean isPublic) {
    super(parent, elementType, name);
    myIsPublic = isPublic;
  }

  public boolean isPublic() {
    return myIsPublic;
  }

  @Nullable
  @Override
  public String getText() {
    return null;
  }
}
