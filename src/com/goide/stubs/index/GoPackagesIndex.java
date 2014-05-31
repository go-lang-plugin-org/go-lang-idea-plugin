package com.goide.stubs.index;

import com.goide.psi.GoFile;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class GoPackagesIndex extends StringStubIndexExtension<GoFile> {
  public static final StubIndexKey<String, GoFile> KEY = StubIndexKey.createIndexKey("go.packages");

  @Override
  public int getVersion() {
    return 2;
  }

  @NotNull
  @Override
  public StubIndexKey<String, GoFile> getKey() {
    return KEY;
  }

  public static Collection<String> getAllPackages(@NotNull final Project project) {
    return ApplicationManager.getApplication().runReadAction(new Computable<Collection<String>>() {
      @NotNull
      public Collection<String> compute() {
        return StubIndex.getInstance().getAllKeys(KEY, project);
      }
    });
  }
}
