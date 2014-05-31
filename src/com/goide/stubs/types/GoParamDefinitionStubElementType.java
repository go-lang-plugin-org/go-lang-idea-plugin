package com.goide.stubs.types;

import com.goide.psi.GoParamDefinition;
import com.goide.psi.impl.GoParamDefinitionImpl;
import com.goide.stubs.GoParamDefinitionStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoParamDefinitionStubElementType extends GoNamedStubElementType<GoParamDefinitionStub, GoParamDefinition> {
  public GoParamDefinitionStubElementType(@NotNull String name) {
    super(name);
  }

  @NotNull
  @Override
  public GoParamDefinition createPsi(@NotNull GoParamDefinitionStub stub) {
    return new GoParamDefinitionImpl(stub, this);
  }

  @NotNull
  @Override
  public GoParamDefinitionStub createStub(@NotNull GoParamDefinition psi, StubElement parentStub) {
    return new GoParamDefinitionStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoParamDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoParamDefinitionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoParamDefinitionStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }

  @Override
  protected boolean shouldIndex() {
    return false;
  }
}
