package com.goide.stubs.types;

import com.goide.psi.GoReceiver;
import com.goide.psi.impl.GoReceiverImpl;
import com.goide.stubs.GoReceiverStub;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoReceiverStubElementType extends GoNamedStubElementType<GoReceiverStub, GoReceiver> {
  public GoReceiverStubElementType(@NotNull String name) {
    super(name);
  }

  @Override
  public GoReceiver createPsi(@NotNull GoReceiverStub stub) {
    return new GoReceiverImpl(stub, this);
  }

  @Override
  public GoReceiverStub createStub(@NotNull GoReceiver psi, StubElement parentStub) {
    return new GoReceiverStub(parentStub, this, psi.getName(), psi.isPublic());
  }

  @Override
  public void serialize(@NotNull GoReceiverStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
    dataStream.writeBoolean(stub.isPublic());
  }

  @NotNull
  @Override
  public GoReceiverStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new GoReceiverStub(parentStub, this, dataStream.readName(), dataStream.readBoolean());
  }

  @Override
  protected boolean shouldIndex() {
    return false;
  }
}
