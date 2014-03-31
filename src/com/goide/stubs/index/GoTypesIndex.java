package com.goide.stubs.index;

import com.goide.psi.GoNamedElement;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

public class GoTypesIndex extends StringStubIndexExtension<GoNamedElement> {
  public static final StubIndexKey<String, GoNamedElement> KEY = StubIndexKey.createIndexKey("go.type.name");

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
