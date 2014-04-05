package com.goide.stubs.types;

import com.goide.GoLanguage;
import com.goide.psi.GoCompositeElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubBase;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class GoStubElementType<S extends StubBase<T>, T extends GoCompositeElement> extends IStubElementType<S, T> {
  public GoStubElementType(@NonNls @NotNull String debugName) {
    super(debugName, GoLanguage.INSTANCE);
  }

  @NotNull
  public String getExternalId() {
    return "go." + super.toString();
  }

  public void indexStub(@NotNull final S stub, @NotNull final IndexSink sink) {
  }
}
