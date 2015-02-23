package com.goide.sdk;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoSmallIDEsSdkService extends GoSdkService {
  @Override
  public String getSdkHomePath(@NotNull Module module) {
    return null;
  }

  @Nullable
  @Override
  public String getSdkHomePath(@NotNull Project project) {
    return null;
  }

  @Nullable
  @Override
  public String getSdkVersion(@NotNull Module module) {
    return null;
  }

  @Nullable
  @Override
  public String getSdkVersion(@NotNull Project project) {
    return null;
  }

  @Override
  public void chooseAndSetSdk(@NotNull Project project, @Nullable Module module) {

  }
}
