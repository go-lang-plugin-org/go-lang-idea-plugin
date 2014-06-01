package com.goide.stubs.index;

import com.goide.psi.GoNamedElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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

  @NotNull
  public static Collection<GoNamedElement> find(@NotNull String name, @NotNull Project project, GlobalSearchScope scope) {
    return StubIndex.getElements(KEY, name, project, scope, GoNamedElement.class);
  }
}
