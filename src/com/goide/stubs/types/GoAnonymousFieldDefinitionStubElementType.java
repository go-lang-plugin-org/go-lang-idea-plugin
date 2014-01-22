package com.goide.stubs.types;

import com.goide.psi.GoAnonymousFieldDefinition;
import com.goide.psi.impl.GoAnonymousFieldDefinitionImpl;
import com.goide.stubs.GoAnonymousFieldDefinitionStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoAnonymousFieldDefinitionStubElementType extends GoNamedStubElementType<GoAnonymousFieldDefinitionStub, GoAnonymousFieldDefinition> {
  public GoAnonymousFieldDefinitionStubElementType(@NotNull String name) {
    super(name);
  }

  @Override
  public GoAnonymousFieldDefinition createPsi(@NotNull GoAnonymousFieldDefinitionStub stub) {
    return new GoAnonymousFieldDefinitionImpl(stub, this);
  }

  @Override
  public GoAnonymousFieldDefinitionStub createStub(@NotNull GoAnonymousFieldDefinition psi, StubElement parentStub) {
    return new GoAnonymousFieldDefinitionStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoAnonymousFieldDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoAnonymousFieldDefinitionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoAnonymousFieldDefinitionStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }
}
