package com.goide.stubs.index;

import com.goide.psi.GoFunctionDeclaration;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

public class GoFunctionNameIndex extends StringStubIndexExtension<GoFunctionDeclaration> {
  public static final StubIndexKey<String, GoFunctionDeclaration> KEY = StubIndexKey.createIndexKey("go.function.names");

  @Override
  public int getVersion() {
    return 2;
  }

  @NotNull
  public StubIndexKey<String, GoFunctionDeclaration> getKey() {
    return KEY;
  }
}
