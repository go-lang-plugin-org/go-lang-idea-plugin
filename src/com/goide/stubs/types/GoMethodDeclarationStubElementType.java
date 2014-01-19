package com.goide.stubs.types;

import com.goide.psi.GoMethodDeclaration;
import com.goide.psi.impl.GoMethodDeclarationImpl;
import com.goide.stubs.GoMethodDeclarationStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoMethodDeclarationStubElementType extends GoNamedStubElementType<GoMethodDeclarationStub, GoMethodDeclaration> {
  public GoMethodDeclarationStubElementType(@NotNull String name) {
    super(name);
  }

  @Override
  public GoMethodDeclaration createPsi(@NotNull GoMethodDeclarationStub stub) {
    return new GoMethodDeclarationImpl(stub, this);
  }

  @Override
  public GoMethodDeclarationStub createStub(@NotNull GoMethodDeclaration psi, StubElement parentStub) {
    return new GoMethodDeclarationStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoMethodDeclarationStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoMethodDeclarationStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoMethodDeclarationStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }
}
