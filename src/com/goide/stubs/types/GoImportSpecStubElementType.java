package com.goide.stubs.types;

import com.goide.psi.GoImportSpec;
import com.goide.psi.impl.GoImportSpecImpl;
import com.goide.stubs.GoImportSpecStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoImportSpecStubElementType extends GoNamedStubElementType<GoImportSpecStub, GoImportSpec> {
  public GoImportSpecStubElementType(@NotNull String name) {
    super(name);
  }

  @NotNull
  @Override
  public GoImportSpec createPsi(@NotNull GoImportSpecStub stub) {
    return new GoImportSpecImpl(stub, this);
  }

  @NotNull
  @Override
  public GoImportSpecStub createStub(@NotNull GoImportSpec psi, StubElement parentStub) {
    return new GoImportSpecStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoImportSpecStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoImportSpecStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoImportSpecStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }
}
