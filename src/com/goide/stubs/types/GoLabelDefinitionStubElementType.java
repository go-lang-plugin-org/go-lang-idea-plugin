package com.goide.stubs.types;

import com.goide.psi.GoLabelDefinition;
import com.goide.psi.impl.GoLabelDefinitionImpl;
import com.goide.stubs.GoLabelDefinitionStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoLabelDefinitionStubElementType extends GoNamedStubElementType<GoLabelDefinitionStub, GoLabelDefinition> {
  public GoLabelDefinitionStubElementType(@NotNull String name) {
    super(name);
  }

  @NotNull
  @Override
  public GoLabelDefinition createPsi(@NotNull GoLabelDefinitionStub stub) {
    return new GoLabelDefinitionImpl(stub, this);
  }

  @NotNull
  @Override
  public GoLabelDefinitionStub createStub(@NotNull GoLabelDefinition psi, StubElement parentStub) {
    return new GoLabelDefinitionStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoLabelDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoLabelDefinitionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoLabelDefinitionStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }
}
