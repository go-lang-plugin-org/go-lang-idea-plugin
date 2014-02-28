package com.goide.stubs.index;

import com.goide.psi.GoTypeSpec;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

public class GoTypesIndex extends StringStubIndexExtension<GoTypeSpec> {
  public static final StubIndexKey<String, GoTypeSpec> KEY = StubIndexKey.createIndexKey("go.type.name");

  @Override
  public int getVersion() {
    return 2;
  }

  @NotNull
  @Override
  public StubIndexKey<String, GoTypeSpec> getKey() {
    return KEY;
  }
}
