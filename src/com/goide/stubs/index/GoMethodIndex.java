package com.goide.stubs.index;

import com.goide.psi.GoMethodDeclaration;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class GoMethodIndex extends StringStubIndexExtension<GoMethodDeclaration> {
  public static final StubIndexKey<String, GoMethodDeclaration> KEY = StubIndexKey.createIndexKey("go.method");

  @Override
  public int getVersion() {
    return 1;
  }

  @NotNull
  @Override
  public StubIndexKey<String, GoMethodDeclaration> getKey() {
    return KEY;
  }

  public static Collection<GoMethodDeclaration> find(@NotNull String name, @NotNull Project project, GlobalSearchScope scope) {
    return StubIndex.getElements(KEY, name, project, scope, GoMethodDeclaration.class);
  }
}
