package com.goide.stubs.index;

import com.goide.psi.GoFunctionDeclaration;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
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

  public static Collection<GoFunctionDeclaration> find(String name, Project project, GlobalSearchScope scope) {
    return StubIndex.getElements(KEY, name, project, scope, GoFunctionDeclaration.class);
  }

  public static Collection<String> findAllNames(final Project project) {
    return ApplicationManager.getApplication().runReadAction(new Computable<Collection<String>>() {
      public Collection<String> compute() {
        return StubIndex.getInstance().getAllKeys(KEY, project);
      }
    });
  }
}
