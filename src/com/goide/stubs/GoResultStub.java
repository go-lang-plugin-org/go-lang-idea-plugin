package com.goide.stubs;

import com.goide.psi.GoResult;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

public class GoResultStub extends StubWithText<GoResult> {
  public GoResultStub(StubElement parent, IStubElementType elementType, StringRef ref) {
    super(parent, elementType, ref);
  }

  public GoResultStub(StubElement parent, IStubElementType elementType, String text) {
    this(parent, elementType, StringRef.fromString(text));
  }
}
