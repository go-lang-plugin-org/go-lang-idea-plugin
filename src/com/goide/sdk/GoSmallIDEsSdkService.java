package com.goide.sdk;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoSmallIDEsSdkService extends GoSdkService {
  public GoSmallIDEsSdkService(@NotNull Project project) {
    super(project);
  }

  @Nullable
  @Override
  public String getSdkHomePath(@Nullable Module module) {
    return null;
  }

  @Nullable
  @Override
  public String getSdkVersion(@Nullable Module module) {
    return null;
  }

  @Override
  public void chooseAndSetSdk(@Nullable Module module) {

  }
}
