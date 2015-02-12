package com.goide.jps.model;

import com.goide.GoLibrariesState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JpsGoModuleProperties {
  @NotNull
  private GoLibrariesState myLibrariesState = new GoLibrariesState();

  @NotNull
  public GoLibrariesState getLibrariesState() {
    return myLibrariesState;
  }

  public void setLibrariesState(@Nullable GoLibrariesState librariesState) {
    myLibrariesState = librariesState != null ? librariesState : new GoLibrariesState();
  }
}
