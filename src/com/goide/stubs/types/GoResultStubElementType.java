package com.goide.stubs.types;

import com.goide.psi.GoResult;
import com.goide.psi.impl.GoResultImpl;
import com.goide.stubs.GoResultStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoResultStubElementType extends GoStubElementType<GoResultStub, GoResult> {
  public GoResultStubElementType(@NotNull String name) {
    super(name);
  }

  @NotNull
  @Override
  public GoResult createPsi(@NotNull GoResultStub stub) {
    return new GoResultImpl(stub, this);
  }

  @NotNull
  @Override
  public GoResultStub createStub(@NotNull GoResult psi, StubElement parentStub) {
    return new GoResultStub(parentStub, this, psi.getText());
  }

  @Override
  public void serialize(@NotNull GoResultStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getText());
  }

  @NotNull
  @Override
  public GoResultStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoResultStub(parentStub, this, dataStream.readName());
  }
}
