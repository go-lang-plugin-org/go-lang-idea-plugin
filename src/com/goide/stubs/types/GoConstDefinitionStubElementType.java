package com.goide.stubs.types;

import com.goide.psi.GoConstDefinition;
import com.goide.psi.impl.GoConstDefinitionImpl;
import com.goide.stubs.GoConstDefinitionStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoConstDefinitionStubElementType extends GoNamedStubElementType<GoConstDefinitionStub, GoConstDefinition> {
  public GoConstDefinitionStubElementType(@NotNull String name) {
    super(name);
  }

  @Override
  public GoConstDefinition createPsi(@NotNull GoConstDefinitionStub stub) {
    return new GoConstDefinitionImpl(stub, this);
  }

  @Override
  public GoConstDefinitionStub createStub(@NotNull GoConstDefinition psi, StubElement parentStub) {
    return new GoConstDefinitionStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoConstDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoConstDefinitionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoConstDefinitionStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }
}
