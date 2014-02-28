package com.goide.stubs.types;

import com.goide.psi.GoMethodSpec;
import com.goide.psi.impl.GoMethodSpecImpl;
import com.goide.stubs.GoMethodSpecStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoMethodSpecStubElementType extends GoNamedStubElementType<GoMethodSpecStub, GoMethodSpec> {
  public static final GoMethodSpec[] EMPTY_ARRAY = new GoMethodSpec[0];

  public static final ArrayFactory<GoMethodSpec> ARRAY_FACTORY = new ArrayFactory<GoMethodSpec>() {
    @NotNull
    @Override
    public GoMethodSpec[] create(final int count) {
      return count == 0 ? EMPTY_ARRAY : new GoMethodSpec[count];
    }
  };

  public GoMethodSpecStubElementType(@NotNull String name) {
    super(name);
  }

  @Override
  public GoMethodSpec createPsi(@NotNull GoMethodSpecStub stub) {
    return new GoMethodSpecImpl(stub, this);
  }

  @Override
  public GoMethodSpecStub createStub(@NotNull GoMethodSpec psi, StubElement parentStub) {
    return new GoMethodSpecStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoMethodSpecStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoMethodSpecStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoMethodSpecStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }
}
