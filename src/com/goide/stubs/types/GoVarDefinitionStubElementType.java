package com.goide.stubs.types;

import com.goide.psi.GoVarDefinition;
import com.goide.psi.impl.GoVarDefinitionImpl;
import com.goide.stubs.GoVarDefinitionStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoVarDefinitionStubElementType extends GoNamedStubElementType<GoVarDefinitionStub, GoVarDefinition> {
  public GoVarDefinitionStubElementType(@NotNull String name) {
    super(name);
  }

  @Override
  public GoVarDefinition createPsi(@NotNull GoVarDefinitionStub stub) {
    return new GoVarDefinitionImpl(stub, this);
  }

  @Override
  public GoVarDefinitionStub createStub(@NotNull GoVarDefinition psi, StubElement parentStub) {
    return new GoVarDefinitionStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoVarDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoVarDefinitionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoVarDefinitionStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }
}
