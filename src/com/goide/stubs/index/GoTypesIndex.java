package com.goide.stubs.index;

import com.goide.psi.GoTypeSpec;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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

  @NotNull
  public static Collection<GoTypeSpec> find(@NotNull String name, @NotNull Project project, GlobalSearchScope scope) {
    return StubIndex.getElements(KEY, name, project, scope, GoTypeSpec.class);
  }
}
