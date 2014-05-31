package com.goide.stubs.types;

import com.goide.psi.GoFieldDefinition;
import com.goide.psi.impl.GoFieldDefinitionImpl;
import com.goide.stubs.GoFieldDefinitionStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoFieldDefinitionStubElementType extends GoNamedStubElementType<GoFieldDefinitionStub, GoFieldDefinition> {
  public GoFieldDefinitionStubElementType(@NotNull String name) {
    super(name);
  }

  @NotNull
  @Override
  public GoFieldDefinition createPsi(@NotNull GoFieldDefinitionStub stub) {
    return new GoFieldDefinitionImpl(stub, this);
  }

  @NotNull
  @Override
  public GoFieldDefinitionStub createStub(@NotNull GoFieldDefinition psi, StubElement parentStub) {
    return new GoFieldDefinitionStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoFieldDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoFieldDefinitionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoFieldDefinitionStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }
}
