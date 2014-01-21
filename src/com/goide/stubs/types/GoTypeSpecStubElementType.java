package com.goide.stubs.types;

import com.goide.psi.GoNamedElement;
import com.goide.psi.GoTypeSpec;
import com.goide.psi.impl.GoTypeSpecImpl;
import com.goide.stubs.GoTypeSpecStub;
import com.goide.stubs.index.GoTypesIndex;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.ArrayFactory;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;

public class GoTypeSpecStubElementType extends GoNamedStubElementType<GoTypeSpecStub, GoTypeSpec> {
  public static final GoTypeSpec[] EMPTY_ARRAY = new GoTypeSpec[0];

  public static final ArrayFactory<GoTypeSpec> ARRAY_FACTORY = new ArrayFactory<GoTypeSpec>() {
    @NotNull
    @Override
    public GoTypeSpec[] create(final int count) {
      return count == 0 ? EMPTY_ARRAY : new GoTypeSpec[count];
    }
  };
  
  public GoTypeSpecStubElementType(@NotNull String name) {
    super(name);
  }

  @Override
  public GoTypeSpec createPsi(@NotNull GoTypeSpecStub stub) {
    return new GoTypeSpecImpl(stub, this);
  }

  @Override
  public GoTypeSpecStub createStub(@NotNull GoTypeSpec psi, StubElement parentStub) {
    return new GoTypeSpecStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoTypeSpecStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoTypeSpecStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoTypeSpecStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }

  @NotNull
  @Override
  protected Collection<StubIndexKey<String, ? extends GoNamedElement>> getExtraIndexKeys() {
    return ContainerUtil.<StubIndexKey<String, ? extends GoNamedElement>>list(GoTypesIndex.KEY);
  }
}
