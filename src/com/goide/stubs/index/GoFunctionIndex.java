package com.goide.stubs.index;

import com.goide.psi.GoNamedElement;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

public class GoFunctionIndex extends StringStubIndexExtension<GoNamedElement> {
  public static final StubIndexKey<String, GoNamedElement> KEY = StubIndexKey.createIndexKey("go.function");

  @Override
  public int getVersion() {
    return 2;
  }

  @NotNull
  @Override
  public StubIndexKey<String, GoNamedElement> getKey() {
    return KEY;
  }
}
