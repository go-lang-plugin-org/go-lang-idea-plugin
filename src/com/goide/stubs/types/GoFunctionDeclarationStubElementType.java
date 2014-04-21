package com.goide.stubs.types;

import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoNamedElement;
import com.goide.psi.impl.GoFunctionDeclarationImpl;
import com.goide.stubs.GoFunctionDeclarationStub;
import com.goide.stubs.index.GoFunctionIndex;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.ArrayFactory;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class GoFunctionDeclarationStubElementType extends GoNamedStubElementType<GoFunctionDeclarationStub, GoFunctionDeclaration> {
  public static final GoFunctionDeclaration[] EMPTY_ARRAY = new GoFunctionDeclaration[0];

  public static final ArrayFactory<GoFunctionDeclaration> ARRAY_FACTORY = new ArrayFactory<GoFunctionDeclaration>() {
    @NotNull
    @Override
    public GoFunctionDeclaration[] create(int count) {
      return count == 0 ? EMPTY_ARRAY : new GoFunctionDeclaration[count];
    }
  };
  
  private static final ArrayList<StubIndexKey<String, ? extends GoNamedElement>> EXTRA_KEYS =
    ContainerUtil.<StubIndexKey<String, ? extends GoNamedElement>>newArrayList(GoFunctionIndex.KEY);

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

  @NotNull
  @Override
  protected Collection<StubIndexKey<String, ? extends GoNamedElement>> getExtraIndexKeys() {
    return EXTRA_KEYS;
  }
}
