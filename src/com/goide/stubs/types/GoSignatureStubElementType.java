package com.goide.stubs.types;

import com.goide.psi.GoSignature;
import com.goide.psi.impl.GoSignatureImpl;
import com.goide.stubs.GoSignatureStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoSignatureStubElementType extends GoStubElementType<GoSignatureStub, GoSignature> {
  public GoSignatureStubElementType(@NotNull String name) {
    super(name);
  }

  @Override
  public GoSignature createPsi(@NotNull GoSignatureStub stub) {
    return new GoSignatureImpl(stub, this);
  }

  @Override
  public GoSignatureStub createStub(@NotNull GoSignature psi, StubElement parentStub) {
    return new GoSignatureStub(parentStub, this);
  }

  @Override
  public void serialize(@NotNull GoSignatureStub stub, @NotNull StubOutputStream dataStream) throws IOException {
  }

  @NotNull
  @Override
  public GoSignatureStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoSignatureStub(parentStub, this);
  }
}
