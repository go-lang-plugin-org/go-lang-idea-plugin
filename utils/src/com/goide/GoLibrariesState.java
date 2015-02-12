package com.goide;

import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class GoLibrariesState {
  @NotNull private Collection<String> myPaths = ContainerUtil.newArrayList();

  @NotNull
  public Collection<String> getPaths() {
    return myPaths;
  }

  public void setPaths(@NotNull Collection<String> urls) {
    myPaths = urls;
  }
}
