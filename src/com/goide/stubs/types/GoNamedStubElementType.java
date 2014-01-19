package com.goide.stubs.types;

import com.goide.GoLanguage;
import com.goide.psi.GoNamedElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.NamedStubBase;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class GoNamedStubElementType<S extends NamedStubBase<T>, T extends GoNamedElement> extends IStubElementType<S, T> {
  public GoNamedStubElementType(@NonNls @NotNull String debugName) {
    super(debugName, GoLanguage.INSTANCE);
  }

  @NotNull
  public String getExternalId() {
    return "go." + super.toString();
  }
}
