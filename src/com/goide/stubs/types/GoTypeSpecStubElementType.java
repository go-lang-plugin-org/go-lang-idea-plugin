package com.goide.stubs.types;

import com.goide.psi.GoTypeSpec;
import com.goide.psi.impl.GoTypeSpecImpl;
import com.goide.stubs.GoTypeSpecStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoTypeSpecStubElementType extends GoNamedStubElementType<GoTypeSpecStub, GoTypeSpec> {
  public GoTypeSpecStubElementType(@NotNull String name) {
    super(name);
  }

  @Override
  public GoTypeSpec createPsi(@NotNull GoTypeSpecStub stub) {
    return new GoTypeSpecImpl(stub, this);
  }

  @Override
  public GoTypeSpecStub createStub(@NotNull GoTypeSpec psi, StubElement parentStub) {
    return new GoTypeSpecStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoTypeSpecStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoTypeSpecStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoTypeSpecStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }
}
