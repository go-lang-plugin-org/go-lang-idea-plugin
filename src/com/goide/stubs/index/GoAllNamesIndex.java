package com.goide.stubs.index;

import com.goide.psi.GoNamedElement;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

public class GoAllNamesIndex extends StringStubIndexExtension<GoNamedElement> {
  public static final StubIndexKey<String, GoNamedElement> ALL_NAMES = StubIndexKey.createIndexKey("go.all.name");

  @Override
  public int getVersion() {
    return 10;
  }

  @NotNull
  @Override
  public StubIndexKey<String, GoNamedElement> getKey() {
    return ALL_NAMES;
  }
}
