package com.goide.stubs.index;

import com.goide.psi.GoFunctionDeclaration;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class GoFunctionIndex extends StringStubIndexExtension<GoFunctionDeclaration> {
  public static final StubIndexKey<String, GoFunctionDeclaration> KEY = StubIndexKey.createIndexKey("go.function");

  @Override
  public int getVersion() {
    return 3;
  }

  @NotNull
  @Override
  public StubIndexKey<String, GoFunctionDeclaration> getKey() {
    return KEY;
  }

  public static Collection<GoFunctionDeclaration> find(@NotNull String name, @NotNull Project project, GlobalSearchScope scope) {
    return StubIndex.getElements(KEY, name, project, scope, GoFunctionDeclaration.class);
  }
}
