package com.goide.stubs;

import com.goide.psi.GoParameters;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

public class GoParametersStub extends StubWithText<GoParameters> {
  private final StringRef myText;

  public GoParametersStub(StubElement parent, IStubElementType elementType, StringRef ref) {
    super(parent, elementType);
    myText = ref;
  }

  public GoParametersStub(StubElement parent, IStubElementType elementType, String text) {
    this(parent, elementType, StringRef.fromString(text));
  }

  public String getText() {
    return myText.getString();
  }
}
