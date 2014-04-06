package com.goide.stubs.types;

import com.goide.psi.GoParameterDeclaration;
import com.goide.psi.impl.GoParameterDeclarationImpl;
import com.goide.stubs.GoParameterDeclarationStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoParameterDeclarationStubElementType extends GoStubElementType<GoParameterDeclarationStub, GoParameterDeclaration> {
  public GoParameterDeclarationStubElementType(@NotNull String name) {
    super(name);
  }

  @Override
  public GoParameterDeclaration createPsi(@NotNull GoParameterDeclarationStub stub) {
    return new GoParameterDeclarationImpl(stub, this);
  }

  @Override
  public GoParameterDeclarationStub createStub(@NotNull GoParameterDeclaration psi, StubElement parentStub) {
    return new GoParameterDeclarationStub(parentStub, this);
  }

  @Override
  public void serialize(@NotNull GoParameterDeclarationStub stub, @NotNull StubOutputStream dataStream) throws IOException {
  }

  @NotNull
  @Override
  public GoParameterDeclarationStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoParameterDeclarationStub(parentStub, this);
  }
}
