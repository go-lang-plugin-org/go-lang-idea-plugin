package com.goide.stubs.types;

import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoNamedElement;
import com.goide.psi.impl.GoFunctionDeclarationImpl;
import com.goide.stubs.GoFunctionDeclarationStub;
import com.goide.stubs.index.GoFunctionIndex;
import com.intellij.psi.stubs.*;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.generate.tostring.util.StringUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class GoFunctionDeclarationStubElementType extends GoNamedStubElementType<GoFunctionDeclarationStub, GoFunctionDeclaration> {
  private static final ArrayList<StubIndexKey<String, GoNamedElement>> EXTRA_KEYS = ContainerUtil.newArrayList(GoFunctionIndex.KEY);

  public GoFunctionDeclarationStubElementType(@NotNull String name) {
    super(name);
  }

  @Override
  public GoFunctionDeclaration createPsi(@NotNull GoFunctionDeclarationStub stub) {
    return new GoFunctionDeclarationImpl(stub, this);
  }

  @Override
  public GoFunctionDeclarationStub createStub(@NotNull GoFunctionDeclaration psi, StubElement parentStub) {
    return new GoFunctionDeclarationStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoFunctionDeclarationStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoFunctionDeclarationStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoFunctionDeclarationStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }

  @Override
  public void indexStub(@NotNull GoFunctionDeclarationStub stub, @NotNull IndexSink sink) {
    super.indexStub(stub, sink);

    String name = stub.getName();
    if (StringUtil.isNotEmpty(name)) {
      //noinspection ConstantConditions
      sink.occurrence(GoFunctionIndex.KEY, name);
    }
  }

  @NotNull
  @Override
  protected Collection<StubIndexKey<String, GoNamedElement>> getExtraIndexKeys() {
    return EXTRA_KEYS;
  }
}
