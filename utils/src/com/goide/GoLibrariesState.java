package com.goide;

import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class GoLibrariesState {
  @NotNull private Collection<String> myUrls = ContainerUtil.newArrayList();

  @NotNull
  public Collection<String> getUrls() {
    return myUrls;
  }

  public void setUrls(@NotNull Collection<String> urls) {
    myUrls = urls;
  }
}
